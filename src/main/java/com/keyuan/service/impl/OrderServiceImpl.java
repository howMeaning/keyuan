package com.keyuan.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.keyuan.dto.Result;
import com.keyuan.dto.UserDTO;
import com.keyuan.entity.Order;
import com.keyuan.mapper.OrderMapper;
import com.keyuan.service.IGoodService;
import com.keyuan.service.IOrderService;
import com.keyuan.utils.*;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.LongAdder;

import static com.keyuan.utils.RabbitContent.EXCHANGE_NAME;
import static com.keyuan.utils.RabbitContent.NORMAL_ROUTING_KEY;
import static com.keyuan.utils.RedisContent.CACHE_ORDERNAME;
import static com.keyuan.utils.RedisContent.LOCKKEY;

/**
*消息队列思路:
 *  首先需要定义一个正常队列和一个正常交换机  死信队列和死信交换机 只有一个消费者消费,第二个消费者应当是另一个功能
*    当时间超过了20分钟,则将正常队列的消息转移到死信队列
 *    这里有好几个功能点:
 *      1.确认消息队列中的指定的消息
 *          实现方式:需要手动确认
 *           生成订单:全局唯一id的生成,Random的生成这个过程在生成消息的过程后需要得到deliverTag,这个是消息的唯一标识,
    可以通过envelope这个类获取到获取到deliverTag过后,需要往redis当中存储,key应该是order的id,value是deliverTag
 *          deliverTag的获取有两种方式:
 *              一种就是Deliver这个类获取 (这种不可取)
 *              一种是通过CorrelationData的方式获取
 *              long  deliveryTag = correlationData.getReturned().getMessage().getMessageProperties().getDeliveryTag();
 *                  消息的可靠性传递 查看https://blog.csdn.net/weixin_45466462/article/details/114654992
 *           确认订单:这个时候有钱数的时候,改变状态变成400,表示支付成功,这个时候需要将找指定的redis,然后redis当中返回的
 *   deliverTag往消息队列当中找,然后ack(deliverTag),这里redis还要存finishTime,完成支付的时间,和创建订单的时间,用Hash存
 *           注意细节:这个时候有判断,消息队列没有消息或者finishTime-paytime超过20秒则表示订单失败
 *          这里应当也有一个消息的可靠性传递的过程,应该写一个外部的工具类
*
 *     2.确认消息过后插入数据到数据库和删除缓存
 *          这里的修改主要改的是数据库和缓存的销量
 *          缓存的方式是set数据结构:key应该是order的id,value应该是数据库的soleNum
 *          这里应该要注入GoodService,然后在那里写销量,这里应该要用乐观锁,来进行++操作
*     `     更改完过后需要实现缓存更新策略:删除缓存
 *          什么时候进行缓存的更新,当下次查询数据库的时候再进行缓存的更新
 *          这里需要根据指定的good
 *         注意:这里的功能需要单独的写一个方法块,还需要有一个@Transactional注解,然后该注解下需要用proxy的方法调用
*     3.将消息通过websockect传给前端
 *     如果订单消费成功和销量修改成功,将订单信息存储到webSockect当中,然后通过webSocket传给指定的前端
 *     注意:数据库的Order订单下应该有good的id
 *         每个商品下应该有评论功能  comment  和 备注
 *         数据库的的good标下应该有一个备注字段
 * @author:Administrator
*@date:2023/5/18 0:44
*@Description
*/
@Service
@Slf4j
public class OrderServiceImpl extends ServiceImpl<OrderMapper,Order>  implements IOrderService {

    @Resource
    private WebSocketServerUtil webSocketServerUtil;
    @Resource
    private RedisIDWorker redisIDWorker;

    @Resource
    private RedissonClient redissonClient;

    @Resource
    private OrderMapper orderMapper;

    @Resource
    private RedisSolve redisSolve;

    @Resource
    private  StringRedisTemplate stringRedisTemplate;

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Resource
    private IGoodService goodService;


    /**
     * 获取随机数
     * @return 随机数
     */
    private int getRandom(Long shopId){
        //每日生成4位随机单号,随机单号的生成直接用value的方式,基于每日生成一直incre就行
        //查缓存如果不存在则生成
        String randomStr = stringRedisTemplate.opsForValue().get(RedisContent.RANDOMNUMBER+shopId + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy:MM:dd")));
        if (BeanUtil.isEmpty(randomStr)) {
            Integer randomId = RandomUtil.randomInt(100, 10000);
            redisSolve.set(RedisContent.RANDOMNUMBER+ LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy:MM:dd")), randomId);
            return randomId;
        }
        //不为空直接incre就行
        Long increment = stringRedisTemplate.opsForValue().increment(RedisContent.RANDOMNUMBER+shopId + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy:MM:dd")));
        return increment.intValue();
    }


    /**
     * 生成订单,需要手动确认,发送到队列当中
     * @param order
     * @return
     */
    @Override
    public Order createOrder(Order order) {

        //全局唯一id
        long orderId = redisIDWorker.nextId("order");


        //防止重复下单操作
        RLock lock = redissonClient.getLock(LOCKKEY+orderId);



        boolean islock = lock.tryLock();

        //这里需要获取到用户的id,从ThreadLocal当中获取到
        UserDTO user = UserHolder.getUser();
        order.setUserId(user.getId());
        //随机数
        int random = getRandom(order.getShopId());

        Long  deliverTag = (Long)stringRedisTemplate.opsForHash().get(CACHE_ORDERNAME+order.getUserId(), String.valueOf(orderId));


        if (!islock || deliverTag !=null) {
            log.error("不允许重复下单或订单已存在,单号:{}",orderId);
            return null;
        }




        if (order.getPayMoney() == null){
            order.setStatus(300);
        }

        order.setId(orderId).setOrderNumber(random).setStatus(300);

        rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
            @Override
            public void confirm(CorrelationData correlationData, boolean ack, String cause) {
                //成功的函数
                int success = orderMapper.createOrder(order);
                if (success == 1){
                    log.info("数据发送成功,数据库插入成功!");
                }
            }
        });

        //发送
        rabbitTemplate.convertAndSend(EXCHANGE_NAME,NORMAL_ROUTING_KEY,JSONUtil.toJsonStr(order));
        return order;
    }

    //这里监听,将所有的deliveryTag注册到Redis
    //需要将缓存中的状态码修改
    //这里需要设置手动ack
    //确认消息之后需要表示交易完成直接改商品销量和订单状态
    @RabbitListener(queues = "normal_queue",ackMode = "MANUAL")
    public void recieveOrder(Message message, Channel channel) {
        if (message ==null){
            log.info("当前没有消息!");
        }
        String orderStr = new String(message.getBody());
        Order order = JSONUtil.toBean(orderStr, Order.class);
        //先找缓存,确认所有status是400的消息
        HashOperations<String, Object, String> stringObjectObjectHashOperations = stringRedisTemplate.opsForHash();
        String cacheStatus = stringObjectObjectHashOperations.get(CACHE_ORDERNAME+order.getUserId(), String.valueOf(order.getId()));
        if (BeanUtil.isEmpty(cacheStatus)){
            log.info("目前没有确认的消息:{}",message);
        }else if (Integer.parseInt(cacheStatus) == OrderStatus.SUCCESS_STATUS){
                try {
                    channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
                    webSocketServerUtil.sendMessage(JSONUtil.toJsonStr(order));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }


    /**
     * 这里进行确定订单,
     * 改数据库的状态,改order缓存的状态
     * 改商品的销量
     * @param order
     */
    @Override
    @Transactional
    public Result confirmOrder(Order order) {
        HashOperations<String, Object, String> stringObjectObjectHashOperations = stringRedisTemplate.opsForHash();
        String cacheStatus = stringObjectObjectHashOperations.get(CACHE_ORDERNAME+order.getUserId(), String.valueOf(order.getId()));
        //改数据库的状态和缓存状态
        if (BeanUtil.isNotEmpty(cacheStatus) && Integer.parseInt(cacheStatus) ==OrderStatus.WAITING_STATUS){
           //这里表示不为空,则直接改数据库状态,双写一致性下需要将order删除
            int result = orderMapper.updateStatusById(order.getId(), OrderStatus.SUCCESS_STATUS);
            stringObjectObjectHashOperations.delete(CACHE_ORDERNAME+order.getUserId(),order.getId());
            if (result>0){
                log.info("缓存和是数据库状态修改成功,orderId:{}",order.getId());
            }
            //这里改销量
            goodService.updateSoleNumByIds(order.getGoodId(),order.getShopId());
            return Result.ok(Integer.parseInt(cacheStatus),"确认订单成功!orderId:"+order.getId()+"");
        }else {
            //这里缓存表示为空,则查数据库
            Integer status = orderMapper.selectStatusById(order.getId());
            if (BeanUtil.isNotEmpty(status)&&status == OrderStatus.WAITING_STATUS){
                int result = orderMapper.updateStatusById(order.getId(), OrderStatus.SUCCESS_STATUS);
                if (result > 0){
                    //修改成功,删除缓存
                    stringObjectObjectHashOperations.delete(CACHE_ORDERNAME+order.getUserId(),order.getId());
                }
                goodService.updateSoleNumByIds(order.getGoodId(),order.getShopId());
                return Result.ok( OrderStatus.SUCCESS_STATUS,"确认订单成功!orderId:"+order.getId()+"");

            }else{
               return Result.fail(OrderStatus.FAIL_STATUS,"交易已完成!,或者交易已失败,orderId:{"+order.getId()+"}");
            }
        }

    }

    /**
     * 这里应该还有其他状态的判断,还是要先找数据库
     * 如果finshTime没有或者finshTime-createTime>20则表示失败 333
     * 这里的order缓存实际上还是要给个过期时间
     *
     * @param
     * @return
     */
    @Override
    public List<Order> getAllOrder()
    {

        //这里应该从本地获取到UserId

        //查缓存,缓存不存在则查数据库
        Map<Object, Object> entries = stringRedisTemplate.opsForHash().entries(CACHE_ORDERNAME);
        if (CollectionUtil.isEmpty(entries)){
            //如果是
        }
        return null;
    }




}

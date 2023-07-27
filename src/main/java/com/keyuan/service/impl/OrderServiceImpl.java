package com.keyuan.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.keyuan.dto.Result;
import com.keyuan.dto.UserDTO;
import com.keyuan.entity.Order;
import com.keyuan.entity.User;
import com.keyuan.mapper.CountMapper;
import com.keyuan.mapper.OrderMapper;
import com.keyuan.mapper.UserMapper;
import com.keyuan.service.IGoodService;
import com.keyuan.service.IOrderService;
import com.keyuan.service.IUserService;
import com.keyuan.utils.*;
import com.rabbitmq.client.Channel;
import jdk.internal.org.objectweb.asm.tree.MultiANewArrayInsnNode;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.concurrent.FailureCallback;
import org.springframework.util.concurrent.SuccessCallback;

import javax.annotation.Resource;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.keyuan.utils.RabbitContent.EXCHANGE_NAME;
import static com.keyuan.utils.RabbitContent.NORMAL_ROUTING_KEY;
import static com.keyuan.utils.RedisContent.CACHE_ORDERNAME;
import static com.keyuan.utils.RedisContent.LOCKKEY;

/**
 * 消息队列思路:
 * 首先需要定义一个正常队列和一个正常交换机  死信队列和死信交换机 只有一个消费者消费,第二个消费者应当是另一个功能
 * 当时间超过了20分钟,则将正常队列的消息转移到死信队列
 * 这里有好几个功能点:
 * 1.确认消息队列中的指定的消息
 * 实现方式:需要手动确认
 * 生成订单:全局唯一id的生成,Random的生成这个过程在生成消息的过程后需要得到deliverTag,这个是消息的唯一标识,
 * 可以通过envelope这个类获取到获取到deliverTag过后,需要往redis当中存储,key应该是order的id,value是deliverTag
 * deliverTag的获取有两种方式:
 * 一种就是Deliver这个类获取 (这种不可取)
 * 一种是通过CorrelationData的方式获取
 * long  deliveryTag = correlationData.getReturned().getMessage().getMessageProperties().getDeliveryTag();
 * 消息的可靠性传递 查看https://blog.csdn.net/weixin_45466462/article/details/114654992
 * 确认订单:这个时候有钱数的时候,改变状态变成400,表示支付成功,这个时候需要将找指定的redis,然后redis当中返回的
 * deliverTag往消息队列当中找,然后ack(deliverTag),这里redis还要存finishTime,完成支付的时间,和创建订单的时间,用Hash存
 * 注意细节:这个时候有判断,消息队列没有消息或者finishTime-paytime超过20秒则表示订单失败
 * 这里应当也有一个消息的可靠性传递的过程,应该写一个外部的工具类
 * <p>
 * 2.确认消息过后插入数据到数据库和删除缓存
 * 这里的修改主要改的是数据库和缓存的销量
 * 缓存的方式是set数据结构:key应该是order的id,value应该是数据库的soleNum
 * 这里应该要注入GoodService,然后在那里写销量,这里应该要用乐观锁,来进行++操作
 * `     更改完过后需要实现缓存更新策略:删除缓存
 * 什么时候进行缓存的更新,当下次查询数据库的时候再进行缓存的更新
 * 这里需要根据指定的good
 * 注意:这里的功能需要单独的写一个方法块,还需要有一个@Transactional注解,然后该注解下需要用proxy的方法调用
 * 3.将消息通过websockect传给前端
 * 如果订单消费成功和销量修改成功,将订单信息存储到webSockect当中,然后通过webSocket传给指定的前端
 * 注意:数据库的Order订单下应该有good的id
 * 每个商品下应该有评论功能  comment  和 备注
 * 数据库的的good标下应该有一个备注字段
 *
 * @author:Administrator
 * @date:2023/5/18 0:44
 * @Description
 */
@Service
@Slf4j
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements IOrderService {

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
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private IUserService userService;

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Resource
    private IGoodService goodService;


    /**
     * 获取随机数
     *
     * @return 随机数
     */
    private int getRandom(Long shopId) {
        //每日生成4位随机单号,随机单号的生成直接用value的方式,基于每日生成一直incre就行
        //查缓存如果不存在则生成
        String randomStr = stringRedisTemplate.opsForValue().get(RedisContent.RANDOMNUMBER + shopId + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy:MM:dd")));
        if (BeanUtil.isEmpty(randomStr)) {
            Integer randomId = RandomUtil.randomInt(100, 10000);
            redisSolve.set(RedisContent.RANDOMNUMBER + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy:MM:dd")), randomId);
            return randomId;
        }
        //不为空直接incre就行
        Long increment = stringRedisTemplate.opsForValue().increment(RedisContent.RANDOMNUMBER + shopId + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy:MM:dd")));
        return increment.intValue();
    }


    /**
     *
     *这里生成订单之前需要根据订单id找缓存
     * 如果缓存当中没有或者数据库当中有
     *   则查看order当中是否存在payTime,
     *      payTime如果存在,则payTime跟createTime做对比
     *          如果时间>30分钟
     *              则找状态,如果状态是333表示当前订单已经过期,如果不是则直接改订单状态
     *          如果时间<30分钟
     *              则查找状态,如果状态是400表示当前订单已经成功,如果还是300,则将订单状态改成400
     *      paytime如果不存在,又可能取消了订单然后当前可能是又取消了,则直接根据createTime和当前时间做对比
     *          如果<30分钟,直接返回订单id,并标识剩余时间
     *          如果>30分钟,直接返回订单id,表示当前订单已过期
     * 如果缓存当中没有或者数据库当中也没有
     *   则创建数据库订单,创建缓存订单,初始化状态是300
     * @param order
     * @return
     */
    @Override
    public Result createOrder(Order order) {

        if(order.getOrderId() != null) {

            Boolean aBoolean = stringRedisTemplate.opsForHash().hasKey(RedisContent.CACHE_ORDERNAME + order.getUserId(), String.valueOf(order.getOrderId()));

            if (!aBoolean) {
                //这里说明缓存不存在
                Order newOrder = orderMapper.selectOrderById(order.getOrderId());
                if (newOrder.getOrderStatus() != 333) {
                    stringRedisTemplate.opsForHash().put(RedisContent.CACHE_ORDERNAME + order.getUserId(), String.valueOf(newOrder.getOrderId()), JSONUtil.toJsonStr(newOrder));
                }
            }
            String orderStr = (String) stringRedisTemplate.opsForHash().get(RedisContent.CACHE_ORDERNAME + order.getUserId(), String.valueOf(order.getOrderId()));
            Order newOrder = JSONUtil.toBean(orderStr, Order.class);
            LocalDateTime payTime = newOrder.getPayTime();
            if (BeanUtil.isEmpty(payTime)) {
                if (LocalDateTime.now().isBefore(newOrder.getCreateTime().plusMinutes(30))) {
                    if (newOrder.getOrderStatus() == 300) {
                        //这里返回一个剩余时间
                        long minutes = ChronoUnit.MINUTES.between(newOrder.getCreateTime(), LocalDateTime.now());
                        return Result.ok("订单已创建成功,剩余时间" + minutes + "分钟");
                    }
                }
                else {
                    if (newOrder.getOrderStatus() == 333) {
                        stringRedisTemplate.opsForHash().put(RedisContent.CACHE_ORDERNAME + order.getUserId(), String.valueOf(newOrder.getOrderId()), JSONUtil.toJsonStr(newOrder));
                        return Result.fail(333, "当前的订单已过期");
                    } else {
                        newOrder.setOrderStatus(333);
                        stringRedisTemplate.opsForHash().put(RedisContent.CACHE_ORDERNAME + order.getUserId(), String.valueOf(newOrder.getOrderId()), JSONUtil.toJsonStr(newOrder));
                        return Result.fail(333, "当前订单已过期,订单id:" + order.getOrderId() + "");
                    }
                }
            }
            return Result.ok("当前订单已完成...");
        }
        //id不存在,直接表示第一次创
        long orderId = redisIDWorker.nextId("order");

        RLock lock = redissonClient.getLock(LOCKKEY + orderId);
        boolean result = finishOrder(order, orderId, lock);
        return result ? Result.ok("创建订单成功,订单编号:" + orderId + "") : Result.fail("创建订单失败订单编号:" + orderId + "");
    }

    private boolean finishOrder(Order order, long orderId, RLock lock) {
        try {
            boolean islock = lock.tryLock();


            Long deliverTag = (Long) stringRedisTemplate.opsForHash().get(CACHE_ORDERNAME + order.getUserId(), String.valueOf(orderId));


            if (!islock || deliverTag != null) {
                log.error("不允许重复下单或订单已存在,单号:{}", orderId);
                return false;
            }

            UserDTO user = UserHolder.getUser();


            log.info("user:{}", user);
            //随机数
            int random = getRandom(order.getShopId());

            if (user == null) {
                log.warn("406,用户未授权!");
            }

            order.setUserId(user.getId())
                    .setOrderId(orderId)
                    .setOrderNumber(random)
                    .setOrderStatus(300);

            int success = orderMapper.createOrder(order);
            //创建订单,成功则返回订单
            if (success == 1) {
                //创建订单成功,需要将订单插入到缓存
                stringRedisTemplate.opsForHash().put(RedisContent.CACHE_ORDERNAME + order.getUserId(), String.valueOf(orderId), JSONUtil.toJsonStr(order));
                return true;
            }
            //这里表示创建失败
            log.error("创建订单失败,单号为:{}",order.getOrderId());
            return false;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            //释放锁
            lock.unlock();
        }
    }


    /**
     * 将获取到Order的信息传入,如果payTime不为0,则找缓存状态,如果状态是300 则查看createTime时间-payTime时间<20秒
     * 则表示当前订单没有过期,则将其改成400,发送消息,修改缓存和订单的销量
     * 如果过期了,则查看当前数据库
     * <p>
     * 如果payTime为0,则有可能是未创建订单,订单不存在
     *
     *
     * @param order
     */
    @Override
    @Transactional
    public Result confirmOrder(Order order) {
        //添加CallBack
     CorrelationData correlationData = new CorrelationData(order.getOrderId().toString());
     Message message = new Message(String.valueOf(order.getOrderId()).getBytes());
     message.getMessageProperties().setDeliveryTag(order.getOrderId());

        /**
         * 消息发送成功
         */
        correlationData.getFuture().addCallback(
                result -> log.info("订单消息发送成功!"),
                ex -> {
                    log.info("订单消息发送失败,异常信息:{}", ex);
                    //这里应该做失败重试机制
                });

        LocalDateTime payTime = order.getPayTime();
        if (!BeanUtil.isEmpty(payTime)) {
            //这里表示不为空 找缓存状态
            HashOperations<String, Object, String> stringObjectObjectHashOperations = stringRedisTemplate.opsForHash();
            UserDTO user = UserHolder.getUser();
            if (user==null){
                return Result.fail("当前用户未授权");
            }
            String orderStr = stringObjectObjectHashOperations.get(CACHE_ORDERNAME + user.getId(), String.valueOf(order.getOrderId()));
            Order cacheOrder = JSONUtil.toBean(orderStr, Order.class);

            if (!BeanUtil.isEmpty(order) && order.getOrderStatus() != null) {
                if (cacheOrder.getOrderStatus() == 300 && order.getCreateTime().plusMinutes(30).isAfter(order.getPayTime())) {
                    synchronized (order.getOrderId()) { //这里应该最好要加个锁
                        //这里表示状态是300 并且没有过期的话
                        //改状态
                        orderMapper.updateStatusById(order.getOrderId(), 400);
                        //改销量
                        goodService.updateSoleNumByIds(order.getGoodId(), order.getShopId());
                        //发消息
                        rabbitTemplate.convertAndSend(EXCHANGE_NAME,"order.normal", message, correlationData);
                        log.info("修改状态,销量成功,发送消息成功");
                        return Result.ok("订单确认成功!");
                    }
                }
                stringRedisTemplate.opsForHash().put(RedisContent.CACHE_ORDERNAME + user.getId(),  String.valueOf(order.getOrderId()), JSONUtil.toJsonStr(cacheOrder.setOrderStatus(333)));
                return Result.fail("订单已超时!");
            } else {
                //这里表示缓存状态不存在,则可能缓存不存在或者订单不存在
                return  Result.fail("订单不存在或者缓存状态不存在!");
            }
        }
        return Result.fail("当前未付款,无法确认订单...");
    }

    /**
     *
     * @param
     * @return
     */
    @Override
    public List<Order> getAllOrder() {

        //这里应该从本地获取到UserId

        //查缓存,缓存不存在则查数据库
        Map<Object, Object> entries = stringRedisTemplate.opsForHash().entries(CACHE_ORDERNAME);
        if (CollectionUtil.isEmpty(entries)) {
            //如果是
        }
        return null;
    }

    @Override
    public Order selectOrderById(Long id) {
        return orderMapper.selectOrderById(id);
    }


    //交易失败接口,需要发送一个延时消息到延时队列当中
    @Override
    public Result cancelOrder(Order order) {
        CorrelationData correlationData = new CorrelationData();
        //这里判断order是否有实付,如果有,则将起修改成400
        //这里进行修改order的状态

        //这里需要将获得到order的id,和实付
        correlationData.getFuture().addCallback(
                result -> log.info("订单拒绝队列消息发送成功!"),
                ex -> {
                    log.info("订单队列订单消息发送失败,异常信息:{}", ex);
                    //这里应该做失败重试机制
                });


        rabbitTemplate.convertAndSend(EXCHANGE_NAME,"order.dead",JSONUtil.toJsonStr(order),correlationData);
        return Result.ok("交易取消");
    }


}

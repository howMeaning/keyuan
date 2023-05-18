package com.keyuan.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.keyuan.entity.Order;
import com.keyuan.mapper.OrderMapper;
import com.keyuan.service.IOrderService;
import com.keyuan.utils.RedisContent;
import com.keyuan.utils.RedisIDWorker;
import com.keyuan.utils.RedisSolve;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.aop.framework.AopContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StreamOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.LongAdder;

/**
*
*@author:Administrator
*@date:2023/5/18 0:44
*@Description
*/
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper,Order>  implements IOrderService {
    @Resource
    private RedisIDWorker redisIDWorker;

    @Resource
    private RedissonClient redissonClient;

    @Resource
    private OrderMapper orderMapper;

    @Resource
    private RedisSolve redisSolve;

   //代理对象
    IOrderService proxy;

    private static final ExecutorService ORDER_EXECUTOR= Executors.newSingleThreadExecutor();

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    private static final DefaultRedisScript<Long> ORDER_SCRIPT;

    private final LongAdder longAdder = new LongAdder();

    static{
        ORDER_SCRIPT = new DefaultRedisScript<>();
        ORDER_SCRIPT.setLocation(new ClassPathResource("order.lua"));
        ORDER_SCRIPT.setResultType(Long.class);
    }

    @PostConstruct
    public void init(){
        /**
         * 这里的任务主要用来进行消费订单,但是不确认
         *
         */
        ORDER_EXECUTOR.submit(()->{
            while (true) {
                //从创建的时候就开始找消息队列
                //XREADGROUP GROUP group consumer [COUNT count] [BLOCK milliseconds] [NOACK] STREAMS key [key ...] ID [ID ...]
                stringRedisTemplate.opsForStream().read(Consumer.from("mygroup", "consumerA"),
                        StreamReadOptions.empty().count(2),
                        StreamOffset.create(RedisContent.STREAM_ORDERNAME, ReadOffset.lastConsumed())
                );
                //这里除了读消息队列,也应该读pending-list
                List<MapRecord<String, Object, Object>> read = stringRedisTemplate.opsForStream().read(Consumer.from("mygroup", "consumerB"),
                        StreamReadOptions.empty().count(2),
                        StreamOffset.create(RedisContent.STREAM_ORDERNAME, ReadOffset.from("0")));
                if (read == null || read.isEmpty()){
                    break;
                }
                //如果有数据则将数据传到fairHandle当中
                for (MapRecord<String, Object, Object> record : read) {
                    RecordId id = record.getId();
                    //有数据就直接删除
                    failHandle(id.getValue());
                }
            }
        });
    }


    /**
     *
     * @param order 前端传的数据
     * @return 订单插入数量
     */
    @Override
    public int createOrder(Order order){
        RLock lock = null;
        try {
            proxy= (IOrderService) AopContext.currentProxy();
            //全局唯一id的生成
            long orderId = redisIDWorker.nextId("order");

            //随机单号的生成
            int random = getRandom();

            order.setId(orderId).setOrderNumber(random);

            //这里需要防止重复下单操作
            lock = redissonClient.getLock("lock:order");
            if (!lock.tryLock()) {
                //这里表示没有获取到锁
                log.error("不能重复下单!");
            }

            //通过前端传的钱来判断状态
            if (BeanUtil.isEmpty(order.getPayMoney())){
                order.setStatus(300);
            }else{
                order.setStatus(400);
            }
            handleOrder(order);
        } finally {
            lock.unlock();
        }
        return 0;
    }
    /**
     *
     * 应该有三种订单状态
     * 333  交易失败  从ConsumerB的pending找,发现有数据,直接删除,然后还需要删除
     * 400  交易成功  从pending当中找,直接确认,然后改订单状态,改销量
     * 300  等待支付  查看是否有订单,如果有订单,避免重复消费,如果没有则创建订单,然后将订单放到消息队列当中
     *
     */
    private void handleOrder(Order order) {
        if (order.getStatus() == 400){
            //交易成功
            successHandle(order);
        }else if (order.getStatus()==300){
            //等待支付
            waitHandle(order);
        }
    }

    //400  交易成功  从pending当中找,直接确认,然后改订单状态,改销量
    private void successHandle(Order order) {
        //确认指定订单号的订单
        stringRedisTemplate.opsForStream().acknowledge(RedisContent.STREAM_ORDERNAME,"mygroup",String.valueOf(order.getId()));

        //将订单插入到数据库

        //改数据库的销量

        //改

        //改缓存订单销量
    }

    /**
     * 交易失败
     * 直接删除
     * @param value
     */
    private void failHandle(String value) {

    }

    /**
     * 300  等待支付  查看是否有订单,如果有订单,避免重复消费,如果没有则创建订单,然后将订单放到消息队列当中
     * 一般都是300先
     * @param order 订单信息
     */
    private void waitHandle(Order order) {

      /*  //执行脚本
        stringRedisTemplate.execute(ORDER_SCRIPT,Collections.emptyList(),
                RedisContent.CACHE_ORDERNAME,
                order.getId(),
                RedisContent.STREAM_ORDERNAME,
                JSONUtil.toJsonStr(order)
                );*/
        //首先查看
        HashOperations<String, Object, Order> stringObjectObjectHashOperations = stringRedisTemplate.opsForHash();
        Order cacheOrder = stringObjectObjectHashOperations.get(RedisContent.CACHE_ORDERNAME, order.getId());
        if (BeanUtil.isNotEmpty(cacheOrder  )){
            log.error("不允许重复下单!");
            return;
        }
        stringObjectObjectHashOperations.put(RedisContent.CACHE_ORDERNAME, order.getId(), order);


        RecordId recordId = stringRedisTemplate.opsForStream().add(RedisContent.STREAM_ORDERNAME, BeanUtil.beanToMap(order));


        stringRedisTemplate.opsForStream().createGroup(RedisContent.STREAM_ORDERNAME, "mygroup");

        stringRedisTemplate.opsForStream().claim(RedisContent.CACHE_ORDERNAME, "mygroup","consumerB", Duration.ofMinutes(20),recordId);
    }

    /**
     * 获取随机数
     * @return 随机数
     */
    private int getRandom(){
        //每日生成4位随机单号,随机单号的生成直接用value的方式,基于每日生成一直incre就行
        //查缓存如果不存在则生成
        String randomStr = stringRedisTemplate.opsForValue().get(RedisContent.RANDOMNUMBER + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy:MM:dd")));
        if (BeanUtil.isEmpty(randomStr)) {
            Integer randomId = RandomUtil.randomInt(100, 10000);
            redisSolve.set(RedisContent.RANDOMNUMBER+ LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy:MM:dd")), randomId);
            return randomId;
        }
        //不为空直接incre就行
        Long increment = stringRedisTemplate.opsForValue().increment(RedisContent.RANDOMNUMBER + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy:MM:dd")));
        return increment.intValue();
    }
}

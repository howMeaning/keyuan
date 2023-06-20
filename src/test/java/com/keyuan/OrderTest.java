package com.keyuan;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.keyuan.entity.Order;
import com.keyuan.mapper.GoodMapper;
import com.keyuan.mapper.OrderMapper;
import com.keyuan.service.IOrderService;
import com.keyuan.utils.RedisContent;
import com.keyuan.utils.RedisSolve;
import com.keyuan.utils.WebSocketServerUtil;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalUnit;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.keyuan.utils.RedisContent.CACHE_ORDERNAME;

/**
 * @descrition:
 * @author:how meaningful
 * @date:2023/5/15
 **/
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)

@Slf4j
public class OrderTest {
    @Autowired
    private GoodMapper goodMapper;

    @Resource
    private RabbitTemplate template;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Resource
    private WebSocketServerUtil webSocketServerUtil;
    @Resource
    private OrderMapper mapper;

    @Resource
    private IOrderService orderService;
    @Resource
    private RedisSolve redisSolve;
    @Test
    public void testDay(){
        System.out.println();
        System.out.println(LocalDateTime.now());
        System.out.println(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy:MM:dd")));

        System.out.println(LocalDateTime.now().getDayOfYear());


    }

    @Test
    public void testGetRedis(){
        HashOperations<String, Object, Object> stringObjectObjectHashOperations = stringRedisTemplate.opsForHash();
        Integer integer = BeanUtil.toBean(stringObjectObjectHashOperations.get("test", "x"), Integer.class);
        if (BeanUtil.isEmpty(integer)){
            //生成随机数
            int i = RandomUtil.randomInt(0, 1000);
            System.out.println(i);
        }
    }
    @Test
    public void testPendingList(){
        List<MapRecord<String, Object, Object>> read = stringRedisTemplate.opsForStream().read(Consumer.from("mygroup", "consumerA"),
                StreamReadOptions.empty().count(1),
                StreamOffset.create("test2", ReadOffset.from("0"))
        );
        MapRecord<String, Object, Object> record = read.get(0);
        log.info("id:{}", record.getId());
        String stream = record.getStream();
        log.info("stream:{}", stream);
        Map<Object, Object> value = record.getValue();

        log.info("map:{}",value);
        Object execute = stringRedisTemplate.execute(ORDER_SCRIPT, Collections.singletonList(null));
        log.info("test:{}",execute);
    }

    @Test
    public void testHash(){

        /*Object o = stringRedisTemplate.opsForHash().get(CACHE_ORDERNAME, String.valueOf(10L));
        Integer integer = Integer.valueOf(o);*/
        HashOperations<String, Object, String> stringObjectObjectHashOperations = stringRedisTemplate.opsForHash();
        String s = stringObjectObjectHashOperations.get(CACHE_ORDERNAME, String.valueOf(10L));
        System.out.println(Integer.valueOf(s));

        redisSolve.put(CACHE_ORDERNAME, String.valueOf(20L), 500);

        }
    private static final DefaultRedisScript<Long> ORDER_SCRIPT;

    static{
        ORDER_SCRIPT = new DefaultRedisScript<>();
        ORDER_SCRIPT.setLocation(new ClassPathResource("order.lua"));
        ORDER_SCRIPT.setResultType(Long.class);
    }


        @Test
        public void getRandom() {
            //每日生成4位随机单号,随机单号的生成直接用value的方式,基于每日生成一直incre就行
            //查缓存如果不存在则生成
            String randomStr = stringRedisTemplate.opsForValue().get(RedisContent.RANDOMNUMBER + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy:MM:dd")));
            if (BeanUtil.isEmpty(randomStr)) {
                Integer randomId = RandomUtil.randomInt(100, 10000);
                log.info("randomId:{}",randomId);
                redisSolve.set(RedisContent.RANDOMNUMBER+ LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy:MM:dd")), randomId);
                return;
            }
            //不为空直接incre就行
            Long increment = stringRedisTemplate.opsForValue().increment(RedisContent.RANDOMNUMBER + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy:MM:dd")));
            log.info("t:{}",increment.intValue());
        }


    @Test
    public void testMapper(){
            Order order = mapper.selectOne(new QueryWrapper<Order>().eq("order_id", 1L));
            System.out.println(order);
        }

    @Test
    public void testZset(){
        stringRedisTemplate.opsForZSet().remove(RedisContent.RANKKEY,String.valueOf(1000) );
    }

    @Test
    public  void testOrder(){
        Order order = new Order(null,1001,"1001,1002",10L,"不要放辣椒",10001L,0,10,LocalDateTime.now(),LocalDateTime.now().plusHours(1),LocalDateTime.now().plusHours(2),333,new BigDecimal(10.5));
        orderService.createOrder(order);

    }

    @Test
    public void testWebSockect(){
        while(true){
            webSocketServerUtil.sendMessage("你好");
        }
    }
}

package com.keyuan;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.keyuan.entity.Order;
import com.keyuan.mapper.GoodMapper;
import com.keyuan.utils.RedisContent;
import com.keyuan.utils.RedisSolve;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.JsePlatform;
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
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @descrition:
 * @author:how meaningful
 * @date:2023/5/15
 **/
@SpringBootTest
@Slf4j
public class OrderTest {
    @Autowired
    private GoodMapper goodMapper;


    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private ObjectMapper objectMapper = new ObjectMapper();

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
        Order order = new Order();
        order.setOrderNumber(123135).setId(12312313L).setStatus(122).setPayTime(LocalDateTime.now()).setCreateTime(LocalDateTime.now());
        stringRedisTemplate.opsForHash().put(RedisContent.CACHE_ORDERNAME,String.valueOf(order.getId()),JSONUtil.toJsonStr(order));
    }
    private static final DefaultRedisScript<Long> ORDER_SCRIPT;

    static{
        ORDER_SCRIPT = new DefaultRedisScript<>();
        ORDER_SCRIPT.setLocation(new ClassPathResource("order.lua"));
        ORDER_SCRIPT.setResultType(Long.class);
    }
    @Test
    public void testScript(){
        Order order = new Order();
        order.setOrderNumber(12313).setId(1231232221211L).setStatus(122).setPayTime(LocalDateTime.now()).setCreateTime(LocalDateTime.now());

        //执行脚本
        Long execute = stringRedisTemplate.execute(ORDER_SCRIPT, Collections.emptyList(),
                RedisContent.CACHE_ORDERNAME,
                String.valueOf(order.getId()),
                RedisContent.STREAM_ORDERNAME,
                String.valueOf(order.getOrderNumber()),
                JSONUtil.toJsonStr(order)

        );
        log.info("result:{}",execute);

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
            log.info("t:{}",            increment.intValue());
        }

        @Test
        public void testStream(){
            Order order = new Order();
            order.setOrderNumber(12313).setId(1231232221211L).setStatus(122).setPayTime(LocalDateTime.now()).setCreateTime(LocalDateTime.now());

            //首先查看
            HashOperations<String, Object, Order> stringObjectObjectHashOperations = stringRedisTemplate.opsForHash();
            Order cacheOrder = stringObjectObjectHashOperations.get(RedisContent.CACHE_ORDERNAME, order.getId());
            if (BeanUtil.isNotEmpty(cacheOrder)){
                log.error("不允许重复下单!");
                return;
            }
            stringObjectObjectHashOperations.put(RedisContent.CACHE_ORDERNAME, order.getId(), order);


            RecordId recordId = stringRedisTemplate.opsForStream().add(RedisContent.STREAM_ORDERNAME, BeanUtil.beanToMap(order));


            stringRedisTemplate.opsForStream().createGroup(RedisContent.STREAM_ORDERNAME, "mygroup");

            stringRedisTemplate.opsForStream().claim(RedisContent.CACHE_ORDERNAME, "mygroup","consumerB", Duration.ofMinutes(20),recordId);
        }
}

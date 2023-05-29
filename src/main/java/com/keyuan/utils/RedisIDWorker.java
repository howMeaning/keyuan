package com.keyuan.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.jni.Local;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/**
 * @descrition:全局唯一id生成,这里运用的是Redis生成,也可以用雪花算法替换
 * @author:how meaningful
 * @date:2023/4/10
 **/
@Component
@Slf4j
public class RedisIDWorker {
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    //指定初始时间的时间戳,这里一般用都是每日自动生成不同唯一id
    private static final long BEGIN_TIMESTEAMP = 1682899200;

    //序号的位数
    private static final  int COUNT_BITS = 32;

    public long nextId(String prefix){
        //TODO:左边31位是时间戳 右边31位当天时间然后自增
        //获取到的是指定前缀
        LocalDateTime now = LocalDateTime.now();

        //当前时间
        long currentSecond = now.toEpochSecond(ZoneOffset.UTC);
        //当前的时间-指定的时间
        long timeStemp = currentSecond - BEGIN_TIMESTEAMP;

        log.info("当前的时间戳:{}",timeStemp);

        //进行格式化
        String date = now.format(DateTimeFormatter.ofPattern("yyyy:MM:dd"));

        //以当前的日期做递增获取到的是每日的表
        //这里的increment()方法如果表不存在会新建
        long count = stringRedisTemplate.opsForValue().increment("incr:" + prefix + ":" + date);
        //先左移,移出的变0 在是跟0做运算,0和任意数字做或运算结果都是任意数字本身
        return timeStemp <<COUNT_BITS | count;

    }

    public static void main(String[] args) {
        RedisIDWorker redisIDWorker = new RedisIDWorker();
        LocalDateTime time = LocalDateTime.of(2023, 5, 1, 0, 0, 0);
        //这个中存储的是2022年1月1日0点0分0秒到1970年的时间差
        long second = time.toEpochSecond(ZoneOffset.UTC);//这个方法中的参数是时区参数
        System.out.println("second="+second);

        System.out.println(redisIDWorker.nextId("order"));
    }
}

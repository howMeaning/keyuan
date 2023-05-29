package com.keyuan.utils;

import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.keyuan.entity.Good;
import com.keyuan.service.IGoodService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @descrition:Redis预热
 * @author:how meaningful
 * @date:2023/3/6
 **/
@Component
public class RedisPreheat implements InitializingBean {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private IGoodService goodService;

    @Autowired
    private static final ObjectMapper MAPPER = new ObjectMapper();
    /**
     * 缓存预热
     * 思路:将商品全部传入到Redis当中,用sortedSet来存入
     */
    //TODO:这里需要用SortedSet进行存入,要加查询到的所有的数据进行排序
    @Override
    public void afterPropertiesSet() {
       /* if ( stringRedisTemplate.opsForZSet().size(RedisContent.CACHE_GOOD_KEY)!=0){
            return;
        }
        List<Good> goods = goodService.searchAll();
        for (Good good : goods) {
            String  goodJson= JSONUtil.toJsonStr(good);
            stringRedisTemplate.opsForZSet().addIfAbsent(RedisContent.CACHE_GOOD_KEY,goodJson,good.getSoleNum());
        }
        //这是过期时间
        stringRedisTemplate.expire(RedisContent.CACHE_GOOD_KEY,30, TimeUnit.DAYS);*/
    }
    /**
     * 版本2 用Cache预热所有的商品信息
     *
     */
    /*@Override
    public void afterPropertiesSet() throws Exception {
        Cache<Object, Object> build = Caffeine.newBuilder().build();

    }*/

 }

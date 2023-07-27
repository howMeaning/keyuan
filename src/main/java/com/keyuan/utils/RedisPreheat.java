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
 * @descrition:Redis预热,这里用Es来做
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
     * 缓存预热,通过es来找前10的数据,直接缓存到缓存中
     */
    @Override
    public void afterPropertiesSet() {

    }


 }

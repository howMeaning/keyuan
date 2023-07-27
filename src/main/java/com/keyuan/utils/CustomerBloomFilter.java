package com.keyuan.utils;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import static com.keyuan.utils.RedisContent.CACHEBLOOMFILTER;

/**
 * @descrition:
 * @author:how meaningful
 * @date:2023/7/15
 **/
@Component
public class CustomerBloomFilter {
    @Resource
    private StringRedisTemplate stringRedisTemplate;


    /**
     *
     * @param offset 值
     */
    public void put(Long offset){
        long abs = Math.abs(offset.hashCode());
        long index = (long) (abs % Math.pow(2,7));
        stringRedisTemplate.opsForValue().setBit(CACHEBLOOMFILTER,index,true);
    }

    //这里进行查
    public Boolean contain(Long offset){
        long abs = Math.abs(offset.hashCode());
        long index = (long) (abs % Math.pow(2,7));
        return  stringRedisTemplate.opsForValue().getBit(CACHEBLOOMFILTER,index);
    }
}

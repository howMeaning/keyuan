package com.keyuan.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @descrition:这个类负责Redis的序列化,缓存击穿,穿透,雪崩等问题
 * @author:how meaningful
 * @date:2023/5/12
 **/
@Component
@Slf4j
public class RedisSolve {
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    private static final  ObjectMapper objectMapper;

    static {
        objectMapper = new ObjectMapper();
        //转换为格式化的json对象,显示出来的格式美化
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

        //序列化的时候序列化对象的那些属性
        objectMapper.setSerializationInclusion(JsonInclude.Include.ALWAYS);

        //序列化的时候,如果是空对象,则不报错
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS,false);

        //反序列化的时候,遇到未知属性不会报错
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        //忽略transient修饰的属性
        objectMapper.configure(MapperFeature.PROPAGATE_TRANSIENT_MARKER, true);
    }

    public RedisSolve(StringRedisTemplate stringRedisTemplate){
        this.stringRedisTemplate = stringRedisTemplate;
    }

    /**
     * 存String类型集合
     * @param key
     * @param value

     * @throws JsonProcessingException
     */
    public void set(String key,Object value)  {
        try {
            stringRedisTemplate.opsForValue().set(key,objectMapper.writeValueAsString(value));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 存String类型集合,带有过期时间
     * @param key
     * @param value
     * @param time
     * @param timeUnit
     * @throws JsonProcessingException
     */
    public void setWithExpire(String key, Object value, Long time, TimeUnit timeUnit) throws JsonProcessingException {
        //将类转换成json对象
        stringRedisTemplate.opsForValue().set(key, objectMapper.writeValueAsString(value),time,timeUnit);
    }

    /**
     * 存map集合
     * @param key
     * @param field
     * @param value
     * @throws JsonProcessingException
     */
    public void put(String key,String field,Object value)  {
        try {
            stringRedisTemplate.opsForHash().put(key,field,objectMapper.writeValueAsString(value));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
        /**
         * 存Hash集合,不带过期时间
         * @param key
         * @param field
         * @param value
         * @param time
         * @param timeUnit
         * @throws JsonProcessingException
         */
    public void putWithExpire(String key,String field,Object value,Long time,TimeUnit timeUnit)  {
        try {
            stringRedisTemplate.opsForHash().put(key,field,objectMapper.writeValueAsString(value));
        } catch (JsonProcessingException e) {
            log.error("序列化失败,value:{}",value);
            throw new RuntimeException(e);
        }
        stringRedisTemplate.expire(key, time,timeUnit);
    }
}

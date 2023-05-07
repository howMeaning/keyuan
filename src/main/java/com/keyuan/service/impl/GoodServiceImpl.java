package com.keyuan.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.keyuan.dto.Result;
import com.keyuan.entity.Good;
import com.keyuan.entity.Order;
import com.keyuan.mapper.GoodMapper;
import com.keyuan.service.IGoodService;
import com.keyuan.service.IUpLoadService;
import com.keyuan.utils.RedisContent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.keyuan.utils.RedisContent.CACHE_GOOD_KEY;


/**
 * @descrition:
 * @author:how meaningful
 * @date:2023/3/8
 **/
@Slf4j
@Service
public class GoodServiceImpl extends ServiceImpl<GoodMapper,Good> implements IGoodService {
    @Autowired
    private  GoodMapper goodMapper;

    @Autowired
    private IUpLoadService upLoadService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    //搜索联想
    @Override
    public Result searchAssociation(String inputName) {
        List<String> goods = goodMapper.searchAssociation(inputName);
        if (CollectionUtil.isEmpty(goods)){
            return Result.ok(Collections.emptyList());
        }
        return Result.ok(goods);
       }

    /**
     * 根据名称找商品
     * 思路:先根据名称找缓存,如果没找到,则查数据库,查到数据将指定的数据库的数据存入缓存中
     * @param goodName
     * @return
     */
    @Override
    public Result searchGoodByName(String goodName) {
        Good o = (Good) stringRedisTemplate.opsForHash().get(CACHE_GOOD_KEY, goodName);
        return Result.ok(o);
    }


    /**
     * 查询所有
     */
    @Override
    public  List<Good> searchAll(){
        Set<String> allGood = stringRedisTemplate.opsForZSet().range(CACHE_GOOD_KEY, 1, -1);
        List<Good> goods = allGood.stream().map(s -> JSONUtil.toBean(s,Good.class)).collect(Collectors.toList());
        if (CollectionUtil.isEmpty(goods)){
            goods = goodMapper.searchAll();
        }
        return goods;
    }

    @Override
    public Result insertGood(MultipartFile imgFile, String goodName, Integer goodPrice) {
        return null;
    }

    /**
     * 获取消息队列消息,先查,如果没有则阻塞
     * @return
     */
    @Override
    public Result getOrder() {
        Order order = null;
        try {
            List<MapRecord<String, Object, Object>> read = stringRedisTemplate
                    .opsForStream()
                    .read(
                            //groupName,cosumerName
                            Consumer.from("s1","g1"),
                            //.empty获取的是一个空的StringReadOptions对象 .count获取数据的最大数,block没有数据则阻塞
                            StreamReadOptions.empty().count(1),
                            //Read  Offset
                            StreamOffset.create(RedisContent.ORDERNAME, ReadOffset.lastConsumed())
                            );
            MapRecord<String, Object, Object> record = read.get(0);
            if (CollectionUtil.isEmpty(record)){
                return Result.ok(Collections.emptyList());
            }
            Map<Object, Object> value = record.getValue();
            //将Map集合转成Bean类
            //true表示是否忽略错误
            order = BeanUtil.fillBeanWithMap(value, new Order(), true);

            //确认消息
//            stringRedisTemplate.opsForStream().acknowledge(RedisContent.ORDERNAME,"g1",record.getId());
        } catch (Exception e) {
            log.info(e.getMessage());
             order = HandlePendingList();
            return Result.ok(order);
        }
        return Result.ok(order);
    }

    /**
     * 当异常的时候,读取的是未确认的信息,变成0
     */
    private Order HandlePendingList(){
        List<MapRecord<String, Object, Object>> read = stringRedisTemplate.opsForStream().read(
                Consumer.from("s1", "g1"),
                StreamReadOptions.empty().count(1),
                StreamOffset.create(RedisContent.ORDERNAME, ReadOffset.from("0")
                ));
        //获取第一个信息
        MapRecord<String, Object, Object> record = read.get(0);
        Map<Object, Object> value = record.getValue();
        Order order = BeanUtil.fillBeanWithMap(value, new Order(), true);
        stringRedisTemplate.opsForStream().acknowledge(RedisContent.ORDERNAME,"g1",record.getId());
        return order;
    }

}


package com.keyuan.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.keyuan.dto.GoodDTO;
import com.keyuan.dto.Result;
import com.keyuan.entity.Good;
import com.keyuan.entity.Optional;
import com.keyuan.entity.Snake;
import com.keyuan.mapper.GoodMapper;
import com.keyuan.mapper.OptionalMapper;
import com.keyuan.mapper.SnakeMapper;
import com.keyuan.service.IGoodService;
import com.keyuan.service.IUpLoadService;
import com.keyuan.utils.RedisContent;
import com.keyuan.utils.RedisSolve;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static com.keyuan.utils.RedisContent.CACHE_GOOD_KEY;


/**
 * @descrition:
 * @author:how meaningful
 * @date:2023/3/8
 **/
@Slf4j
@Service
public class GoodServiceImpl extends ServiceImpl<GoodMapper, Good> implements IGoodService {
    @Autowired
    private GoodMapper goodMapper;

    @Resource
    private OptionalMapper optionalMapper;

    @Resource
    private SnakeMapper snakeMapper;

    @Autowired
    private IUpLoadService upLoadService;

    @Resource
    private RedisSolve redisSolve;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    //搜索联想
   /* @Override
    public Result searchAssociation(String inputName) {
        List<String> goods = goodMapper.searchAssociation(inputName);
        if (CollectionUtil.isEmpty(goods)){
            return Result.ok(Collections.emptyList());
        }
        return Result.ok(goods);
       }*/

    /**
     * 根据名称找商品
     * 思路:先根据名称找缓存,如果没找到,则查数据库,查到数据将指定的数据库的数据存入缓存中
     * @param goodName
     * @return
     */
    /*@Override
    public Result searchGoodByName(String goodName) {
        Good o = (Good) stringRedisTemplate.opsForHash().get(CACHE_GOOD_KEY, goodName);
        return Result.ok(o);
    }*/


    /**
     * 查询所有
     */
    @Override
    public List<Good> searchAll() {
        Set<String> allGood = stringRedisTemplate.opsForZSet().range(CACHE_GOOD_KEY, 1, -1);
        List<Good> goods = allGood.stream().map(s -> JSONUtil.toBean(s, Good.class)).collect(Collectors.toList());
        if (CollectionUtil.isEmpty(goods)) {
            goods = goodMapper.searchAll();
        }
        return goods;
    }

    /**
     * 插入商品
     * 插入逻辑:根据填入的商品信息进行查询
     * 1.插入snakeId,用Map<>接收,key是小食,value是小食名字,还有小食钱数,根据小食名字查小食表,先找缓存,如果有则直接将其id插入到主表
     * 2.插入optionalId,和上述一致
     * 3.插入Good,根据上述拿到的两个id组,插入到Good表当中
     */
    @Override
    @Transactional
    public Result insertGood(GoodDTO goodDTO) {
        List<String> snakeNameList = insertSnake(goodDTO);
        List<String> optionalNameList = insertOptional(goodDTO);
        String image = upLoadService.uploadImage(goodDTO.getImage());
        Good good = null;
        if (BeanUtil.isEmpty(goodDTO)) {
            return Result.fail("商品不能为空!");
        }
        //找小食
        good = goodMapper.selectOne(new QueryWrapper<Good>().eq("good_name", goodDTO.getFoodName()));
        if (BeanUtil.isNotEmpty(good)) {
            return Result.fail("商品已存在!");
        } else {
            if (!goodDTO.getFoodFlavor()) {
                //商品不存在,并且没有口感要求
               good = new Good(null, goodDTO.getFoodName(), goodDTO.getFoodPrices(), goodDTO.getFoodType(), goodDTO.getSoleNum(), 0, image);
            }
                good = new Good(null, goodDTO.getFoodName(), goodDTO.getFoodPrices(), goodDTO.getFoodType(), goodDTO.getSoleNum(), 1, image);
        }
        //插入到good
        int success = goodMapper.insertGood(good,snakeNameList,optionalNameList);
        if (success > 0) {
            return Result.ok("插入商品成功!");
        } else {
            return Result.fail("插入商品失败!");
        }
    }

    /**
     * 插入小食
     *
     * @param
     * @return
     */
    private List<String> insertSnake(GoodDTO goodDTO) {
        Map<String, Integer> snakes = goodDTO.getFoodSnakes();
        List<String> snakeNames = new ArrayList<>(snakes.size());
        for (Map.Entry<String, Integer> stringIntegerEntry : snakes.entrySet()) {
            //小食名字
            String snakeName = stringIntegerEntry.getKey();
            //小食费用
            Integer snakeMoney = stringIntegerEntry.getValue();
            //先找缓存
            Snake snake = (Snake) stringRedisTemplate.opsForHash().get(RedisContent.SNAKEKEY, snakeName);
            if (BeanUtil.isEmpty(snake)) {
                //缓存不存在
                snakeNames.add(snakeName);
                //再找数据库:查看是否有名字在表中,如果没有则插入
                snake = snakeMapper.selectOne(new QueryWrapper<Snake>().eq("snake_name", snakeName));
                if (snake == null) {
                    snake = new Snake(null, snakeName, snakeMoney);
                    //插入到数据库
                    snakeMapper.insert(snake);
                    //缓存一致性
                    redisSolve.put(RedisContent.SNAKEKEY,snake.getSnakeName(), snake);
                } else {
                    //如果数据库存在,则直接缓存到缓存
                    redisSolve.put(RedisContent.SNAKEKEY,snake.getSnakeName(), snake);
                }
            }
            snakeNames.add(snake.getSnakeName());
        }
        return snakeNames;
    }

    /**
     * 插入可选项
     *
     * @param goodDTO
     */
    private List<String> insertOptional(GoodDTO goodDTO) {
        String optionalName = goodDTO.getFoodOptionals();
        String[] optionalNames = optionalName.split(",");
        List<String> optionalNameList = new ArrayList<>(optionalNames.length);
        for (String o : optionalNames) {
            optionalNameList.add(o);
            Optional optional = (Optional) stringRedisTemplate.opsForHash().get(RedisContent.OPTIONALKEY, o);
            if (BeanUtil.isEmpty(optional)) {
                optional = optionalMapper.selectOne(new QueryWrapper<Optional>().eq("optional_name", o));
                if (optional == null) {
                    optional = new Optional(null, o);
                    optionalMapper.insert(optional);
                } else {
                    //如果存在,则直接缓存到缓存
                    redisSolve.put(RedisContent.SNAKEKEY, String.valueOf(optional.getOptionalId()), optional);
                }
            }
        }
        return optionalNameList;
    }


    /**
     * 获取消息队列消息,先查,如果没有则阻塞
     * @return
     *//*
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

    *//**
     * 当异常的时候,读取的是未确认的信息,变成0
     *//*
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
*/
}


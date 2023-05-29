package com.keyuan.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.keyuan.dto.GoodDTO;
import com.keyuan.dto.Result;
import com.keyuan.entity.Snake;
import com.keyuan.mapper.SnakeMapper;
import com.keyuan.service.ISnakeService;
import com.keyuan.utils.RedisContent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @descrition:
 * @author:how meaningful
 * @date:2023/5/26
 **/
@Slf4j
@Service
public class SnakeServiceImpl extends ServiceImpl<SnakeMapper, Snake> implements ISnakeService {
    @Resource
    private SnakeMapper snakeMapper;


    /**
     * 查所有snake
     * @param snakeId
     * @return
     */
    @Override
    public Result getSnake(String snakeId,Long shopId) {
        log.info("ids:{}",snakeId);
        String[] idStr = snakeId.split(",");
        List<String> idList = new ArrayList<>(Arrays.asList(idStr));
        List<Long> resultList = idList.stream().map(Long::valueOf).collect(Collectors.toList());
        log.info("resultList:{}",resultList);
        List<Snake> snakes = snakeMapper.selectBySnakeIdSnakes(resultList,shopId);
        if (snakes !=null){
            log.info("snakes不为null:{}",snakes);
                return Result.ok(snakes);
        }else {
                //这里表示数据库也没有
                log.info("抱歉:没有这个id的商品,商品id:{}",resultList);;
                return Result.ok(Collections.emptyList());
            }
        }


    /**
     * 插入小食
     *
     * @param
     * @return
     */
    @Override
    public List<String> insertSnake(GoodDTO goodDTO) {
        Map<String, BigDecimal> snakes = goodDTO.getFoodSnakes();
        List<String> snakeNames = new ArrayList<>(snakes.size());
        int i =0;
        for (Map.Entry<String, BigDecimal> stringIntegerEntry : snakes.entrySet()) {
            //小食名字
            String snakeName = stringIntegerEntry.getKey();
            //小食费用
            BigDecimal snakeMoney = stringIntegerEntry.getValue();
            //先找数据库,数据库存在则直接返回
            Snake snake = snakeMapper.selectOneBySnakeName(snakeName);
            if (snake == null){
                //如果不存在则直接返回空
                return Collections.emptyList();
               /* snake=  new Snake(null,snakeName,snakeMoney,goodDTO.getShopId());
                log.info("snake:{}",snake);
                 i+= snakeMapper.insertSnake(snake);

                snakeNames.add(snakeName);*/
            }

            snakeNames.add(snakeName);
        }
        return snakeNames;
    }
}

package com.keyuan.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.keyuan.dto.GoodDTO;
import com.keyuan.dto.Result;
import com.keyuan.entity.*;
import com.keyuan.mapper.GoodMapper;
import com.keyuan.service.IGoodService;
import com.keyuan.service.IScaleService;
import com.keyuan.service.ISnakeService;
import com.keyuan.service.IUpLoadService;
import com.keyuan.utils.RedisContent;
import com.keyuan.utils.RedisSolve;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.keyuan.utils.RedisContent.CACHEGOOD;
import static com.keyuan.utils.RedisContent.RANKKEY;


/**
 * @descrition:商品下的几个问题
 *  1.数据库销量的月更新问题
 *  2.修改销量的测试(貌似总是出现修改错误问题)
 *  3.目前压测还没测
 *  4.自定义数据的存储问题
 * @author:how meaningful
 * @date:2023/5/25
 **/
@Slf4j
@Service
public class GoodServiceImpl extends ServiceImpl<GoodMapper, Good> implements IGoodService {
    @Autowired
    private GoodMapper goodMapper;

    @Resource
    private ISnakeService snakeService;

    @Resource
    private IScaleService scaleService;

    @Resource
    private IUpLoadService upLoadService;

    @Resource
    private RedisSolve redisSolve;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    private final Cache<Long,Good> cache = Caffeine.newBuilder().build();


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
     * key是goodId value是good
     * 这里可以用Cache缓存
     * 这里返回的应当是GoodDTO
     *
     */
    @Override
    public Result searchAll(Long shopId) {
        //再找Redis缓存
        //这里的Redis不应该带销量,因为很容易导致数据的不一致
        Map<Object, Object> entries = stringRedisTemplate.opsForHash().entries(CACHEGOOD+shopId);
        List<Good> list = new ArrayList<>(entries.size());
        if (CollectionUtil.isEmpty(entries)){
            //再找数据库
            List<Good> goods = goodMapper.selectAll();
            if (goods != null){
                for (Good good : goods) {
                    //这里不应该带销量
                    good.setSoleNum(null);
                    //这里增加缓存,需要加逻辑过期时间
                    redisSolve.putWithExpire(CACHEGOOD+shopId,String.valueOf(good.getId()),good,30L, TimeUnit.DAYS);;
                }
                return Result.ok(goods);
            }else {
                return Result.fail("当前还没有商品!");
            }
        }
        for (Map.Entry<Object, Object> objectObjectEntry : entries.entrySet()) {
            String value = (String)objectObjectEntry.getValue();
            Good good= JSONUtil.toBean(value, Good.class);
            //这里不带销量,因为数据不是一致性的数据
            good.setSoleNum(null);
            list.add(good);
        }
        //这里表示缓存存在,直接返回
        return Result.ok(list);
    }


    /**
     * 插入商品
     * 插入逻辑:根据填入的商品信息进行查询
     * 1.插入snakeId,用Map<>接收,key是小食,value是小食名字,还有小食钱数,根据小食名字查小食表,先找缓存,如果有则直接将其id插入到主表
     * 2.插入optionalId,和上述一致
     * 3.插入Good,根据上述拿到的两个id组,插入到Good表当中
     * 这里还需要实现商品的月更新和逻辑删除的结束时间
     * 这两个都需要后端来进行插入后计算,所以应该是后端得到结果后放到entity中
     */
    @Override
    @Transactional
    public Result insertGood(GoodDTO goodDTO) {
        log.info("goodDTO:{}",goodDTO);
        List<String> snakeList = snakeService.insertSnake(goodDTO);
        if (CollectionUtil.isEmpty(snakeList)){
            return Result.fail("小食审核中....");
        }
        String image =null;
        if (!BeanUtil.isEmpty(goodDTO.getImage())){
             image = upLoadService.uploadImage(goodDTO.getImage());
        }else{
            log.warn("商品没有相册!");
        }
        Good  good = goodMapper.selectGoodByName(goodDTO.getFoodName());
        //这里处理goodFood,Good不能直接传String[]
        List<String> foodOptionals = goodDTO.getFoodOptionals();
        //将list转成String
        String foodOptionalStr = String.join(",", foodOptionals);
        if (BeanUtil.isNotEmpty(good)) {
            return Result.fail("商品已存在!");
        }
       else if (goodDTO.getFoodFlavor()) {
             good = BeanUtil.copyProperties(goodDTO, Good.class);
             good.setSoleNum(0L).setOptionalName(foodOptionalStr).setHasScale(scaleService.insertScale(goodDTO)).setFlavor(1).setImage(image);
        }else {
            good = BeanUtil.copyProperties(goodDTO, Good.class);
            good.setSoleNum(0L).setOptionalName(foodOptionalStr).setHasScale(scaleService.insertScale(goodDTO)).setFlavor(0).setImage(image);
        }
        //插入到good
        Integer success = goodMapper.insertGood(good,snakeList);
        if (success > 0) {
            //这里插入到缓存当中
            redisSolve.put(CACHEGOOD+good.getShopId(),String.valueOf(good.getId()),good);
            return Result.ok("插入商品成功!");

        } else {
            return Result.fail("插入商品失败!");
        }
    }

    /**
     * 增加数据库销量,修改缓存销量
     *
     * @return
     */
    @Override
    public Integer updateSoleNumByIds(String goodId,Long shopId) {
        String[] ids = goodId.split(",");
        Integer result = goodMapper.updateSoleNumByIds(ids);
        log.info("修改影响数:{}",result);
        if (Integer.valueOf(result)>0){
            //这里需要缓存一致性策略,将销量缓存删除,两个都带有销量,所以两个都需要删除
            //这里实际上用主动更新的方案并不好
            stringRedisTemplate.delete(CACHEGOOD);
            log.info("销量修改成功,商品id:{}",ids);
        }
        return result;
    }

    @Override
    public List<Good> getRankFive(Long shopId) {
        Set<String> range = stringRedisTemplate.opsForZSet().range(RANKKEY+shopId,1,-1);
        log.info("range:{}",range);
        //如果这里为空
        if (CollectionUtil.isEmpty(range)){
            //如果空直接找数据库
            for (Rank rank : goodMapper.selectSoleNum(shopId)) {
                stringRedisTemplate.opsForZSet().add(RANKKEY+shopId,String.valueOf(rank.getId()),rank.getSoleNum());
            }
        }
        //这里进行排行榜操作
        Set<String> top5 = stringRedisTemplate.opsForZSet().reverseRange(RANKKEY+shopId, 0, 4);
        List<Long> ids = top5.stream().map(Long::valueOf).collect(Collectors.toList());
        log.info("ids:{}",ids);
        List<Good> top5Good = goodMapper.selectListByIds(ids);
        if (CollectionUtil.isEmpty(top5Good)){
            return Collections.emptyList();
        }
        return top5Good;
    }

    /**
     * 这里进行逻辑的下架
     * 只是对数据库进行逻辑删除,如果超过了会有一个过期时间的
     * @param id
     * @return
     */
    @Override
    public Result logicRemoveGoodbyId(Long id) {
        LocalDateTime localDateTime = LocalDateTime.now().plusDays(10);
        String expire = localDateTime.format(DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss"));
        goodMapper.updateDeleteById(id,localDateTime);
        //这里先进行逻辑删除
        return Result.ok("下架成功,过期时间:"+expire+"");
    }

    public Result removeGoodById(Long id,Long shopId){
        int i = goodMapper.deleteById(id);
        //这里需要删除缓存
        stringRedisTemplate.opsForHash().delete(CACHEGOOD+shopId);
        if (i!=1){
          log.error("删除失败:{}",i);
        }
        return Result.ok("商品id:"+id+"删除");
    }

}


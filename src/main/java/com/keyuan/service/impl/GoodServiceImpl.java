package com.keyuan.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.keyuan.dto.GoodDTO;
import com.keyuan.dto.Result;
import com.keyuan.dto.ScaleDTO;
import com.keyuan.entity.*;
import com.keyuan.mapper.GoodMapper;
import com.keyuan.service.IGoodService;
import com.keyuan.service.IScaleService;
import com.keyuan.service.ISnakeService;
import com.keyuan.service.IUpLoadService;
import com.keyuan.utils.RedisContent;
import com.keyuan.utils.RedisSolve;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
import org.redisson.misc.Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.nio.file.CopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.keyuan.utils.RedisContent.CACHEGOOD;
import static com.keyuan.utils.RedisContent.RANKKEY;


/**
 * @descrition:商品下的几个问题 1.数据库销量的月更新问题
 * 2.修改销量的测试(貌似总是出现修改错误问题)
 * 3.目前压测还没测
 * 4.自定义数据的存储问题
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

    private final Cache<Long, Good> cache = Caffeine.newBuilder().build();


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
     * 这里需要得到SnakeId,ScaleId,然后查两个表最终变成GoodDTO返回到前端
     */
    @Override
    public Result searchAll(Long shopId) {
        //再找Redis缓存
        //这里的Redis不应该带销量,因为很容易导致数据的不一致
        HashOperations<String, String, String> stringObjectObjectHashOperations = stringRedisTemplate.opsForHash();
        Map<String, String> entries = stringObjectObjectHashOperations.entries(CACHEGOOD + shopId);
        List<Snake> snake = null;
        List<ScaleDTO> scaleDTOS = null;
        List<GoodDTO> goodDTOS = null;
        Map<String, List<GoodDTO>> map =null;
        Set<String> hashSet = null;
        if (CollectionUtil.isEmpty(entries)) {
            List<String> types = goodMapper.selectAllType();
            hashSet = new HashSet<>(types);
            map = new HashMap<>(hashSet.size());

            //再找数据库
            List<Good> goods = goodMapper.selectAll();
            if (goods != null) {
                for (String type : hashSet) {
                    goodDTOS = new ArrayList<>();
                    for (Good good : goods) {
                        if (type.equals(good.getFoodType())) {
                            Integer hasScale = good.getHasScale();
                            if (hasScale != null) {
                                //这里
                                scaleDTOS = scaleService.getScale(good.getId(), good.getShopId());
                            }
                            String snakeId = good.getSnakeId();
                            if (!StrUtil.isBlank(snakeId)) {
                                snake = snakeService.getSnake(snakeId, good.getShopId());
                            }
                            GoodDTO goodDTO = getGoodDTO(snake, scaleDTOS, good);
                            //这里返回的是List<Good>,但是我要转成List<GoodDTO>再返回;
                            goodDTOS.add(goodDTO);

                        }
                    }
                    map.put(type,goodDTOS);
                    for (Map.Entry<String, List<GoodDTO>> stringListEntry : map.entrySet()) {
                        JSONUtil.toJsonStr(stringListEntry.getValue());
                        entries.put(stringListEntry.getKey(), JSONUtil.toJsonStr(stringListEntry.getValue()));
                    }

                    stringRedisTemplate.opsForHash().putAll(CACHEGOOD+shopId,entries);
                }
                //这里增加缓存,需要加逻辑过期时间
                return Result.ok(map);
            } else {
                return Result.fail("当前还没有商品!");
            }
        }
        map = new HashMap<>(entries.size());
        //这里表示有缓存
        for (Map.Entry<String, String> objectObjectEntry : entries.entrySet()) {
            goodDTOS = JSONUtil.toBean(objectObjectEntry.getValue() , ArrayList.class, false);
            map.put(objectObjectEntry.getKey(),goodDTOS);
        }
        //这里表示缓存存在,直接返回
        return Result.ok(map);
    }

    private static GoodDTO getGoodDTO(List<Snake> snake, List<ScaleDTO> scaleDTOS, Good good) {
        //这里不应该带销量
        good.setSoleNum(null);
        GoodDTO goodDTO = BeanUtil.copyProperties(good, GoodDTO.class);
        goodDTO.setFoodSnakes(snake).setScales(scaleDTOS);
        goodDTO.setImage(good.getImage());
        return goodDTO;
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
        log.info("goodDTO:{}", goodDTO);
        List<String> snakeList = snakeService.insertSnake(goodDTO);
        if (CollectionUtil.isEmpty(snakeList)) {
            return Result.fail("小食审核中....");
        }
        String image = null;
        if (!BeanUtil.isEmpty(goodDTO.getImage())) {
            image = upLoadService.uploadImage(goodDTO.getImageFile());
        } else {
            log.warn("商品没有相册!");
        }
        Good good = goodMapper.selectGoodByName(goodDTO.getFoodName());
        //这里处理goodFood,Good不能直接传String[]
        List<String> foodOptionals = goodDTO.getFoodOptionals();
        //将list转成String
        String foodOptionalStr = String.join(",", foodOptionals);
        if (BeanUtil.isNotEmpty(good)) {
            return Result.fail("商品已存在!");
        } else if (goodDTO.getFoodFlavor()) {
            good = BeanUtil.copyProperties(goodDTO, Good.class);
            good.setSoleNum(0L).setOptionalName(foodOptionalStr).setHasScale(scaleService.insertScale(goodDTO)).setFlavor(1).setImage(image);
        } else {
            good = BeanUtil.copyProperties(goodDTO, Good.class);
            good.setSoleNum(0L).setOptionalName(foodOptionalStr).setHasScale(scaleService.insertScale(goodDTO)).setFlavor(0).setImage(image);
        }
        //插入到good
        Integer success = goodMapper.insertGood(good, snakeList);
        if (success > 0) {
            //这里插入到缓存当中
            redisSolve.put(CACHEGOOD + good.getShopId(), String.valueOf(good.getId()), good);
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
    public Integer updateSoleNumByIds(String goodId, Long shopId) {
        String[] ids = goodId.split(",");
        Integer result = goodMapper.updateSoleNumByIds(ids);
        if (Integer.valueOf(result) > 0) {
            //这里需要缓存一致性策略,将销量缓存删除,两个都带有销量,所以两个都需要删除
            //这里实际上用主动更新的方案并不好
            //TODO:时效性要求较高,这里应该直接修改的方式
            stringRedisTemplate.delete(CACHEGOOD);
            log.info("销量修改成功,修改影响数:{}", result);
        }
        return result;
    }

    @Override
    public List<Good> getRankFive(Long shopId) {
        //zrange key min max 先找所有数据
        Set<String> range = stringRedisTemplate.opsForZSet().range(RANKKEY + shopId, 1, -1);
        log.info("range:{}", range);
        //如果这里为空
        if (CollectionUtil.isEmpty(range)) {
            //如果空直接找数据库
            for (Rank rank : goodMapper.selectSoleNum(shopId)) {
                stringRedisTemplate.opsForZSet().add(RANKKEY + shopId, String.valueOf(rank.getId()), rank.getSoleNum());
            }
        }
        //数据库缓存过后直接进行排行榜操作,这里肯定有数据
        Set<String> top5 = stringRedisTemplate.opsForZSet().reverseRange(RANKKEY + shopId, 0, 4);
        List<Long> ids = top5.stream().map(Long::valueOf).collect(Collectors.toList());
        log.info("ids:{}", ids);
        List<Good> top5Good = goodMapper.selectListByIds(ids);
        log.info("排序后:{}",top5Good);
        if (CollectionUtil.isEmpty(top5Good)) {
            return Collections.emptyList();
        }
        return top5Good;
    }

    /**
     * 这里进行逻辑的下架
     * 只是对数据库进行逻辑删除,如果超过了会有一个过期时间的
     *
     * @param id
     * @return
     */
    @Override
    public Result logicRemoveGoodbyId(Long id) {
        LocalDateTime localDateTime = LocalDateTime.now().plusDays(10);
        String expire = localDateTime.format(DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss"));
        goodMapper.updateDeleteById(id, localDateTime);
        //这里先进行逻辑删除
        return Result.ok("下架成功,过期时间:" + expire + "");
    }

    public Result removeGoodById(Long id, Long shopId) {
        int i = goodMapper.deleteById(id);
        //这里需要删除缓存
        stringRedisTemplate.opsForHash().delete(CACHEGOOD + shopId);
        if (i != 1) {
            log.error("删除失败:{}", i);
        }
        return Result.ok("商品id:" + id + "删除");
    }

}


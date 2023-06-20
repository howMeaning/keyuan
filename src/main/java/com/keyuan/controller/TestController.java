package com.keyuan.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.lang.UUID;
import com.keyuan.dto.*;
import com.keyuan.entity.Order;
import com.keyuan.entity.Scale;
import com.keyuan.entity.Snake;
import com.keyuan.entity.User;
import com.keyuan.mapper.UserMapper;
import com.keyuan.service.IUserService;
import com.keyuan.utils.RedisContent;
import com.keyuan.utils.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @descrition:
 * @author:how meaningful
 * @date:2023/5/25
 **/
@RestController
@Slf4j
@RequestMapping("/test")
public class TestController {

    @Resource
    private IUserService userService;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /*@GetMapping("/getScale1")
    public Result getScale(){
     List<Scale> list = new ArrayList();
        list.add(new Scale(1L,"小",new BigDecimal(10.5),1L));
        list.add(new Scale(2L,"中",new BigDecimal(10.5),1L));
        list.add(new Scale(3L,"大",new BigDecimal(10.5),1L));
        return Result.ok(list);
    }*/

    @GetMapping("/getScale2")
    public Result getScale2(){
        Map<String,BigDecimal> map = new HashMap<>();
        map.put("小",new BigDecimal(10));
        map.put("中",new BigDecimal(10));
        map.put("大",new BigDecimal(10));
        return Result.ok(map);
    }

    @PostMapping("/getScale3")
    public Result getScale3(@RequestBody Map<String,BigDecimal> map){
        UserDTO user = UserHolder.getUser();
        log.info("scales:{},userDTO:{}",map,user);
        return Result.ok(map);
    }

 /*   @GetMapping("/getGoodDTO")
    public Result getGoodDTO(){
        Map<String,Integer> snakeMap = new HashMap<>();
        snakeMap.put("小食1",100);
        snakeMap.put("小食2",100);
        snakeMap.put("小食3",100);
        List<ScaleDTO> scaleList =new ArrayList<>();
        scaleList.add(new ScaleDTO(null,"小",new BigDecimal(10),12L));
        List<String> optional  = new ArrayList<>();
        optional.add("米粉");
        optional.add("肠粉");
        optional.add("炒粉");
        GoodDTO goodDTO = new GoodDTO(null,"食品名称","textType",snakeMap,new BigDecimal(10),scaleList,optional,false,10L,null);
        return Result.ok(goodDTO);
    }*/
    @GetMapping("/getOrder")
    public Result getOrder(){
        UserDTO user = UserHolder.getUser();
        log.info("user:{}",user);
        Order order = new Order(null,1001,"1001,1002",10L,"不要放辣椒",10001L,0,10, LocalDateTime.now(),LocalDateTime.now().plusMinutes(10),LocalDateTime.now().plusHours(2),333,new BigDecimal("10.5"),new BigDecimal(9));
        return Result.ok(order);
    }

    @GetMapping("/getShopDTO")
    public Result getShopDTO(){
        ShopDTO shopDTO = new ShopDTO(null, "汉堡", "华莱士", "xxxxx", "xxxxx", "外卖定制", LocalDateTime.of(2023, 10, 23, 23, 21, 11), 11.3, 11.4, 100L, new BigDecimal(15), null, "广州");
        return Result.ok(shopDTO);
    }


    @GetMapping("/getGoodDTO")
    public Result getGoodDTO(){
        Snake snake = new Snake(1L,"名字",new BigDecimal(10.2),10L);
        Snake snake2 = new Snake(2L,"名字",new BigDecimal(10.3),10L);
        List<Snake> objects = new ArrayList<>();
        objects.add(snake);
        objects.add(snake2);
        ScaleDTO scaleDTO = new ScaleDTO(1L, "名字", new BigDecimal(10.3), 10L);
        ScaleDTO scaleDTO2 =  new  ScaleDTO(1L,"名字",new BigDecimal(10.3),10L);
        ScaleDTO scaleDTO3=  new  ScaleDTO(1L,"名字",new BigDecimal(10.3),10L);
        List<ScaleDTO> objects2 = new ArrayList<>();
        objects2.add(scaleDTO);
        objects2.add(scaleDTO2);
        objects2.add(scaleDTO3);
        List<String> foodOptional = new ArrayList<>();
        foodOptional.add("面");
        foodOptional.add("米粉");
        foodOptional.add("肠粉");

        GoodDTO goodDTO =  new GoodDTO(1L,"foodName","foodType",objects,new BigDecimal(10L),objects2,foodOptional,1,false,10L,100L,null,"转换后");
        return Result.ok(goodDTO);
    }

    @GetMapping("getBatch/{ids}")
    public void testCollection(@PathVariable("ids")List<Long> ids){

    }

    @GetMapping("getUser")
    public Result testGetUser(){
        User user = new User(null,"test",1L,"13412345678",null, 12.11, 12.333, "深圳", null, LocalDateTime.now());
        return Result.ok(user);
    }

    /**
     * 这里进行测试用户的授权
     */
    public void testAuthorize(){

    }

}

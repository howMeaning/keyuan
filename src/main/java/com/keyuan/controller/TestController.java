package com.keyuan.controller;

import com.keyuan.dto.GoodDTO;
import com.keyuan.dto.Result;
import com.keyuan.dto.ScaleDTO;
import com.keyuan.dto.ShopDTO;
import com.keyuan.entity.Order;
import com.keyuan.entity.Scale;
import com.keyuan.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

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
        log.info("scales:{}",map);
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
        Order order = new Order(null,1001,"1001,1002",10L,"不要放辣椒",10001L,0,10, LocalDateTime.now(),LocalDateTime.now().plusHours(1),LocalDateTime.now().plusHours(2),333,new BigDecimal(10.5));
        return Result.ok(order);
    }

    @GetMapping("/getShopDTO")
    public Result getShopDTO(){
        ShopDTO shopDTO = new ShopDTO(null, "汉堡", "华莱士", "xxxxx", "xxxxx", "外卖定制", LocalDateTime.of(2023, 10, 23, 23, 21, 11), 11.3, 11.4, 100L, new BigDecimal(15), null, "广州");
        return Result.ok(shopDTO);
    }

    @GetMapping("/getUser")
    public Result getUser(){
        User user = new User(null,"test",10L,"13612345678",null,12.11,12.22,"深圳","US121ASAF",LocalDateTime.now());
        return Result.ok(user);
    }
}

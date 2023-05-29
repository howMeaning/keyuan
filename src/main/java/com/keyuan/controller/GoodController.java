package com.keyuan.controller;

import com.keyuan.dto.GoodDTO;
import com.keyuan.dto.Result;
import com.keyuan.entity.Good;
import com.keyuan.entity.Scale;
import com.keyuan.mapper.ShopMapper;
import com.keyuan.service.IGoodService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Id;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @descrition:
 * @author:how meaningful
 * @date:2023/3/6
 **/
@RestController
@RequestMapping("/good")
@Slf4j
public class GoodController {
    @Autowired
    IGoodService goodService;




    //查找所有商品
    //将所有的根据typeId,插入到GEO当中
    @GetMapping("/searchAll/{shopId}")
    public Result searchAll(@PathVariable("shopId") Long shopId){
        return goodService.searchAll(shopId);
    }

    //添加商品
    @PostMapping("/insertGood")
    public Result insertGood(@RequestBody GoodDTO goodDTO){
        return goodService.insertGood(goodDTO);
    }

    /*@PostMapping("/searchSnakeId")
    public  Result searchSnakeId(@RequestParam("snakeName") List<String> snakeNames){
        log.info("snakeName:{}",snakeNames);
        return Result.ok(goodService.searchSnakeId(snakeNames));
    }
*/
    @GetMapping("/getType")
    public Result getType(@RequestParam ("hobby")String hobby){
        log.info("结果:{}",hobby);
        return Result.ok();
    }

    @GetMapping("/getRankFive/{id}")
    public Result getRankFive(@RequestParam("id")Long shopId){
        return Result.ok(goodService.getRankFive(shopId));
    }

    @PostMapping("/logicRemoveGoodbyId")
    public Result logicRemoveGoodbyId(@RequestParam("id") Long id){
        return goodService.logicRemoveGoodbyId(id);
    }

    @PostMapping("/removeGood")
    public Result removeGoodById(@RequestParam("id") Long id,@RequestParam("shopId") Long shopId){
        return goodService.removeGoodById(id,shopId);
    }

   /* */
}

package com.keyuan.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.keyuan.dto.Result;
import com.keyuan.entity.Good;
import com.keyuan.service.IGoodService;
import com.keyuan.service.IUpLoadService;
import com.keyuan.service.impl.GoodServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @descrition:
 * @author:how meaningful
 * @date:2023/3/6
 **/
@RestController
@RequestMapping("/list")
@Slf4j
public class GoodController {
    @Autowired
    IGoodService goodService;

    @GetMapping("/search/{inputName}")
    public Result searchAssociation(@PathVariable("inputName")String inputName){
            return goodService.searchAssociation(inputName);
    }
    //查找所有
    @GetMapping("/searchOne")
    public Result searchGoodByName(@RequestParam("goodName")String goodName){
        return goodService.searchGoodByName(goodName);
    }

    //查找所有商品
    @GetMapping("/searchAll")
    public Result searchAll(){
        return Result.ok(goodService.searchAll());
    }

    //添加商品
    @PostMapping("/insertGood")
    public Result insertGood(
            @RequestParam("file") MultipartFile imgFile,
            @RequestParam("name")String goodName,
            @RequestParam("price") Integer goodPrice
    ){
        return goodService.insertGood(imgFile,goodName,goodPrice);
    }
    @GetMapping("/getOrder")
    public Result getOrder(){
        return goodService.getOrder();
    }

    @GetMapping("/getType")
    public Result getType(@RequestParam ("hobby")String hobby){
        log.info("结果:{}",hobby);
        return Result.ok();
    }

}

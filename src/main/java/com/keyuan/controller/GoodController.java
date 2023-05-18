package com.keyuan.controller;

import com.keyuan.dto.GoodDTO;
import com.keyuan.dto.Result;
import com.keyuan.entity.Good;
import com.keyuan.service.IGoodService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

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

   /* @GetMapping("/search/{inputName}")
    public Result searchAssociation(@PathVariable("inputName")String inputName){
            return goodService.searchAssociation(inputName);
    }
    //查找所有
    @GetMapping("/searchOne")
    public Result searchGoodByName(@RequestParam("goodName")String goodName){
        return goodService.searchGoodByName(goodName);
    }*/

    //查找所有商品
    @GetMapping("/searchAll")
    public Result searchAll(){
        return Result.ok(goodService.searchAll());
    }

    //添加商品
    @PostMapping("/insertGood")
    public Result insertGood(GoodDTO goodDTO){
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

}

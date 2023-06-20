package com.keyuan.controller;

import com.keyuan.dto.Result;
import com.keyuan.service.ISnakeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @descrition:
 * @author:how meaningful
 * @date:2023/5/26
 **/
@RestController
@RequestMapping("/snake")
public class SnakeController {
    @Resource
    private ISnakeService snakeService;

    @GetMapping("/getSnake/{id}/{shopId}")
    public Result getSnake(@RequestParam("id") String SnakeId,@RequestParam("shopId")Long shopId){
        return Result.ok(snakeService.getSnake(SnakeId,shopId));
    }
}

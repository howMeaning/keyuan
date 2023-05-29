package com.keyuan.controller;

import com.keyuan.dto.Result;
import com.keyuan.entity.Order;
import com.keyuan.service.IOrderService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;


/**
 * @descrition:目前项目遗留的问题
 *  用户消费历史记录问题
 *  消息队列的安全性处理问题
 * @author:how meaningful
 * @date:2023/5/25
 **/
@RestController
@RequestMapping("/order")
public class OrderController {
    @Resource
    IOrderService orderService;
    @PostMapping("/createOrder")
    public Result createOrder(@RequestBody Order order){
        orderService.createOrder(order);
       return Result.ok(order);
    }

    @PostMapping("/confirmOrder")
    public Result confirmOrder(@RequestBody Order order){
        return orderService.confirmOrder(order);
    }


    @GetMapping("/getOrder")
    public Result getAllOrder(){
        return Result.ok(orderService.getAllOrder());
    }


}

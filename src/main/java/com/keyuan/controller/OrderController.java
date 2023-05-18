package com.keyuan.controller;

import com.keyuan.dto.OrderDTO;
import com.keyuan.dto.Result;
import com.keyuan.entity.Order;
import com.keyuan.mapper.OrderMapper;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @descrition:
 * @author:how meaningful
 * @date:2023/5/13
 **/
@RestController
@RequestMapping("/order")
public class OrderController {
    @Resource
    private OrderMapper orderMapper;
  /*  @PostMapping("/createOrder")
    public Result createOrder(Order order){
        orderMapper.createOrder(order);
    }*/
}

package com.keyuan.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.keyuan.dto.OrderDTO;
import com.keyuan.dto.Result;
import com.keyuan.entity.Order;

import java.util.concurrent.ExecutionException;

public interface IOrderService extends IService<Order> {
    int createOrder(Order order) throws ExecutionException, InterruptedException;
}

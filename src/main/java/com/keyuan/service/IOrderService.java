package com.keyuan.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.keyuan.dto.Result;
import com.keyuan.entity.Order;

import java.util.List;

public interface IOrderService extends IService<Order> {
    Result createOrder(Order order);

    Result confirmOrder(Order order);

    List<Order> getAllOrder();

    Order selectOrderById(Long id);

    Result cancelOrder(Order order);

//    Result cancelOrder(Order order);
}

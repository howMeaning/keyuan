package com.keyuan.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.keyuan.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OrderMapper extends BaseMapper<Order> {
    Integer createOrder(Order o);

    Integer selectStatusById(Long orderId);

    Integer updateStatusById(@Param("orderId") Long orderId,@Param("orderStatus") Integer  status);

    List<Order> selectAllOrder();

    Order selectOrderById(Long id);
}

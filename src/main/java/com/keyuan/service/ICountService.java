package com.keyuan.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.keyuan.dto.Result;
import com.keyuan.entity.Count;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
public interface ICountService extends IService<Count> {
    Result payMoney(Long userId, Long shopId, Integer inputPassword, BigDecimal payMoney);
}

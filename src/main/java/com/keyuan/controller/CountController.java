package com.keyuan.controller;

import com.keyuan.dto.CountDTO;
import com.keyuan.dto.Result;
import com.keyuan.service.ICountService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.math.BigDecimal;

/**
 * @descrition:
 * @author:how meaningful
 * @date:2023/6/7
 **/
@RestController
@RequestMapping("/count")
public class CountController {
    @Resource
    private ICountService iCountService;
    @PostMapping("payMoney")
    public Result payMoney(@RequestBody CountDTO countDTO){
        return iCountService.payMoney(countDTO.getUserId(),countDTO.getShopId(),countDTO.getInputPassword(),countDTO.getPayMoney());
    }
}

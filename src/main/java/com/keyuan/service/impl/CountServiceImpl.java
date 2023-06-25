package com.keyuan.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.keyuan.dto.Result;
import com.keyuan.entity.Count;
import com.keyuan.mapper.CountMapper;
import com.keyuan.service.ICountService;
import com.keyuan.service.IUserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;

/**
 * @descrition:
 * @author:how meaningful
 * @date:2023/6/7
 **/
@Service
public class CountServiceImpl extends ServiceImpl<CountMapper,Count> implements ICountService {
    @Resource
    private IUserService userService;

    @Resource
    private CountMapper countMapper;
    @Transactional
    public Result payMoney(Long userId,Long shopId,Integer password,BigDecimal payMoney){

        Long descId = userService.selectUserByShopId(shopId);

        Count count = countMapper.selectById(userId);

        Integer payPassword = count.getPayPassword();
        if (!password.equals(payPassword)){
            return Result.fail("密码错误,请重试!");
        }
        Integer result = countMapper.updateCountById(descId, userId, payMoney);
        if (result!=null){
            return Result.ok(200,"支付成功");
        }
        return Result.fail("支付失败,系统出现未知错误!");
    }
}

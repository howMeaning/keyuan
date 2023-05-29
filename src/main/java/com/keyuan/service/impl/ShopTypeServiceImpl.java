package com.keyuan.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.keyuan.entity.ShopType;
import com.keyuan.mapper.ShopMapper;
import com.keyuan.mapper.ShopTypeMapper;
import com.keyuan.service.IShopService;
import com.keyuan.service.IShopTypeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @descrition:
 * @author:how meaningful
 * @date:2023/5/27
 **/
@Service
@Slf4j
public class ShopTypeServiceImpl extends ServiceImpl<ShopTypeMapper, ShopType>implements IShopTypeService {
    @Resource
    private ShopTypeMapper shopTypeMapper;
    @Override
    public Long getShopTypeIdByName(String shopType) {
        Long id = shopTypeMapper.selectTypeByName(shopType);
        if (id == null){
            return null;
        }
        //这里表示如果有,则直接返回
        return id;
    }
}

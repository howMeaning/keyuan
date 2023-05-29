package com.keyuan.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.keyuan.dto.Result;
import com.keyuan.dto.ShopDTO;
import com.keyuan.entity.Shop;

import java.util.List;

public interface IShopService extends IService<Shop> {
    //这里需要进行找所有Shop

    Result saveShop(ShopDTO shopDTO);

    Result queryShopDistanceByTypeId(Long typeId,Integer current);


    Result loadShopDataById(Long id,Long typeId);

    Result removeShopById(Long id,Long typeId);
}

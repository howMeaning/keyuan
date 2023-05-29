package com.keyuan.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.keyuan.entity.ShopType;

public interface IShopTypeService extends IService<ShopType> {
    Long getShopTypeIdByName(String shopType);

}

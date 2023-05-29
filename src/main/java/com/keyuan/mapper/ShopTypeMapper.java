package com.keyuan.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.keyuan.entity.ShopType;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ShopTypeMapper extends BaseMapper<ShopType> {
    Long selectTypeByName(String shopType);
}

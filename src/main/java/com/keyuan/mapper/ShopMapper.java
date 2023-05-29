package com.keyuan.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.keyuan.dto.Result;
import com.keyuan.entity.Shop;
import com.keyuan.utils.SystemContents;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @descrition:
 * @author:how meaningful
 * @date:2023/5/23
 **/
@Mapper
public interface ShopMapper extends BaseMapper<Shop> {
    Integer insertShop(Shop shop);

    Page<Shop> selectShopByType(Integer typeId, Integer current, Integer pageSize);

    List<Shop> selectAll();

    List<Shop> selectShopById(List<Long> ids);

    Shop selectShopOne(@Param("id") Long id, @Param("typeId") Long typeId);
}

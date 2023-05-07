package com.keyuan.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.keyuan.entity.GoodType;

import java.util.List;

/**
 * @descrition:
 * @author:how meaningful
 * @date:2023/3/8
 **/

public interface GoodTypeMapper extends BaseMapper<GoodType> {
    public List<GoodType> searchAll();
}

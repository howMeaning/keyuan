package com.keyuan.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import com.keyuan.entity.Scale;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ScaleMapper extends BaseMapper<Scale> {

    /**
     * 插入optional
     */
    int insertScale(Scale scale);


    Scale selectScaleByGoodId(@Param("goodId") Long goodId,@Param("shopId") Long shopId);
}

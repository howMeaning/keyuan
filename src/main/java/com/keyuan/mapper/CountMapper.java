package com.keyuan.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.keyuan.entity.Count;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;

@Mapper
public interface CountMapper extends BaseMapper<Count> {
    Integer updateCountById(@Param("srcId") Long srcUserId, @Param("decId") Long descUserId,@Param("count") BigDecimal count);
}

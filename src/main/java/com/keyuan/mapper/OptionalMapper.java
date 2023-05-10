package com.keyuan.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.keyuan.entity.Optional;

public interface OptionalMapper extends BaseMapper<Optional> {
    /**
     * 插入optional
     */
    int insertOptional(String optionalName);
}

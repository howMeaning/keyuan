package com.keyuan.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.keyuan.entity.Good;

import java.util.List;

/**
 * @descrition:
 * @author:how meaningful
 * @date:2023/3/7
 **/

public interface GoodMapper extends BaseMapper<Good> {

    public List<String> searchAssociation(String inputName);

    public List<Good> searchAll();
}

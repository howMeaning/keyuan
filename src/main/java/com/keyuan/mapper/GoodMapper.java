package com.keyuan.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.keyuan.entity.Good;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @descrition:
 * @author:how meaningful
 * @date:2023/3/7
 **/
@Mapper
public interface GoodMapper extends BaseMapper<Good> {

    List<Good> searchAll();

    int insertGood(Good good, @Param("snakeNames")List<String> snakeNameList,@Param("OptionalNames")List<String> optionalNameList);

}

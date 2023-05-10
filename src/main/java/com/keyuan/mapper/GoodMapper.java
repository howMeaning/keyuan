package com.keyuan.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.keyuan.entity.Good;
import com.keyuan.entity.Optional;
import com.keyuan.entity.Snake;

import javax.swing.text.html.Option;
import java.util.List;

/**
 * @descrition:
 * @author:how meaningful
 * @date:2023/3/7
 **/

public interface GoodMapper extends BaseMapper<Good> {

    List<Good> searchAll();








}

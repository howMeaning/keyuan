package com.keyuan.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.keyuan.entity.Good;
import com.keyuan.entity.Rank;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @descrition:
 * @author:how meaningful
 * @date:2023/3/7
 **/
@Mapper
public interface GoodMapper extends BaseMapper<Good> {

    List<Good> selectAll();

    Integer insertGood(@Param("good") Good good,@Param("snakeList") List<String> snakeList);

    Integer updateSoleNumByIds(String[] ids);

    List<Good> selectListByIds(List<Long> ids);

    List<Rank> selectSoleNum(Long shopId);

    Good selectGoodByName(String foodName);

    Integer updateDeleteById(Long id, LocalDateTime endTime);

    Long selectIdByName(String foodName);

    List<String> selectAllType();
}

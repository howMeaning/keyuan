package com.keyuan.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.keyuan.entity.Snake;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SnakeMapper extends BaseMapper<Snake> {

    Snake selectOneBySnakeId(Long id);

    List<Snake> selectBySnakeIdSnakes(@Param("ids") List<Long> ids,@Param("shopId")Long shopId);

    int insertSnake(Snake snake);

    Snake selectOneBySnakeName(String snakeName);
}

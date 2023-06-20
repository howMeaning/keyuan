package com.keyuan.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.keyuan.dto.GoodDTO;
import com.keyuan.dto.Result;
import com.keyuan.entity.Snake;

import java.util.List;

public interface ISnakeService extends IService<Snake> {
    List<Snake> getSnake(String ids,Long shopId);

     List<String> insertSnake(GoodDTO goodDTO);


}

package com.keyuan.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.keyuan.dto.GoodDTO;
import com.keyuan.dto.Result;
import com.keyuan.dto.ScaleDTO;
import com.keyuan.entity.Scale;

import java.util.List;

/**
 * @descrition:
 * @author:how meaningful
 * @date:2023/5/26
 **/

public interface IScaleService extends IService<Scale> {


    List<ScaleDTO> getScale(Long goodId, Long shopId);

     int insertScale(GoodDTO goodDTO);

}

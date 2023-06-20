package com.keyuan.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.keyuan.dto.GoodDTO;
import com.keyuan.dto.Result;
import com.keyuan.entity.Good;
import com.keyuan.entity.Order;
import com.keyuan.entity.Shop;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @descrition:
 * @author:how meaningful
 * @date:2023/3/7
 **/

public interface IGoodService extends IService<Good> {
/*    Result searchAssociation(String inputName);

    Result searchGoodByName(String goodName);*/

   Result searchAll(Long shopId);

    Result insertGood(GoodDTO goodDTO);

    Integer updateSoleNumByIds(String goodIds,Long shopId);

    List<Good> getRankFive(Long shopId);


    Result logicRemoveGoodbyId(Long id);

    Result removeGoodById(Long id,Long shopId);
}

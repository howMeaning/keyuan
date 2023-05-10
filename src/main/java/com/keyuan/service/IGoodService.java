package com.keyuan.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.keyuan.dto.Result;
import com.keyuan.entity.Good;
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

    List<Good> searchAll();

    Result insertGood(MultipartFile imgFile, Good good);

    /* Result getOrder();*/
}

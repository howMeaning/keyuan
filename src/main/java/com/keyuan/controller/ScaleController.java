package com.keyuan.controller;

import com.keyuan.dto.Result;
import com.keyuan.dto.UserDTO;
import com.keyuan.service.IScaleService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @descrition:
 * @author:how meaningful
 * @date:2023/5/26
 **/

@RestController
@RequestMapping("/scale")
public class ScaleController {
    @Resource
    private IScaleService scaleService;

    /**
     * 获取规模,需要一个goodId
     * @param
     * @param goodId
     * @return
     */
    @GetMapping("/getScale/{id}/{shopId}")
    public Result getScale(@RequestParam("id")Long goodId,@RequestParam("shopId")Long shopId){
        return Result.ok(scaleService.getScale(goodId,shopId));
    }

}

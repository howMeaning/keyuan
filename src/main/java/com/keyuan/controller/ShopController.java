package com.keyuan.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.keyuan.dto.Result;
import com.keyuan.dto.ShopDTO;
import com.keyuan.entity.Shop;
import com.keyuan.service.IShopService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @descrition:
 * @author:how meaningful
 * @date:2023/5/25
 **/
@RestController
@RequestMapping("/shop")
public class ShopController {

    @Resource
    private IShopService shopService;


    @GetMapping("/of/distance")
    public Result queryShopDistanceByType(@RequestParam("typeId") Long typeId,@RequestParam("current") Integer current){
        return shopService.queryShopDistanceByTypeId(typeId,current);
    }

    @PostMapping("/of/saveShop")
    public Result saveShop(@RequestBody ShopDTO shopDTO){
        return  shopService.saveShop(shopDTO);
    }

    @PostMapping("/of/loadShopOne")
    public Result loadShopOne(@RequestParam("id") Long id,@RequestParam("typeId") Long typeId){
        return shopService.loadShopDataById(id,typeId);
    }

    @PostMapping("/of/deleteShopById")
    public Result removeShopById(@RequestParam("id") Long id,@RequestParam("typeId") Long typeId){
        shopService.removeShopById(id,typeId);

        return Result.ok("删除成功");
    }

}

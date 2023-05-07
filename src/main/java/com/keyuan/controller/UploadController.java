package com.keyuan.controller;

import com.keyuan.dto.Result;
import com.keyuan.service.IUpLoadService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;




/**
 * @descrition:文件上传,后缀名不是png,jpg,gif,jpeg图片,就表示不能上传
 * 文件的大小不能超过10M
 * @author:how meaningful
 * @date:2023/3/6
 **/
@Slf4j
@RestController
@RequestMapping("/upload")
public class UploadController {
    @Autowired
    IUpLoadService upLoadService;
    @PostMapping("/image")
    public Result uploadImage(@RequestParam("file") MultipartFile imgFile){
            return Result.ok(upLoadService.uploadImage(imgFile));
        }

    }




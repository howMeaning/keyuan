package com.keyuan.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.UUID;
import com.keyuan.service.IUpLoadService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

import static com.keyuan.utils.RedisContent.IMGAGENAME;

/**
 * @descrition:
 * @author:how meaningful
 * @date:2023/3/13
 **/
@Slf4j
@Service
public class UpLoadServiceImpl implements IUpLoadService {

    @Override
    public String uploadImage(MultipartFile imgFile) {
        //接收文件
        String imageName = getImage(imgFile);
        return imageName;
    }

    /**
     * 将照片转移
     * @param imgFile
     * @return
     */
    private String getImage(MultipartFile imgFile) {
        if (imgFile.isEmpty()) {
            return "文件是空文件!";
        }
        if (imgFile.getSize() > 1024 * 1024 * 10) {
            return "文件大于限制!";
        }
        String suffix = FileUtil.getSuffix(imgFile.getOriginalFilename());
        if (!"png,jpg".toUpperCase().contains(suffix.toUpperCase())) {
            return "文件格式不正确!";
        }

        //将文件上传到指定的位置
        File file = new File(IMGAGENAME);
        if (!file.exists()) {
            file.mkdir();
        }
        String fileName = UUID.randomUUID().toString(true) + "." + suffix;
        try {
            imgFile.transferTo(new File(IMGAGENAME, fileName));
            log.info("文件上传成功:{}", fileName);
            return "image/"+fileName;
        } catch (IOException e) {
            throw new RuntimeException("文件上传失败", e);
        }
    }
}

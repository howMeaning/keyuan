package com.keyuan.service;

import org.springframework.web.multipart.MultipartFile;

public interface IUpLoadService {

    String uploadImage(MultipartFile imgFile);
}

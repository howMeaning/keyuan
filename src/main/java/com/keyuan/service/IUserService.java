package com.keyuan.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.keyuan.dto.Result;
import com.keyuan.entity.User;

public interface IUserService extends IService<User> {

    Result createUser(User user);

    Integer updateUserPhoneById(String phone,Long id);

    Result getUserByToken(String token);

    Result updateAreaById(Double x,Double y,Long id);

    Long selectUserByShopId(Long shopId);

    User selectUserById(Long id);
}

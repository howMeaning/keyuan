package com.keyuan.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.keyuan.entity.User;
import org.apache.ibatis.annotations.Param;

/**
 * @descrition:
 * @author:how meaningful
 * @date:2023/5/25
 **/

public interface UserMapper extends BaseMapper<User> {

    int insertUser(User user);

    Integer updateUserPhoneById(@Param("phone") String phone,@Param("id") Long id);

    User selectUserByToken(String token);

    Integer updateAreaById(@Param("x") Double x,@Param("y") Double y,@Param("id") Long id);

    Long selectUserByShopId(Long shopId);

    User selectUserById(Long id);
}

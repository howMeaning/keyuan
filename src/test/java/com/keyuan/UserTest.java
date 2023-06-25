package com.keyuan;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.lang.UUID;
import cn.hutool.json.JSONUtil;
import com.keyuan.dto.Result;
import com.keyuan.dto.UserDTO;
import com.keyuan.entity.User;
import com.keyuan.utils.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * @descrition:
 * @author:how meaningful
 * @date:2023/6/19
 **/
@SpringBootTest
@Slf4j
public class UserTest {

    @Test
    public void testInsertUser(){
        /**
         * 这里模拟授权
         */
        User user = new User(null,"test",10L,"13612345678","null",12.11,12.22,"深圳",null, LocalDateTime.now());
        //这里生成token
        String token = UUID.randomUUID().toString(true);
        user.setToken(token);
        UserDTO userDTO = BeanUtil.copyProperties(user, UserDTO.class);
        log.info("userDTO:{}",userDTO);

        //将用户存在缓存当中
        Map<String, Object> userMap = BeanUtil.beanToMap(userDTO, new HashMap<>(),
                CopyOptions.create()
                        .setIgnoreNullValue(true).setIgnoreError(true)
                            .setFieldValueEditor((fieldName, fieldValue) -> JSONUtil.toJsonStr(fieldValue)));

        log.info("map:{}",userMap);
    }

}

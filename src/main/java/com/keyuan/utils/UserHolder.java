package com.keyuan.utils;

import com.keyuan.dto.UserDTO;

/**
 * @descrition:
 * @author:how meaningful
 * @date:2023/5/25
 **/

public class UserHolder {
    private static final ThreadLocal<UserDTO> tl = new ThreadLocal<>();

    public static void savesUser(UserDTO userDTO){
            tl.set(userDTO);
    }

    public static UserDTO getUser(){
        return  tl.get();
    }

    public static void removeUser(){
        UserDTO user = UserHolder.getUser();
        tl.remove();
    }
}
package com.keyuan.utils;

import cn.hutool.core.util.StrUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * @descrition:这里是否符合正则表达式
 * @author:how meaningful
 * @date:2023/5/27
 **/
public class RegexUtil {
    /**
     * 判断是否是有效的手机号
     * @param phone
     * @return
     */
    public static boolean isPhoneInvaild(String phone){
        return mismatch(phone,RegexPattern.PHONE_REGEX);
    }

    /**
     * 这里判断是否普配
     * @param str
     * @param regex
     * @return
     */
    private static boolean mismatch(String str,String regex) {
            if (StrUtil.isBlank(str)){
                //如果是否空则直接返回true
                return true;
            }
            return !str.matches(regex);
    }

}

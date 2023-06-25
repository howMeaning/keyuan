package com.keyuan.interceptor;

import com.keyuan.utils.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @descrition:这里需要判断是否有授权
 * @author:how meaningful
 * @date:2023/6/20
 **/
@Slf4j
public class PayInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //判断是否有授权,如果没授权直接false
        if (UserHolder.getUser() == null){
            log.info("用户未授权");
            return false;
        }
        return true;

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserHolder.removeUser();
    }
}

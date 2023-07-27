package com.keyuan.interceptor;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import com.keyuan.dto.UserDTO;
import com.keyuan.entity.User;
import com.keyuan.utils.RedisContent;
import com.keyuan.utils.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @descrition:在这里需要判断用户是否开启定位模式
 * 这里需要进行存储用户的ThreadLocal的相关信息
 * 如何判断,首先需要找缓存,这里需要调用UserService当中的queryAreaById
 * @author:how meaningful
 * @date:2023/5/26
 **/
@Slf4j
public class VisitInterceptor implements HandlerInterceptor {
    private StringRedisTemplate stringRedisTemplate;

    public VisitInterceptor(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("token");
        //找缓存
        HashOperations<String, String, String> stringObjectObjectHashOperations = stringRedisTemplate.opsForHash();
        stringRedisTemplate.opsForValue();
        Map<String, String> entries = stringObjectObjectHashOperations.entries(RedisContent.CACHEUSER+token);
        if (entries.isEmpty()){
            //406表示当前用户未授权
            log.warn("当前用户未授权!");
        }
        log.info("entries:{}",entries);
        //如果存在,key应该是id,value应该是User,将user存到ThreadLocal当中
        UserDTO userDTO = BeanUtil.fillBeanWithMap(entries, new UserDTO(),
                new CopyOptions().setIgnoreNullValue(true)
                );

        if (userDTO.getX() == null &&userDTO.getY()==null){
            //407表示当前没有位置
            log.warn("当前用户没有位置!");
        }

        UserHolder.savesUser(userDTO);

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserHolder.removeUser();
    }
}

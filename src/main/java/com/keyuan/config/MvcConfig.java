package com.keyuan.config;


import com.keyuan.interceptor.VisitInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

/**
 * @descrition:
 * @author:how meaningful
 * @date:2023/5/26
 **/
@Configuration
public class MvcConfig implements WebMvcConfigurer {
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new VisitInterceptor(stringRedisTemplate))
                .addPathPatterns("/**")
                .excludePathPatterns("/user/createUser");
    }
}

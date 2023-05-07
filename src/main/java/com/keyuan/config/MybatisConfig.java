package com.keyuan.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * @descrition:
 * @author:how meaningful
 * @date:2023/3/7
 **/
@Configuration
@MapperScan("com.keyuan.mapper")
public class MybatisConfig {

}

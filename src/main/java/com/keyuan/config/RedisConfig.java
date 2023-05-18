package com.keyuan.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @descrition:
 * @author:how meaningful
 * @date:2023/4/8
 **/
@Configuration
public class RedisConfig {
//    @Bean
//    public LettuceClientConfigurationBuilderCustomizer lettuceClientConfigurationBuilderCustomizer(){
//        return new LettuceClientConfigurationBuilderCustomizer() {
//            @Override
//            public void customize(LettuceClientConfiguration.LettuceClientConfigurationBuilder clientConfigurationBuilder) {
//                    clientConfigurationBuilder.readFrom(ReadFrom.REPLICA_PREFERRED);
//            }
//        };
//    }

    /**
     * 分布式锁的配置
     */
    @Bean
    public RedissonClient redissonClient(){
        //创建配置对象
        Config config = new Config();
        config.useSingleServer().setAddress("redis://192.168.239.100:6379").setPassword("1234");
        return Redisson.create(config);
    }

}

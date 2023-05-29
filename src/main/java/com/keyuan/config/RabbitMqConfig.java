package com.keyuan.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

import static com.keyuan.utils.RabbitContent.*;

/**
 * @descrition:
 * @author:how meaningful
 * @date:2023/5/21
 **/
@Configuration
public class RabbitMqConfig {


    @Bean
    public DirectExchange normalExchange(){
        return  new DirectExchange(EXCHANGE_NAME, false, true);
    }
    @Bean
    public DirectExchange deadExchange(){
        return  new DirectExchange(DEADEXCHANGE_NAME, false, true);
    }

    /**
     * 这里需要指定死信交换机
     * @return
     */
    @Bean
    public Queue normalQueue(){
        //1000为1秒,60秒为1分钟,20分钟
        long ttl = 1000 * 60 * 20;

        Map<String, Object> arg = new HashMap<>();
        //设置死信交换机
        arg.put("x-dead-letter-exchange",DEADEXCHANGE_NAME);
        //设置routingkey
        arg.put("x-dead-letter-routing-key",NORMALTODEAD_ROUTING_KEY);
        //设置TTL过期时间,单位是ms
        arg.put("x-message-ttl",ttl);
        return QueueBuilder.durable(QUEUE_NAME).withArguments(arg).build();
    }
    @Bean
    public Queue deadQueue(){
        Queue build = QueueBuilder.durable(DEADQUEUE_NAME).build();
        return build;
    }

    /**
     * 正常队列的绑定
     * @param normalQueue 正常队列
     * @param directExchange 直接交换机
     * @return
     */
    @Bean
    public Binding normalBinding(
            @Qualifier("normalQueue") Queue normalQueue,
            @Qualifier("normalExchange")DirectExchange directExchange
                                 ){
            return BindingBuilder.bind(normalQueue).to(directExchange).with(NORMAL_ROUTING_KEY);
    }

    @Bean
    public Binding deadBingding(
            @Qualifier("deadQueue") Queue deadQueue,
            @Qualifier("deadExchange")DirectExchange directExchange
    ){
        return BindingBuilder.bind(deadQueue).to(directExchange).with(DEAD_ROUNTING_KEY);
    }
}

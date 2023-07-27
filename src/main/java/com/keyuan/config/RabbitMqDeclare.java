package com.keyuan.config;

import com.keyuan.utils.RabbitContent;
import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.listener.Topic;

import java.util.HashMap;
import java.util.Map;

import static com.keyuan.utils.RabbitContent.*;

/**
 * @descrition:
 * @author:how meaningful
 * @date:2023/5/21
 **/
@Configuration
public class RabbitMqDeclare {


    @Bean
    public TopicExchange topicExchange(){
        return  new TopicExchange(EXCHANGE_NAME);
    }

    @Bean
    public Queue normalQueue(){
        return new Queue(QUEUE_NAME);
    }



    /**
     * 这里需要指定死信交换机
     * @return
     */
    @Bean
    public Queue normalToDeadQueue(){
        //设置死信交换机
        return QueueBuilder.
                durable(NORMALTODEAD)
                .ttl(10000)
                .deadLetterExchange(DEADEXCHANGE_NAME)
                .deadLetterRoutingKey(DEAD_ROUNTING_KEY)
                .build();
    }
    @Bean
    public Binding normalBinding(){
        return BindingBuilder.bind(normalQueue()).to(topicExchange()).with(NORMAL_ROUTING_KEY);
    }
    @Bean
    public Binding normalToDeadQueueBind(){
        return BindingBuilder.bind(normalToDeadQueue()).to(topicExchange()).with(NORMALTODEAD_ROUTING_KEY);
    }



 /*



    @Bean
    public DirectExchange cancelExchange(){
        return new DirectExchange(CANCEL_EXCHANGE);
    }

    @Bean
    public Queue cancelQueue(){
        //先来个5秒
        int ttl = 10000;
        Map<String, Object> arg = new HashMap<>();
        //设置死信交换机
        arg.put("x-dead-letter-exchange",DEADEXCHANGE_NAME);
        //设置routingkey
        arg.put("x-dead-letter-routing-key",NORMALTODEAD_ROUTING_KEY);
        return QueueBuilder.durable(CANCEL_QUEUE)
                .ttl(ttl)
                .withArguments(arg).build();
    }
     @Bean
    public Binding cancelling(){
        return BindingBuilder.bind(cancelQueue()).to(cancelExchange()).with(CANCEL_ROUTINGKEY);
    }
    */



}

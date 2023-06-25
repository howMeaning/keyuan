package com.keyuan.config;

import com.keyuan.utils.RabbitContent;
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
public class RabbitMqDeclare {


    @Bean
    public DirectExchange normalExchange(){
        return  new DirectExchange(EXCHANGE_NAME, false, true);
    }


    /**
     * 这里需要指定死信交换机
     * @return
     */
    @Bean
    public Queue normalQueue(){
        Map<String, Object> arg = new HashMap<>();
        //设置死信交换机
        return QueueBuilder.durable(QUEUE_NAME)
                .ttl(60000)
                .deadLetterExchange(DEADEXCHANGE_NAME)
                .deadLetterRoutingKey(DEAD_ROUNTING_KEY)
                .withArguments(arg).build();
    }
    /**
     * 正常队列的绑定
     * @param normalQueue 正常队列
     * @param normalExchange 正常交换机
     * @return
     */
    @Bean
    public Binding normalBinding(
            @Qualifier("normalQueue") Queue normalQueue,
            @Qualifier("normalExchange")DirectExchange normalExchange)
    {
        return BindingBuilder.bind(normalQueue).to(normalExchange).with(NORMAL_ROUTING_KEY);
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



    ///私信交换机的绑定
    @Bean
    public DirectExchange deadExchange(){
        return new DirectExchange(DEADEXCHANGE_NAME);
    }

    @Bean
    public Queue deadQueue(){
        return new Queue(DEADQUEUE_NAME);
    }
    @Bean
    public Binding deadBinding(){
        return BindingBuilder.bind(deadQueue()).to(deadExchange()).with(DEAD_ROUNTING_KEY);
    }

}

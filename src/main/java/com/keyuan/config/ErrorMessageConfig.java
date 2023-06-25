package com.keyuan.config;

import com.keyuan.utils.RabbitContent;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.retry.MessageRecoverer;
import org.springframework.amqp.rabbit.retry.RepublishMessageRecoverer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @descrition:错误信息的交换机和队列
 * @author:how meaningful
 * @date:2023/6/19
 **/
@Configuration
public class ErrorMessageConfig {
    @Bean
    public DirectExchange errorExchange(){
        return new DirectExchange(RabbitContent.ERROREXCHANGE_NAME);
    }
    @Bean
    public Queue errorQueue(){
        return QueueBuilder.durable(RabbitContent.ERRORQUEUE_NAME).build();
    }
    @Bean
    public Binding errorBind(){
        return BindingBuilder.bind(errorQueue()).to(errorExchange()).with(RabbitContent.ERROR_ROUTINGKEY);
    }
    @Bean
    public MessageRecoverer republishMessageRecoverer(RabbitTemplate rabbitTemplate){
        return new RepublishMessageRecoverer(rabbitTemplate,RabbitContent.ERROREXCHANGE_NAME, RabbitContent.ERROR_ROUTINGKEY);
    }

}

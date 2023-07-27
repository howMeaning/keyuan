package com.keyuan.config;

import com.keyuan.utils.RabbitContent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ReturnedMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.retry.MessageRecoverer;
import org.springframework.amqp.rabbit.retry.RepublishMessageRecoverer;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @descrition:
 * @author:how meaningful
 * @date:2023/6/19
 **/

@Configuration
@Slf4j
public class RabbitMqConfig implements ApplicationContextAware {
    @Bean
    public MessageConverter jsonMessageConverter(){
        return new Jackson2JsonMessageConverter();
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        RabbitTemplate bean = applicationContext.getBean(RabbitTemplate.class);
        bean.setReturnsCallback(returnedMessage -> log.info("消息发送队列失败，应答码{}，原因{}，交换机{}，路由键{},消息{}",
                returnedMessage.getReplyCode(),
                returnedMessage.getReplyText(),
                returnedMessage.getExchange(),
                returnedMessage.getRoutingKey(),
                returnedMessage.getMessage().toString()));
    }


}

package com.keyuan;

import com.keyuan.utils.RabbitContent;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.concurrent.FailureCallback;
import org.springframework.util.concurrent.SuccessCallback;

import javax.annotation.Resource;

/**
 * @descrition:
 * @author:how meaningful
 * @date:2023/6/22
 **/
@SpringBootTest
@Slf4j
public class rabbitMqTest {
    @Resource
    private RabbitTemplate rabbitTemplate;
    @Test
    public void testConvert(){
        CorrelationData correlationData = new CorrelationData();
        correlationData.getFuture().addCallback(new SuccessCallback<CorrelationData.Confirm>() {
            @Override
            public void onSuccess(CorrelationData.Confirm result) {
                if (result.isAck()) {
                    log.info("消息发送成功:{}", correlationData.getId());
                }
            }
        }, ex -> log.error("消息发送失败:{}",ex.getMessage()));

        rabbitTemplate.convertAndSend(RabbitContent.EXCHANGE_NAME,RabbitContent.NORMAL_ROUTING_KEY,"你好",correlationData);
    }
}

package com.keyuan.utils;

import ch.qos.logback.core.encoder.EchoEncoder;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import com.keyuan.dto.UserDTO;
import com.keyuan.entity.Order;
import com.keyuan.mapper.OrderMapper;
import com.keyuan.service.IOrderService;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;

import static com.keyuan.utils.RedisContent.CACHE_ORDERNAME;

/**
 * @descrition:在这里做所有的数据一致性: 状态的一致性
 * 销量的一致性
 * 做延迟队列的监听
 * 做失败消息队列的监听
 * @author:how meaningful
 * @date:2023/6/19
 **/
@Component
@Slf4j
public class RabbitListenUtil {
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private OrderMapper orderMapper;

    @Resource
    private WebSocketServerUtil webSocketServerUtil;

    /**
     * 在这里做状态和销量的一致性
     * 确认订单后将缓存中的状态修改,并且发送webSocketServer
     *
     * @param message
     * @param
     */
        @RabbitListener(queues = RabbitContent.ERRORQUEUE_NAME)
        public void receiveDeadError(Message message){
            log.info("接收到错误队列的消息:{}", new String(message.getBody()));
        }

    @RabbitListener(queues = RabbitContent.DEADQUEUE_NAME)
    public void receiveDead(String message) {
        log.info("接收到死信队列的消息:{}",message);
        Order order = JSONUtil.toBean(message, Order.class);
        order.setOrderStatus(333);
        log.info("order:{}",order);
        //这里接收到死信队列后直接将缓存状态修改
        stringRedisTemplate.opsForHash().put(CACHE_ORDERNAME+order.getUserId(),String.valueOf(order.getOrderId()), JSONUtil.toJsonStr(order));

    }
    @RabbitListener(queues = RabbitContent.QUEUE_NAME)
    public void receiveNormal(String message){
            log.info("接收到普通队列的消息:{}",message);
            //接收到普通队列的消息,将缓存状态修改,然后发webSocket给前端

        Order order = JSONUtil.toBean(message, Order.class);
        order.setOrderStatus(400);
        String orderJson= JSONUtil.toJsonStr(order);
        //这里接收到死信队列后直接将缓存状态修改
        stringRedisTemplate.opsForHash().put(CACHE_ORDERNAME+order.getUserId(),String.valueOf(order.getOrderId()),JSONUtil.toJsonStr(orderJson));
        webSocketServerUtil.sendMessage(orderJson);
    }



}

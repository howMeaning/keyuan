package com.keyuan.utils;

import ch.qos.logback.core.encoder.EchoEncoder;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import com.keyuan.dto.UserDTO;
import com.keyuan.entity.Order;
import com.keyuan.mapper.OrderMapper;
import com.keyuan.service.IOrderService;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
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
     * @param channel
     */
//
  /*  @RabbitListener(queues = RabbitContent.QUEUE_NAME,ackMode = "MANUAL")
    public void receiveOrder(Message message, Channel channel) {
        try {
            if (message == null) {
                log.info("当前没有消息!");
                return;
            }
            log.info("正常队列消息接收成功!");
            String orderIdStr = new String(message.getBody());
            if ("".equals(orderIdStr)) {
                log.info("消息发生错误!");
                return;
            }
            long orderId = Long.parseLong(orderIdStr);


            //找数据库
            Order order = orderMapper.selectOrderById(orderId);
            log.info("order:{}",order);
            if (order.getOrderStatus() == 400) {
                String[] goodIdStr = order.getGoodId().split(",");
                List<String> goodIds = new ArrayList<>(goodIdStr.length);
                Collections.addAll(goodIds, goodIdStr);
                goodIds.forEach(goodId -> stringRedisTemplate.opsForZSet().incrementScore(RedisContent.RANKKEY + order.getShopId(), goodId, 1));
                //这里缓存的一致性,找数据库,然后缓存到Redis当中
                stringRedisTemplate.opsForHash().put(
                        CACHE_ORDERNAME + order.getUserId(),
                        String.valueOf(order.getOrderId()), String.valueOf(order.getOrderStatus()));
                log.info("销量一致性,缓存一致性更新成功");
                channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
                //发送到WebSocket当中
//            webSocketServerUtil.sendMessage(JSONUtil.toJsonStr(order));
            }

        } catch (Exception e) {
            log.info("程序出错原因:{}",e.getMessage());
            throw new RuntimeException(e);
        }
    }*/
    /*
     */

    /**
     * 这里进行确认失败的消息,将数据库的状态修改成333,然后缓存到缓存当中
     *//*
    @RabbitListener(queues = RabbitContent.CANCEL_QUEUE)
    public void receiveFailOrder(Message message, Channel channel) {
        try {
            if (message == null) {
                log.info("当前没有消息!");
                return;
            }
            log.info("订单拒绝队列消息接收成功!");
            String messageStr = message.toString();
            HashMap hashMap = JSONUtil.toBean(messageStr, HashMap.class);
            UserDTO user = UserHolder.getUser();
            Long userId = user.getId();

            Long orderId = JSONUtil.toBean(orderIdStr, Long.class);

            //这里确认订单
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);



            //这里缓存的一致性,找数据库,然后缓存到Redis当中
            stringRedisTemplate.opsForHash().put(CACHE_ORDERNAME + userId, JSONUtil.toJsonStr(orderIdStr), JSONUtil.toJsonStr(333));
            log.info("死信修改缓存状态成功");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }*/
    @RabbitListener(queues = RabbitContent.ERRORQUEUE_NAME)
    public void receiveError(Message message) {
        log.info("接收到了错误队列的消息:{}", new String(message.getBody()));
    }

    @RabbitListener(queues = RabbitContent.DEADQUEUE_NAME)
    public void receiveDead(Message message) {
        log.info("接收到死信队列的消息:{}", new String(message.getBody()));

    }

    @RabbitListener(queues = RabbitContent.QUEUE_NAME)
    public void listenNatureMq(Message message, Channel channel){
        log.info("接收到message:{},但是不确认", new String(message.getBody()));
        try {
            channel.basicNack(message.getMessageProperties().getDeliveryTag(),false,false);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
       /* try {
            channel.basicNack(message.getMessageProperties().getDeliveryTag(),false,false);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }*/
    }


}

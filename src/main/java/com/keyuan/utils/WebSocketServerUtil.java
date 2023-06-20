package com.keyuan.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.util.annotation.Nullable;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.TimeUnit;

/**
 * @descrition:
 * @author:how meaningful
 * @date:2023/5/22
 **/
@Component
@ServerEndpoint(value = "/order/webSokect/{uId}" )
@Slf4j
public class WebSocketServerUtil {
    private Session session;
    private final static CopyOnWriteArraySet<WebSocketServerUtil > webSocketSet = new CopyOnWriteArraySet<>();
    private final static ConcurrentHashMap<String,WebSocketServerUtil > webSocketMap  = new ConcurrentHashMap<>();
    private String uId = null;


    @OnOpen
    public void onOpen(Session session, @PathParam("uId") String uId){
        log.info("uId:{}",uId);
        this.session = session;
        this.uId = uId;
        if(webSocketMap .containsKey(uId)){
            webSocketMap .remove(uId);
            webSocketMap .put(uId,this);
        }else{
            webSocketMap .put(uId,this);
            webSocketSet.add(this);
        }

        log.info("【websocket消息】有新的连接，总数：{}",webSocketMap.size());
    }

    @OnClose
    public void onClose(){
        log.info("uid:{}",uId);
        if(webSocketMap.containsKey(uId)){
            webSocketMap.remove(uId);
            //从set中删除
            webSocketSet.remove(this);
        }
        log.info("【websocket消息】连接断开，总数：{}",webSocketSet.size());
    }

    @OnMessage
    public void onMessage(String message){
        log.info("【websocket消息】收到客户端发来的消息：{}",message);
    }

    public void sendMessage(String message){
        try {
            this.session.getBasicRemote().sendText(message);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    /**
     * 发送自定义消息
     * */
    public static void sendInfo(String message, Long uId) throws Exception {
        //log.info("发送消息到:"+uId+"，报文:"+message);
        log.info("message{}",message);
        if(webSocketMap.containsKey(uId)){
            if (!message.isEmpty()){
                webSocketMap.get(uId).sendMessage(message);
            }
            log.warn("目前消息为空");
            webSocketMap.get(uId).sendMessage("目前没有消息");
        }else{
            log.error("用户"+uId+",不在线！");
            throw new Exception("连接已关闭，请刷新页面后重试");
        }
    }


}

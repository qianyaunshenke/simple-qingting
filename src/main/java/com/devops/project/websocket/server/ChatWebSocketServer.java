package com.devops.project.websocket.server;

import com.alibaba.fastjson.JSON;
import com.devops.project.websocket.po.KefuMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@ServerEndpoint("/webSocket/{loginName}")
@Component
public class ChatWebSocketServer {
    //静态变量，用来记录当前在线客服连接数。应该把它设计成线程安全的。
    private static AtomicInteger onlineNum = new AtomicInteger();

    //concurrent包的线程安全Set，用来存放每个客户端对应的WebSocketServer对象。
    private static ConcurrentHashMap<String, Session> sessionPools = new ConcurrentHashMap<>();

    //发送消息
    public void sendMessage(Session session, String message) throws IOException {
        if (session != null) {
            synchronized (session) {
                log.info("发送数据：" + message);
                session.getBasicRemote().sendText(message);
            }
        }
    }

    //给指定用户发送信息
    public void sendInfo(String userName, String message) {
        Session session = sessionPools.get(userName);
        try {
            sendMessage(session, message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 群发消息
    public void broadcast(String message) {
        for (Session session : sessionPools.values()) {
            try {
                sendMessage(session, message);
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
        }
    }

    //建立连接成功调用
    @OnOpen
    public void onOpen(Session session, @PathParam(value = "loginName") String loginName) {
        sessionPools.put(loginName, session);
        addOnlineCount();
        log.info(loginName + "加入webSocket！当前客服人数为" + onlineNum);
        // 广播上线消息
        KefuMessage msg = new KefuMessage();
        msg.setDate(new Date());
        msg.setTo("0");
        msg.setText(loginName);
        broadcast(JSON.toJSONString(msg, true));
    }

    //关闭连接时调用
    @OnClose
    public void onClose(@PathParam(value = "loginName") String loginName) {
        sessionPools.remove(loginName);
        subOnlineCount();
        log.info(loginName + "断开webSocket连接！当前客服人数为" + onlineNum);
        // 广播下线消息
        KefuMessage msg = new KefuMessage();
        msg.setDate(new Date());
        msg.setTo("-2");
        msg.setText(loginName);
        broadcast(JSON.toJSONString(msg, true));
    }

    //收到客户端信息后，根据接收人的username把消息推下去或者群发
    // to=-1群发消息
    @OnMessage
    public void onMessage(String message) throws IOException {
        log.info("server get" + message);
        KefuMessage msg = JSON.parseObject(message, KefuMessage.class);
        msg.setDate(new Date());
        if (msg.getTo().equals("-1")) {
            broadcast(JSON.toJSONString(msg, true));
        } else {
            sendInfo(msg.getTo(), JSON.toJSONString(msg, true));
        }
    }

    //错误时调用
    @OnError
    public void onError(Session session, Throwable throwable) {
        log.info("发生错误");
        throwable.printStackTrace();
    }

    public static void addOnlineCount() {
        onlineNum.incrementAndGet();
    }

    public static void subOnlineCount() {
        onlineNum.decrementAndGet();
    }

    public static AtomicInteger getOnlineNumber() {
        return onlineNum;
    }

    public static ConcurrentHashMap<String, Session> getSessionPools() {
        return sessionPools;
    }
}

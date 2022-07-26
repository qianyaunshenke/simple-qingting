package com.devops.project.websocket.server;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.devops.common.utils.StringUtils;
import com.devops.project.business.domain.Visitor;
import com.devops.project.business.service.IVisitorService;
import com.devops.project.websocket.po.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
@ServerEndpoint("/ws_visitor/bak")
public class VisitorSocketServer extends BaseWebSocket {

    // 从websocketconfig中注入service
    public static IVisitorService visitorService;
    //静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
    private static AtomicInteger onlineNum = new AtomicInteger();

    /**
     * 存储连接的客户端
     */
    private static Map<String, VisitorSocketServer> clients = new ConcurrentHashMap<String, VisitorSocketServer>();
    private Session session;
    /**
     * 访客id
     */
    private String visitor_id;


    //建立连接成功调用
    @OnOpen
    public void onOpen(Session session) {
        String queryString = session.getQueryString();
        if (StringUtils.isEmpty(queryString)) return;
        Map<String, String> paramsMap = parseParams(queryString);
        String visitor_id = paramsMap.get("visitor_id");
        String to_id = paramsMap.get("to_id");
        if (StringUtils.isEmpty(visitor_id) || StringUtils.isEmpty(to_id)) {
            log.info("访客ws参数为空");
            return;
        }
        Visitor visitor = visitorService.getByVisitorId(visitor_id);
        if (visitor == null) {
            log.info("访客visitorId不存在:{}", visitor_id);
            return;
        }
        this.visitor_id = visitor_id;
        this.session = session;
        if (!clients.containsKey(visitor_id)) {
            addOnlineCount();
        }
        clients.put(visitor_id, this);
        log.info(visitor_id + "加入webSocket！当前人数为" + onlineNum);

    }

    //关闭连接时调用
    @OnClose
    public void onClose(Session session) {
        String queryString = session.getQueryString();
        if (StringUtils.isEmpty(queryString)) return;
        Map<String, String> paramsMap = parseParams(queryString);
        String visitor_id = paramsMap.get("visitor_id");
        if (clients.containsKey(visitor_id)) {
            clients.remove(visitor_id);
            subOnlineCount();
            log.info(visitor_id + "断开webSocket连接！当前人数为" + onlineNum);
        }
    }

    //收到客户端信息后，根据接收人的username把消息推下去或者群发
    // to=-1群发消息
    @OnMessage
    public void onMessage(String message) throws IOException {
        log.info("onMessage server get message:" + message);
        if (message.equals("ping")) return;
        Message msg = JSONUtil.toBean(message, Message.class);

        String msgType = msg.getType();
        Object data = msg.getData();
        switch (msgType) {
            case "ping":
                Map<String, Object> pongContent = new HashMap<>();
                pongContent.put("type", "pong");
                pongContent.put("data", null);
//                {"type":"ping","data":"visitor:13|535e92df-06a7-4718-a288-30c1366fb8da"}
                String visitorId = ((String) data).substring(8);
                sendMessageTo(visitorId, JSON.toJSONString(msg, true));
                break;
            case "inputing":
                String from = ((JSONObject) data).get("from", String.class);
                String to = ((JSONObject) data).get("to", String.class);
                sendMessageTo(to, JSON.toJSONString(msg, true));
                break;
        }
    }

    //错误时调用
    @OnError
    public void onError(Session session, Throwable throwable) {
        log.info("发生错误");
        throwable.printStackTrace();
    }

    //给指定用户发送信息
    public void sendMessageTo(String to, String message) {

        for (VisitorSocketServer item : clients.values()) {
            if (item.visitor_id.contains(to)) {
                item.session.getAsyncRemote().sendText(message);
            }
        }
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

    public static Map<String, VisitorSocketServer> getClients() {
        return clients;
    }

    public static void setClients(Map<String, VisitorSocketServer> clients) {
        VisitorSocketServer.clients = clients;
    }
}

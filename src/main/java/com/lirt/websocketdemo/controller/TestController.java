package com.lirt.websocketdemo.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@ServerEndpoint("/chat")
@Slf4j
public class TestController {

    /**
     * 全部在线会话  PS: 基于场景考虑 这里使用线程安全的Map存储会话对象。
     */
    private static Map<String, Session> onlineSessions = new ConcurrentHashMap<>();

    @OnOpen
    public void onopen (Session session) {
        log.info("登录一个用户: {}", session.getId());
        onlineSessions.put(session.getId(), session);
        sendMessageToAll("有一用户登录, 当前在线有：" + onlineSessions.size() + "人");
    }
    @OnMessage
    public void onMessage(Session session, @RequestParam("msg")String msg) {
        log.info("{} 用户发来消息：{}", session.getId(), msg);
        sendMessageToAll(msg);
    }

    /**
     * 当关闭连接：1.移除会话对象 2.更新在线人数
     */
    @OnClose
    public void onClose(Session session) {
        onlineSessions.remove(session.getId());
        log.info("有一用户退出, 当前在线有：" + onlineSessions.size() + "人");
    }

    /**
     * 当通信发生异常：打印错误日志
     */
    @OnError
    public void onError(Session session, Throwable error) {
        error.printStackTrace();
    }

    /**
     * 公共方法：发送信息给所有人
     */
    private static void sendMessageToAll(String msg) {
        onlineSessions.forEach((id, session) -> {
            try {
                session.getBasicRemote().sendText(msg);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public static void sendMessageToAll(String msg, Map<String, Session> onlineSessions) {
        onlineSessions.forEach((id, session) -> {
            try {
                session.getBasicRemote().sendText(msg);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public static Map<String, Session> copySessions() {
        return onlineSessions;
    }
}

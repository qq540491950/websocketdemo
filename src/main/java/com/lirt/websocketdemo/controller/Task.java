package com.lirt.websocketdemo.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.websocket.Session;
import java.util.Map;

@EnableScheduling
@Slf4j
@Component
public class Task {

    int num = 0;

    @Scheduled(cron = "0/1 * * * * *")
    public void sendMsg() {
        Map<String, Session> onlineSession = TestController.copySessions();
        if (num == 100) {
            TestController.sendMessageToAll(String.valueOf(0),onlineSession);
            log.info("发送消息: {}", String.valueOf(num));
            num = 0;
        } else {
            TestController.sendMessageToAll(String.valueOf(num), onlineSession);
            log.info("发送消息: {}", String.valueOf(num));
            num += 1;
        }
    }
}

package com.example.graduation_project.config;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private static final Map<Long, WebSocketSession> ONLINE_USERS = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Long userId = (Long) session.getAttributes().get("userId");
        ONLINE_USERS.put(userId, session);
        log.info("User {} connected", userId);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        Long userId = (Long) session.getAttributes().get("userId");
        String payload = message.getPayload();
        log.info("Received message from user {}: {}", userId, payload);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        Long userId = (Long) session.getAttributes().get("userId");
        ONLINE_USERS.remove(userId);
        log.info("User {} disconnected", userId);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        Long userId = (Long) session.getAttributes().get("userId");
        ONLINE_USERS.remove(userId);
        log.error("User {} connection error", userId, exception);
    }

    public void sendMessageToUser(Long userId, Object message) {
        WebSocketSession session = ONLINE_USERS.get(userId);
        if (session != null && session.isOpen()) {
            try {
                log.info("Sending message to user {}", userId);
                session.sendMessage(new TextMessage(JSONUtil.toJsonStr(message)));
            } catch (IOException e) {
                log.error("Failed to send message to user {}", userId, e);
            }
        } else {
            log.info("User {} is not online", userId);
        }
    }
}

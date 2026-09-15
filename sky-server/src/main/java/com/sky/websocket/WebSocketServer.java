package com.sky.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * WebSocket服务，用于向管理端主动推送订单消息。
 */
@Component
@ServerEndpoint("/ws/{sid}")
@Slf4j
public class WebSocketServer {

    private static final Map<String, Session> SESSION_MAP = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session, @PathParam("sid") String sid) {
        SESSION_MAP.put(sid, session);
        log.info("WebSocket连接建立：sid={}", sid);
    }

    @OnClose
    public void onClose(@PathParam("sid") String sid) {
        SESSION_MAP.remove(sid);
        log.info("WebSocket连接关闭：sid={}", sid);
    }

    @OnMessage
    public void onMessage(String message, @PathParam("sid") String sid) {
        log.info("收到WebSocket消息：sid={}, message={}", sid, message);
    }

    @OnError
    public void onError(Session session, Throwable error, @PathParam("sid") String sid) {
        SESSION_MAP.remove(sid);
        log.error("WebSocket连接异常：sid={}", sid, error);
    }

    /**
     * 向所有已连接的客户端发送消息。
     */
    public void sendToAllClient(String message) {
        for (Map.Entry<String, Session> entry : SESSION_MAP.entrySet()) {
            Session session = entry.getValue();
            if (!session.isOpen()) {
                SESSION_MAP.remove(entry.getKey(), session);
                continue;
            }

            try {
                session.getBasicRemote().sendText(message);
            } catch (IOException e) {
                log.error("WebSocket消息发送失败：sid={}", entry.getKey(), e);
            }
        }
    }
}

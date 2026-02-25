package com.glowxq.websocket.sever;

import com.glowxq.core.common.entity.SocketMessage;
import com.glowxq.core.common.entity.WsSession;
import com.glowxq.core.common.feishu.utils.FeishuMessageUtils;
import com.glowxq.core.util.JsonUtils;
import com.glowxq.core.util.SocketUtil;
import com.glowxq.redis.WebsocketRedisService;
import com.glowxq.websocket.cache.SocketManagerCache;
import com.glowxq.websocket.cts.ClientToServiceHandler;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.glowxq.websocket.cache.SocketManagerCache.LOGIN_ID;

@Component
@Slf4j
@RequiredArgsConstructor
public class WebSocketServer extends TextWebSocketHandler {

    protected final WebsocketRedisService websocketRedisService;

    protected final ServerState serverState;

    protected final List<ClientToServiceHandler> clientToServiceHandlers;

    /**
     * 连接建立后
     *
     * @param session session
     * @throws Exception e
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        if (serverState.isShuttingDown()) {
            log.warn("Service is shutting down. Rejecting new connection.");
            session.close(CloseStatus.SERVICE_RESTARTED);
            return;
        }
        String sid = session.getId();
        String loginId = (String) session.getAttributes().get(LOGIN_ID);
        WsSession wsSession = new WsSession(sid, loginId, session);

        SocketManagerCache.onlineSessionMap.put(sid, wsSession);
        SocketManagerCache.addOnlineSid(loginId, sid);
        websocketRedisService.addUserToOnlineChat(sid, loginId);
        log.info("当前连接数:" + websocketRedisService.getConnectionCount());
        log.info("当前在线人数: " + websocketRedisService.getOnlineUserCount());
        log.info("当前内存中的用户: " + JsonUtils.toJsonString(websocketRedisService.getAllOnlineUsernames()));
    }

    /**
     * 接受客户端消息
     *
     * @param session session
     * @param message message
     * @throws IOException e
     */
    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) {
        if (serverState.isShuttingDown()) {
            log.warn("Service is shutting down. Ignoring new messages.");
            return;
        }
        String sessionId = session.getId();
        WsSession wsSession = SocketManagerCache.onlineSessionMap.get(sessionId);
        if (wsSession == null) {
            log.warn("【websocket消息】收到客户端消息，但未找到对应的sessionId:{}", sessionId);
            FeishuMessageUtils.sendInternalMessage("【websocket消息】收到客户端消息，但未找到对应的sessionId:{%s}".formatted(sessionId));
            return;
        }
        SocketMessage socketMessage = JsonUtils.parseObject(message.getPayload(), SocketMessage.class);
        log.info("【websocket消息】收到客户端消息，sessionId:{}, message:{}", sessionId, socketMessage);
        clientToServiceHandlers.forEach(handler -> handler.handlerMessage(wsSession, socketMessage));
    }

    /**
     * 连接关闭后
     *
     * @param session session
     * @param status  status
     * @throws Exception e
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        if (serverState.isShuttingDown()) {
            log.warn("Service is shutting down. Skipping cleanup for closed connection.");
            return;
        }
        String loginId = (String) session.getAttributes().get(LOGIN_ID);
        log.info("【websocket消息】连接断开，loginId:[{}]", loginId);
        if (session.isOpen()) {
            try {
                String sid = session.getId();
                websocketRedisService.removeUserBySessionId(sid);
                // 注意删除顺序,先清除WSSession的Map
                SocketManagerCache.removeUserSession(sid);
                SocketManagerCache.onlineSessionMap.remove(sid);
                session.close();
            } catch (IOException e) {
                log.error("【websocket消息】连接断开异常，error", e);
                throw new IllegalArgumentException(e);
            }
        }
    }

    /**
     * 根据loginId 推送消息
     *
     * @param loginIds      登陆用户Id集合
     * @param socketMessage 消息对象
     */
    @SneakyThrows
    public <T> void sendMessage(SocketMessage<T> socketMessage, List<String> loginIds) {
        log.info(" 定向推送。推送用户范围:{}, message: {}", loginIds, JsonUtils.toJsonString(socketMessage));
        for (String loginId : loginIds) {
            // 验证当前内存中【用户】是否存在
            boolean existsUsername = SocketManagerCache.onlineUserSessionIdMap.containsKey(loginId);
            if (existsUsername) {
                List<String> notifyUserSids = SocketManagerCache.onlineUserSessionIdMap.get(loginId);
                for (String notifyUserSid : notifyUserSids) {
                    // 验证当前内存中【session】是否存在
                    boolean existsUserSession = SocketManagerCache.onlineSessionMap.containsKey(notifyUserSid);
                    if (existsUserSession) {
                        WsSession wsSession = SocketManagerCache.onlineSessionMap.get(notifyUserSid);
                        wsSession.getSession().sendMessage(new TextMessage(SocketUtil.transferMessage(socketMessage)));
                    }
                    else {
                        log.info(" websocket定向推送。message: {}。用户:{}推送失败", JsonUtils.toJsonString(socketMessage), loginId);
                    }
                }
            }
        }
    }

    /**
     * 根据loginId 推送消息
     *
     * @param socketMessage 消息对象
     */
    @SneakyThrows
    public <T> void sendMessageToAllUser(SocketMessage<T> socketMessage) {
        log.info(" 全员推送。message: {}", JsonUtils.toJsonString(socketMessage));
        List<String> allOnlineUsernames = new ArrayList<>(SocketManagerCache.onlineUserSessionIdMap.keySet());
        for (String loginId : allOnlineUsernames) {
            List<String> notifyUserSids = SocketManagerCache.onlineUserSessionIdMap.get(loginId);
            for (String notifyUserSid : notifyUserSids) {
                boolean existsUserSession = SocketManagerCache.onlineSessionMap.containsKey(notifyUserSid);
                if (existsUserSession) {
                    WsSession wsSession = SocketManagerCache.onlineSessionMap.get(notifyUserSid);
                    wsSession.getSession().sendMessage(new TextMessage(SocketUtil.transferMessage(socketMessage)));
                }
                else {
                    log.info(" websocket全员推送。message: {}。用户:{}推送失败", JsonUtils.toJsonString(socketMessage), loginId);
                }
            }
        }
    }

    public void disconnectAll() {
        ConcurrentHashMap<String, WsSession> onlineSessionMap = SocketManagerCache.onlineSessionMap;
        for (Map.Entry<String, WsSession> sessionEntry : onlineSessionMap.entrySet()) {
            String sid = sessionEntry.getKey();
            WsSession wsSession = sessionEntry.getValue();
            // 清理 redis
            websocketRedisService.removeUserBySessionId(sid);
            // 断开websocket 连接 ...
            WebSocketSession session = wsSession.getSession();
            if (session != null) {
                try {
                    wsSession.getSession().close(CloseStatus.SERVICE_RESTARTED);
                    log.info(" 优雅退出，关闭 websocket 连接 ...");
                } catch (IOException e) {
                    log.error("【websocket消息】连接断开异常，error", e);
                    throw new IllegalArgumentException(e);
                }
            }
        }
    }
}

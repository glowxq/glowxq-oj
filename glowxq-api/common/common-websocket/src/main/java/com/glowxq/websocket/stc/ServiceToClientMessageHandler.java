package com.glowxq.websocket.stc;

import com.glowxq.core.common.entity.SocketMessage;
import com.glowxq.core.common.entity.TransferMessage;
import com.glowxq.core.common.enums.SocketChannelEnum;
import com.glowxq.core.util.JsonUtils;
import com.glowxq.websocket.sever.WebSocketServer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

/**
 * 在业务层接收订阅额消息，并结合业务进行处理
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ServiceToClientMessageHandler implements com.glowxq.redis.handler.ServiceToClientMessageHandler {

    private final WebSocketServer webSocketServer;

    @Override
    @Retryable(retryFor = Exception.class, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    public <T> void handleTransferMessage(TransferMessage<T> tm) {
        log.info(" glowxq-service-websocket [service-to-ws] tm = {}", JsonUtils.toJsonString(tm));
        SocketMessage<T> tmMessage = tm.getMessage();
        SocketChannelEnum channel = tmMessage.getChannel();
        switch (tmMessage.getScope()) {
            case SERVER: // 通知到后台服务端
                if (SocketChannelEnum.CLOSE == channel) {
                    // todo ...
                }
                break;
            case SOCKET_CLIENT: // 通知到socket客户端，即浏览器、移动端等
                // 推送给全体用户
                if (tm.isToPushAll()) {
                    webSocketServer.sendMessageToAllUser(tm.getMessage());
                }
                // 推送给指定用户
                else {
                    webSocketServer.sendMessage(tm.getMessage(), tm.getToUsers());
                }
                break;
            case SOCKET_SERVER:
                // todo something ..
                break;
            default:
                break;
        }
    }
}

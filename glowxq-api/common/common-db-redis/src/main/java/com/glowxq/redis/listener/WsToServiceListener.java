package com.glowxq.redis.listener;

import com.glowxq.core.common.entity.TransferMessage;
import com.glowxq.core.util.JsonUtils;
import com.glowxq.redis.handler.WsToServiceMsgHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * redis消息listener, 用于监听websocket to service消息的推送
 *
 * @author glowxq
 * @since 2023/9/8 10:12
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class WsToServiceListener implements MessageListener {

    private final List<WsToServiceMsgHandler> messageHandlers;

    private final RedisTemplate<Object, Object> redisTemplate;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        log.info(" [ws-to-service] message = {}", JsonUtils.toJsonString(message));
        TransferMessage tm = (TransferMessage) redisTemplate.getValueSerializer().deserialize(message.getBody());
        log.info(" [ws-to-service] tm = {}", JsonUtils.toJsonString(tm));
        // 调用所有实现了TransferMessageHandler接口的处理器
        for (WsToServiceMsgHandler handler : messageHandlers) {
            handler.handlerMsg(tm);
        }
    }

}

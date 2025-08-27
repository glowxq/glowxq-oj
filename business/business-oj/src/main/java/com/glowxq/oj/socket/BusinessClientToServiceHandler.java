package com.glowxq.oj.socket;

import com.glowxq.core.common.entity.SocketMessage;
import com.glowxq.core.common.entity.WsSession;

public interface BusinessClientToServiceHandler {

    /**
     * 处理业务消息
     *
     * @param wsSession
     * @param socketMessage
     * @param <T>
     */
    <T> void handlerMessageBusiness(WsSession wsSession, SocketMessage<T> socketMessage);
}

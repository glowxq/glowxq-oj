package com.glowxq.oj.socket;

import com.glowxq.core.common.entity.SocketMessage;
import com.glowxq.core.common.entity.WsSession;

public abstract class BaseBusinessClientToServiceHandler implements BusinessClientToServiceHandler {

    @Override
    public <T> void handlerMessageBusiness(WsSession wsSession, SocketMessage<T> socketMessage) {
        Thread.ofVirtual().start(() -> {
            this.doBusiness(wsSession, socketMessage);
        });
    }

    /**
     * 业务处理
     *
     * @param session
     * @param socketMessage
     */
    protected abstract void doBusiness(WsSession session, SocketMessage socketMessage);
}

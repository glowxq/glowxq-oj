package com.glowxq.websocket.cts;

import com.glowxq.core.common.entity.SocketMessage;
import com.glowxq.core.common.entity.WsSession;

public interface ClientToServiceHandler {

    void handlerMessage(WsSession wsSession, SocketMessage socketMessage);
}

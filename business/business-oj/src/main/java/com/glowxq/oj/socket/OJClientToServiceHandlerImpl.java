package com.glowxq.oj.socket;

import com.glowxq.core.common.entity.SocketMessage;
import com.glowxq.core.common.entity.WsSession;
import com.glowxq.websocket.cts.BaseClientToServiceHandlerImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class OJClientToServiceHandlerImpl extends BaseClientToServiceHandlerImpl {

    private final ClientToServiceBusinessWebsocketFactory clientToServiceBusinessWebsocketFactory;

    @Override
    public void handlerMessage(WsSession wsSession, SocketMessage socketMessage) {
        BusinessClientToServiceHandler websocketHandler = clientToServiceBusinessWebsocketFactory.getWebsocketHandler(socketMessage.getBusinessType());
        websocketHandler.handlerMessageBusiness(wsSession, socketMessage);
    }
}

package com.glowxq.platform.handler;

import com.glowxq.core.common.entity.TransferMessage;
import com.glowxq.core.util.JsonUtils;
import com.glowxq.redis.handler.WsToServiceMsgHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class WebsocketMessageHandler implements WsToServiceMsgHandler {

    @Override
    public <T> void handlerMsg(TransferMessage<T> transferMessage) {
        // [do something ...] 在业务层接收透传过来的 websocket信息，进行业务处理
        log.info(" [WsToService] transferMessage = {}", JsonUtils.toJsonString(transferMessage));
    }

}

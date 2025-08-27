package com.glowxq.oj.socket;

import com.glowxq.core.common.enums.WebsocketBusinessType;
import com.glowxq.core.util.SpringUtils;
import org.springframework.stereotype.Component;

@Component
public class ClientToServiceBusinessWebsocketFactory {

    public BusinessClientToServiceHandler getWebsocketHandler(WebsocketBusinessType businessBindType) {
        switch (businessBindType) {
            case CodeMonitor -> {
                return SpringUtils.getBean(CodeMonitorBusinessClientToServiceHandler.class);
            }
        }
        return null;
    }
}

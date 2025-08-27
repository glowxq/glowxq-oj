package com.glowxq.websocket.configuration;

import com.glowxq.websocket.sever.WebSocketServer;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final WebSocketServer webSocketServer;

    @Resource
    private WebSocketInterceptor webSocketInterceptor;

    public WebSocketConfig(WebSocketServer webSocketServer) {
        this.webSocketServer = webSocketServer;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry webSocketHandlerRegistry) {
        webSocketHandlerRegistry
                // 添加myHandler消息处理对象，和websocket访问地址
                .addHandler(webSocketServer, "/socket")
                // 设置允许跨域访问
                .setAllowedOrigins("*") // ！！！【建议指定域名，不要全部开放。】
                // 添加拦截器可实现用户链接前进行权限校验等操作
                .addInterceptors(webSocketInterceptor);
    }
}

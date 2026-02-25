package com.glowxq.core.common.entity;

import com.glowxq.core.common.enums.MessageTransferScopeEnum;
import com.glowxq.core.common.enums.SocketChannelEnum;
import com.glowxq.core.common.enums.WebsocketBusinessType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author glowxq
 * @since 2023/9/6 17:20
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SocketMessage<T> {

    @Schema(description = "data")
    protected T data;

    @Schema(description = "消息类型")
    protected WebsocketBusinessType businessType;

    @Schema(description = "通道类型")
    protected SocketChannelEnum channel = SocketChannelEnum.DEFAULTS;

    @Schema(description = "消息通知作用域")
    protected MessageTransferScopeEnum scope = MessageTransferScopeEnum.SOCKET_CLIENT;


    public SocketMessage(T data,WebsocketBusinessType businessType) {
        this.data = data;
        this.businessType = businessType;
    }
}

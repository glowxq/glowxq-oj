package com.glowxq.core.util;

import com.glowxq.core.common.entity.SocketMessage;
import com.glowxq.core.common.entity.SocketResult;
import com.glowxq.core.common.entity.TransferMessage;

/**
 * @author glowxq
 * @since 2023/9/6 17:27
 */
public class SocketUtil {

    public static SocketMessage formatSocketMessage(String message) {
        return JsonUtils.parseObject(message, SocketResult.class);
    }

    public static <T> String transferMessage(SocketMessage<T> bean) {
        return JsonUtils.toJsonString(bean);
    }

    public static <T> TransferMessage<T> pubTransferMessage(SocketMessage<T> sb, String... usernames) {
        TransferMessage<T> transferMessage = new TransferMessage<>();
        transferMessage.setMessage(sb);
        transferMessage.toUsers(usernames);
        return transferMessage;
    }

}

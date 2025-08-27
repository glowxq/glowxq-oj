package com.glowxq.core.util.http;

import lombok.Data;

/**
 * @author glowxq
 * @version 1.0
 * @date 2024/4/30
 */
@Data
public class TimeoutConfigure {

    /**
     * 连接超时
     */
    private int connectTimeout;

    /**
     * 读取超时
     */
    private int readTimeout;

    /**
     * 写入超时
     */
    private int writeTimeout;

    public TimeoutConfigure() {
    }

    public TimeoutConfigure(int timeout) {
        this.connectTimeout = timeout;
        this.readTimeout = timeout;
        this.writeTimeout = timeout;
    }

    public TimeoutConfigure(int connectTimeout, int readTimeout, int writeTimeout) {
        this.connectTimeout = connectTimeout;
        this.readTimeout = readTimeout;
        this.writeTimeout = writeTimeout;
    }


}

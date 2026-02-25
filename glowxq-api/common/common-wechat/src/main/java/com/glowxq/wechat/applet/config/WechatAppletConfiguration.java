package com.glowxq.wechat.applet.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author glowxq
 * @version 1.0
 * @date 2024/7/29
 */
@Configuration
@ConfigurationProperties(prefix = "wechat.applet")
@Data
public class WechatAppletConfiguration {

    /**
     * appId
     */
    private String appId;

    /**
     * 密钥
     */
    private String secret;

    /**
     * 令 牌
     */
    private String token;

    /**
     * AES 密钥
     */
    private String aesKey;

    /**
     * 消息数据格式
     */
    private String msgDataFormat;
}

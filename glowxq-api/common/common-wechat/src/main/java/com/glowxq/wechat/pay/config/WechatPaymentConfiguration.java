package com.glowxq.wechat.pay.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author glowxq
 * @version 1.0
 * @date 2024/7/29
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "wechat.payment")
public class WechatPaymentConfiguration {

    private String appId;

    private String mchId;

    private String mchKey;

    private String subAppId;

    private String subMchId;

    private String notifyUrl;

    private String payNotifyPath;

    private String refundNotifyPath;

    private String apiV3Key;

    private String privateKeyPath;

    private String privateCertPath;

    private String keyPath;
}

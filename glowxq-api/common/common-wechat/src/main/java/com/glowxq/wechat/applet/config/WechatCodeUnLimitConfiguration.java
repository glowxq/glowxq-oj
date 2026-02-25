package com.glowxq.wechat.applet.config;

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
@ConfigurationProperties(prefix = "wechat.wxa-code-unlimit")
public class WechatCodeUnLimitConfiguration {

    private String environment;

    private String page;

    private String scene;
}

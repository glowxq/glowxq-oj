package com.glowxq.core.common.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author glowxq
 * @version 1.0
 * @date 2024/6/4
 */
@Data
@ConfigurationProperties(prefix = "app")
@Configuration
public class AppConfig {

    /**
     * 应用名
     */
    private String name;

    /**
     * 业务
     */
    private String business = "system";

    /**
     * 环境
     */
    private String environment;

    /**
     * 版本
     */
    private String version;
}

package com.glowxq.security.core;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author glowxq
 * @version 1.0
 * @since 2025/1/6 10:02
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "web.cors")
public class CorsProperties {

    private CopyOnWriteArrayList<String> allowedOrigins;
}

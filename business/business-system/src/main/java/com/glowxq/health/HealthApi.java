package com.glowxq.health;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author glowxq
 * @version 1.0
 * @date 2025/7/30
 */
@Slf4j
@RestController
public class HealthApi {

    /**
     * 健康检查
     */
    @GetMapping("/health")
    public void healthCheck() {
        log.info("health");
    }
}

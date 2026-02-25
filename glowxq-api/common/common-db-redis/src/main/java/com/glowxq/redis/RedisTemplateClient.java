package com.glowxq.redis;

import org.springframework.data.redis.core.RedisTemplate;

/**
 * @author glowxq
 * @since 2024/2/29 9:47
 */
public interface RedisTemplateClient {

    RedisTemplate<Object, Object> getTemplate();
}

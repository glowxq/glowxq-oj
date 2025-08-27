package com.glowxq.core.datascope;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * 数据权限配置类
 * <p>
 * 启用AOP和组件扫描，自动注册数据权限相关的组件。
 * </p>
 *
 * @author glowxq
 * @since 2024/7/12
 */
@Configuration
@EnableAspectJAutoProxy
@ComponentScan("com.glowxq.core.datascope")
public class DataScopeConfig {
    // 配置类无需额外内容，主要用于启用组件扫描和AOP
}

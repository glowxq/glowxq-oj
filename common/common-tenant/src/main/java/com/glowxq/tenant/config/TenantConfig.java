package com.glowxq.tenant.config;

import com.mybatisflex.core.tenant.TenantFactory;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * 租户配置类 - 提供多租户功能的核心配置
 * <p>
 * 该配置类负责：
 * 1. 读取应用配置文件中的租户相关设置
 * 2. 注册租户工厂Bean用于多租户数据隔离
 * 3. 提供忽略表名列表配置，用于排除不需要租户过滤的表
 * </p>
 *
 * @author glowxq
 * @version 1.0
 * @date 2025/6/5
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "glowxq.tenant")
public class TenantConfig {

    /**
     * 需要忽略租户过滤的表名列表
     * <p>
     * 这些表在查询时将不添加租户条件，通常用于系统表、配置表等全局共享的数据
     * </p>
     */
    private List<String> ignoreTable = new ArrayList<>();

    /**
     * 是否启用多租户功能
     * <p>
     * 默认为true，设置为false时将完全禁用多租户功能
     * </p>
     */
    private Boolean enable = Boolean.FALSE;

    /**
     * 多租户工厂Bean
     * <p>
     * 创建并配置租户工厂，该工厂负责在SQL执行时添加租户条件
     * </p>
     *
     * @return 配置好的租户工厂实例
     */
    @Bean
    public TenantFactory tenantFactory() {
        GlowxqTenantFactory factory = new GlowxqTenantFactory();
        factory.setIgnoreTables(ignoreTable);
        factory.setEnable(enable);
        return factory;
    }
}

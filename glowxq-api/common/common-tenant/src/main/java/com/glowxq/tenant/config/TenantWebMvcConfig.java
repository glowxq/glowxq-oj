package com.glowxq.tenant.config;

import com.glowxq.tenant.interceptor.TenantInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 租户Web配置类 - 配置租户相关的Web拦截器
 * <p>
 * 该配置类负责注册租户拦截器，用于从HTTP请求中提取租户信息。
 * 只有在glowxq.tenant.enable=true时才会启用此配置。
 * </p>
 *
 * @author glowxq
 * @version 1.0
 * @date 2025/6/5
 */
@Slf4j
@Configuration
public class TenantWebMvcConfig implements WebMvcConfigurer {

    /**
     * 添加拦截器
     * <p>
     * 注册TenantInterceptor拦截器，用于提取HTTP请求中的租户ID
     * 并将其设置到TenantContext上下文中
     * </p>
     *
     * @param registry 拦截器注册表
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        log.info("注册租户拦截器，拦截路径：/**");
        registry.addInterceptor(new TenantInterceptor())
                .addPathPatterns("/**");
    }
}

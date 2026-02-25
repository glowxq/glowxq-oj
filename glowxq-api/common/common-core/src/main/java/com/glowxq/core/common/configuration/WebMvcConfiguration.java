package com.glowxq.core.common.configuration;

import com.glowxq.core.common.conver.StringToLocalDateConverter;
import com.glowxq.core.common.conver.StringToLocalDateTimeConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * @author glowxq
 * @version 1.0
 * @since 2022/8/29
 */
@Slf4j
@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {

    private final ObjectProvider<HttpMessageConverters> messageConverters;

    public WebMvcConfiguration(ObjectProvider<HttpMessageConverters> messageConverters) {
        this.messageConverters = messageConverters;
    }

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
        log.info("顶层租户拦截器开始执行……");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**").addResourceLocations("classpath:/static/", "classpath:/META-INF/resources/");
    }

    /**
     * 两种解决方案: - 一、 使用 WebMvcConfigurer 而非WebMvcConfigurationSupport - 二、 使用此方法
     * <p>
     * 参考WebMvcAutoConfiguration类中configureMessageConverters方法，使Jackson配置生效
     *
     * @param converters 消息转换器
     */
    @Override
    public void configureMessageConverters(@NonNull List<HttpMessageConverter<?>> converters) {
        this.messageConverters.ifAvailable(customConverters -> converters.addAll(customConverters.getConverters()));
    }

    /**
     * 添加自定义格式化器
     * <p>
     * 主要解决前端传递带时间的日期字符串（如 "2025-06-01 00:00:00"）
     * 转换为 LocalDate 类型的问题
     * </p>
     *
     * @param registry 格式化器注册表
     */
    @Override
    public void addFormatters(FormatterRegistry registry) {
        // 添加自定义的 LocalDate 转换器
        registry.addConverter(new StringToLocalDateConverter());
        registry.addConverter(new StringToLocalDateTimeConverter());
        log.info("注册自定义 LocalDate  LocalDateTime 格式化器，支持带时间的日期字符串转换");
    }
}
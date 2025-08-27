package com.glowxq.oj.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;

import java.io.IOException;

/**
 * 前端静态资源配置
 * 支持Vue3 SPA应用的路由
 * 
 * @author glowxq
 * @since 2025/1/9
 */
@Slf4j
@Configuration
public class FrontendConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 配置静态资源处理
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/")
                .resourceChain(true)
                .addResolver(new PathResourceResolver() {
                    @Override
                    protected Resource getResource(String resourcePath, Resource location) throws IOException {
                        Resource requestedResource = location.createRelative(resourcePath);
                        
                        // 如果请求的资源存在，直接返回
                        if (requestedResource.exists() && requestedResource.isReadable()) {
                            return requestedResource;
                        }
                        
                        // 如果是API请求，不处理
                        if (resourcePath.startsWith("api/")) {
                            return null;
                        }
                        
                        // 对于Vue3 SPA路由，返回index.html
                        Resource indexHtml = new ClassPathResource("static/index.html");
                        if (indexHtml.exists() && indexHtml.isReadable()) {
                            log.debug("Serving index.html for SPA route: {}", resourcePath);
                            return indexHtml;
                        }
                        
                        return null;
                    }
                });
        
        log.info("前端静态资源配置完成，支持Vue3 SPA路由");
    }
}

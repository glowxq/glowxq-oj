package com.glowxq.security.config;

import cn.dev33.satoken.jwt.StpLogicJwtForSimple;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.strategy.SaAnnotationStrategy;
import com.glowxq.security.core.MySaCheckPermissionHandler;
import com.glowxq.security.core.interceptor.MySaInterceptor;
import com.glowxq.security.pojo.WhitelistProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;

/**
 * @author glowxq
 * @since 2024/1/22 16:36
 */

@Slf4j
@Configuration
public class SaTokenConfig implements WebMvcConfigurer {

    private final WhitelistProperties whitelistProperties;

    public SaTokenConfig(WhitelistProperties whitelistProperties) {
        this.whitelistProperties = whitelistProperties;
    }

    @Bean
    public StpLogic getStpLogicJwt() {
        // Sa-Token 整合 jwt (简单模式)
        return new StpLogicJwtForSimple();
    }

    /**
     * 注册 Sa-Token 路由拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        log.info("注册 Sa-Token 路由拦截器");
        // 注册自定义 @SaCheckPermission 注解Handler;
        SaAnnotationStrategy.instance.registerAnnotationHandler(new MySaCheckPermissionHandler());
        // 注册 自定义 MySaInterceptor 拦截器
        registry.addInterceptor(new MySaInterceptor(handler -> {
                    // 这里可以结合自己业务改造
                    SaRouter.match("/**", r -> StpUtil.checkLogin());
                }))
                .addPathPatterns("/**").excludePathPatterns(new ArrayList<>(whitelistProperties.getWhitelist()));
    }
}

package com.glowxq.generator.pojo.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author glowxq
 * @since 2024/1/15 8:38
 */
@Data
@Component
@ConfigurationProperties(prefix = "web.generator")
public class GeneratorProperties {

    // 初始值配置
    private PathProperties path;

    // 全局配置
    private GlobalProperties global;

    // 模块名
    private String moduleName ;

    // 是否覆盖
    private Boolean overwrite = true;

    // service 名
    private String serviceName ;

    @Data
    public static class GlobalProperties {

        // 作者
        private String author = "glowxq";

        // 包名
        private String packages = "com.glowxq";
    }

    @Data
    public static class PathProperties {

        // 前端项目初始路径
        private String web;

        // 后端项目初始路径
        private String api;
    }

}

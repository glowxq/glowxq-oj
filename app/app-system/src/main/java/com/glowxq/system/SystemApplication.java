package com.glowxq.system;

import com.glowxq.core.common.constant.Constant;
import com.glowxq.mysql.FlywayProperties;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.retry.annotation.EnableRetry;

@EnableRetry
@ComponentScan(basePackages = {Constant.BASE_PACKAGE})
@SpringBootApplication
@EnableAspectJAutoProxy
@RequiredArgsConstructor
public class SystemApplication {

    @Getter
    private static String version;

    private final FlywayProperties flywayProperties;

    private final Flyway frameworkFlyway;

    private final Flyway businessFlyway;

    @Value("${app.version}")
    private String appVersion;

    private static void setVersion(String appVersion) {
        SystemApplication.version = appVersion;
    }

    public static void main(String[] args) {
        SpringApplication.run(SystemApplication.class, args);
        String template = """
           ------------------%s  (v%s)-------------------
           """;
        String result = String.format(template, "glowxq-system", getVersion());
        System.out.println(result);
    }

    @PostConstruct
    public void init() {
        setVersion(appVersion); // 通过辅助方法设置静态字段
        FlywayProperties.FlywayConfig business = flywayProperties.getBusiness();
        FlywayProperties.FlywayConfig framework = flywayProperties.getFramework();
        if (framework.isEnabled()) {
            frameworkFlyway.migrate();
        }
        if (business.isEnabled()) {
            businessFlyway.migrate();
        }
    }
}
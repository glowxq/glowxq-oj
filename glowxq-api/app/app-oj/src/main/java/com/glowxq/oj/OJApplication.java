package com.glowxq.oj;

import com.glowxq.core.common.configuration.AppConfig;
import com.glowxq.core.common.constant.Constant;
import com.glowxq.core.util.SpringApplicationContextUtils;
import com.glowxq.mysql.FlywayProperties;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.flywaydb.core.Flyway;
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
public class OJApplication {

    private static AppConfig appConfig;

    private final FlywayProperties flywayProperties;

    private final Flyway frameworkFlyway;

    private final Flyway businessFlyway;

    public static void main(String[] args) {
        SpringApplication.run(OJApplication.class, args);
        String template = """
                             ___      _                             __ _                       (_)                     _ __     _   \s
                            / __|    | |     ___   __ __ __ __ __  / _` |     o O O   ___      | |     o O O  __ _    | '_ \\   (_)  \s
                           | (_ |    | |    / _ \\  \\ V  V / \\ \\ /  \\__, |    o       / _ \\    _/ |    o      / _` |   | .__/   | |  \s
                            \\___|   _|_|_   \\___/   \\_/\\_/  /_\\_\\   __|_|   TS__[O]  \\___/   |__/_   TS__[O] \\__,_|   |_|__   _|_|_ \s
                          _|""\"""|_|""\"""|_|""\"""|_|""\"""|_|""\"""|_|""\"""| {======|_|""\"""|_|""\"""| {======|_|""\"""|_|""\"""|_|""\"""|\s
                          "`-0-0-'"`-0-0-'"`-0-0-'"`-0-0-'"`-0-0-'"`-0-0-'./o--000'"`-0-0-'"`-0-0-'./o--000'"`-0-0-'"`-0-0-'"`-0-0-'\s
                                                    ------------------ [%s]  env:%s (v%s) -------------------
                          """;
        String result = String.format(template, appConfig.getName(), appConfig.getEnvironment(), appConfig.getVersion());
        System.out.println(result);
    }

    public void setAppConfig(AppConfig appConfig) {
        OJApplication.appConfig = appConfig;
    }

    @PostConstruct
    public void init() {
        AppConfig appConfig = SpringApplicationContextUtils.getInstance().getBean(AppConfig.class);
        setAppConfig(appConfig); // 通过辅助方法设置静态字段
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
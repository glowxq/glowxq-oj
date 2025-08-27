package com.glowxq.oj;

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
public class OJApplication {

    @Getter
    private static String version;

    private final FlywayProperties flywayProperties;

    private final Flyway frameworkFlyway;

    private final Flyway businessFlyway;

    @Value("${app.version}")
    private String appVersion;

    private static void setVersion(String appVersion) {
        OJApplication.version = appVersion;
    }

    public static void main(String[] args) {
        SpringApplication.run(OJApplication.class, args);
        String template = """
                             ___      _                             __ _                       (_)                     _ __     _   \s
                            / __|    | |     ___   __ __ __ __ __  / _` |     o O O   ___      | |     o O O  __ _    | '_ \\   (_)  \s
                           | (_ |    | |    / _ \\  \\ V  V / \\ \\ /  \\__, |    o       / _ \\    _/ |    o      / _` |   | .__/   | |  \s
                            \\___|   _|_|_   \\___/   \\_/\\_/  /_\\_\\   __|_|   TS__[O]  \\___/   |__/_   TS__[O] \\__,_|   |_|__   _|_|_ \s
                          _|""\"""|_|""\"""|_|""\"""|_|""\"""|_|""\"""|_|""\"""| {======|_|""\"""|_|""\"""| {======|_|""\"""|_|""\"""|_|""\"""|\s
                          "`-0-0-'"`-0-0-'"`-0-0-'"`-0-0-'"`-0-0-'"`-0-0-'./o--000'"`-0-0-'"`-0-0-'./o--000'"`-0-0-'"`-0-0-'"`-0-0-'\s
                                                    ------------------%s  (v%s)-------------------
                          """;
        String result = String.format(template, "glowxq-glowxq-oj", getVersion());
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
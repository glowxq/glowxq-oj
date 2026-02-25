package com.glowxq.configuration;

import com.glowxq.core.common.constant.Constant;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author glowxq
 * @since 2022/8/26 15:14
 */
@Slf4j
@Configuration
@MapperScan(basePackages = {Constant.BASE_MAPPER_PACKAGE})
public class MapperConfiguration {
}

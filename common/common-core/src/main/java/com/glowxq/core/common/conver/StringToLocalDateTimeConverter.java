package com.glowxq.core.common.conver;

import com.glowxq.core.common.exception.common.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 自定义 String 到 LocalDate 的转换器
 * 支持多种日期格式的转换
 *
 * @author glowxq
 * @version 1.0
 * @date 2025/6/20
 */
@Slf4j
public class StringToLocalDateTimeConverter implements Converter<String, LocalDateTime> {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public LocalDateTime convert(String source) {
        if (!StringUtils.hasText(source)) {
            return null;
        }

        source = source.trim();

        try {
            return LocalDateTime.parse(source, DATE_TIME_FORMATTER);
        } catch (Exception e) {
            log.warn("LocalDateTime 无法解析日期字符串: {}, 错误: {}", source, e.getMessage(), e);
            throw new BusinessException("LocalDateTime 转换失败 date format: " + source);
        }
    }
}
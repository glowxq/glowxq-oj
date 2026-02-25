package com.glowxq.core.common.conver;

import com.glowxq.core.common.exception.common.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
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
public class StringToLocalDateConverter implements Converter<String, LocalDate> {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public LocalDate convert(String source) {
        if (!StringUtils.hasText(source)) {
            return null;
        }

        source = source.trim();

        try {
            // 优先尝试解析带时间的格式 "yyyy-MM-dd HH:mm:ss"
            if (source.length() > 10 && source.contains(" ")) {
                return LocalDate.parse(source, DATE_TIME_FORMATTER);
            }
            // 其次尝试解析纯日期格式 "yyyy-MM-dd"
            else {
                return LocalDate.parse(source, DATE_FORMATTER);
            }
        } catch (Exception e) {
            log.warn("LocalDate 无法解析日期字符串: {}, 错误: {}", source, e.getMessage(), e);
            throw new BusinessException("LocalDate转换失败 date format: " + source);
        }
    }
}
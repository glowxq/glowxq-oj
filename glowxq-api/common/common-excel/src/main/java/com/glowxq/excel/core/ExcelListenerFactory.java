package com.glowxq.excel.core;

import org.springframework.stereotype.Component;

/**
 * @author glowxq
 * @since 2024/12/27 16:12
 */
@Component
public class ExcelListenerFactory {

    public <T> DefaultExcelListener<T> createListener(boolean validateHeader, Class<T> clazz) {
        return new DefaultExcelListener<>(validateHeader, clazz);
    }
}

package com.glowxq.core.common.filter.trace;

import cn.hutool.core.util.IdUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;

/**
 * 追踪日志上下文工具类
 * 用于在应用中获取和操作当前SpanId
 *
 * @author glowxq
 * @version 1.0
 * @date 2025/5/26
 */
public class TraceLogContext {

    /**
     * MDC中存储SpanId的键名
     */
    public static final String TRACE_ID_KEY = "mdc_span_id";

    /**
     * MDC中存储SpanId的键名
     */
    private static final String PREFIX_KEY = "mdc_prefix";

    /**
     * 生成新的SpanId并存入MDC
     *
     * @return 生成的SpanId
     */
    public static String generateSpanId() {
        String spanId = IdUtil.simpleUUID().toUpperCase();
        MDC.put(TRACE_ID_KEY, spanId);
        return spanId;
    }

    /**
     * 获取当前上下文中的spanId，如果不存在则生成新的
     *
     * @return 当前spanId
     */
    public static String getSpanId() {
        String spanId = MDC.get(TRACE_ID_KEY);
        if (StringUtils.isBlank(spanId)) {
            spanId = generateSpanId();
        }
        return spanId;
    }

    /**
     * 设置指定的spanIdd到MDC中
     *
     * @param spanId 要设置的spanId
     */
    public static void setSpanId(String spanId) {
        if (StringUtils.isNotBlank(spanId)) {
            MDC.put(TRACE_ID_KEY, spanId);
        }
    }

    /**
     * 设置prefix
     *
     * @param prefix
     */
    public static void setPrefixKey(String prefix) {
        if (StringUtils.isNotBlank(prefix)) {
            MDC.put(PREFIX_KEY, prefix);
        }
    }

    /**
     * 清除MDC中的数据
     */
    public static void clear() {
        MDC.remove(TRACE_ID_KEY);
        // 同时清除前缀相关的键
        MDC.remove(PREFIX_KEY);
    }

    /**
     * 获取MDC中的所有上下文信息
     * 用于在跨线程或异步任务中传递完整的上下文
     */
    public static java.util.Map<String, String> getFullContext() {
        return MDC.getCopyOfContextMap();
    }

    /**
     * 设置完整的MDC上下文
     * 用于在跨线程或异步任务中恢复完整的上下文
     *
     * @param contextMap 上下文映射
     */
    public static void setFullContext(java.util.Map<String, String> contextMap) {
        if (contextMap != null) {
            MDC.setContextMap(contextMap);
        }
    }

    /**
     * 执行带有spanId的任务
     * 适用于需要在异步任务中继承父线程spanId的场景
     *
     * @param runnable 要执行的任务
     */
    public static void runWithSpanId(Runnable runnable) {
        String parentSpanId = getSpanId();
        try {
            runnable.run();
        } finally {
            clear();
            if (StringUtils.isNotBlank(parentSpanId)) {
                setSpanId(parentSpanId);
            }
        }
    }
}

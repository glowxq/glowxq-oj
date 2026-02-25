package com.glowxq.core.common.filter.trace;

import java.lang.annotation.*;

/**
 * 追踪日志注解
 * 用于在应用执行过程中自动生成UUID并在上下文中存储，便于全链路追踪
 *
 * @author glowxq
 * @version 1.0
 * @date 2025/5/26
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface TraceLog {

    /**
     * 日志前缀，用于在日志输出时添加识别标识
     */
    String logPrefix() default "";

    /**
     * 日志前缀，用于在日志输出时添加识别标识
     */
    String logPrefixSub() default "";

    /**
     * 是否在请求结束时自动清除UUID
     */
    boolean autoCleanup() default false;

    /**
     * 是否记录方法执行时间
     */
    boolean logExecutionTime() default false;

    /**
     * 是否记录方法参数
     * 注意：启用此功能可能会导致敏感信息泄露
     */
    boolean logParams() default false;

    /**
     * 是否记录方法返回值
     * 注意：启用此功能可能会导致敏感信息泄露
     */
    boolean logResult() default false;

    /**
     * 是否记录异常堆栈
     */
    boolean logException() default false;

    /**
     * 异常是否发送飞书通知
     */
    boolean feishuMessage() default false;
}

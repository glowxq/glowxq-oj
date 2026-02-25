package com.glowxq.core.common.annotation;

import com.glowxq.core.common.enums.BusinessHandlerType;
import com.glowxq.core.common.enums.ModuleEnum;

import java.lang.annotation.*;

/**
 * @author weiweicode
 * @version 1.0
 * @since 2025/6/9 23:56
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@Documented
public @interface OperationLog {

    /**
     * 日志内容
     *
     * @return
     */
    String desc() default "";

    /**
     * 模块
     *
     * @return
     */
    ModuleEnum module();

    /**
     * 业务处理类型
     *
     * @return
     */
    BusinessHandlerType handleType() default BusinessHandlerType.NONE;

    /**
     * 排除指定的请求参数
     */
    String[] excludeParamNames() default {};

    /**
     * 是否保存请求参数
     */
    boolean isSaveResponse() default true;

    /**
     * 是否保存请求参数
     *
     * @return
     */
    boolean isSaveRequest() default true;
}

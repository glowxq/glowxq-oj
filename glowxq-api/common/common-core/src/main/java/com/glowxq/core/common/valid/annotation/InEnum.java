package com.glowxq.core.common.valid.annotation;

/**
 * @author glowxq
 * @version 1.0
 * @date 2025/3/18
 */

import com.glowxq.core.common.enums.base.BaseType;
import com.glowxq.core.common.valid.validator.InEnumValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = InEnumValidator.class) // 指定校验器
public @interface InEnum {

    /**
     * 目标枚举类（必须实现BaseType接口）
     */
    Class<? extends BaseType> enumClass();

    /**
     * 校验失败时的提示信息
     */
    String message() default "必须属于{enumClass}中的有效Code";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
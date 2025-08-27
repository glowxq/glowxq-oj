package com.glowxq.core.common.valid.validator;

import com.glowxq.core.common.enums.base.BaseType;
import com.glowxq.core.common.valid.annotation.InEnum;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class InEnumValidator implements ConstraintValidator<InEnum, Object> {

    private Set<Object> validCodes;

    private Class<? extends BaseType> enumClass;

    @Override
    public void initialize(InEnum constraintAnnotation) {
        this.enumClass = constraintAnnotation.enumClass();

        // 校验枚举类是否实现BaseType接口
        if (!BaseType.class.isAssignableFrom(enumClass)) {
            throw new IllegalArgumentException("@InEnum的枚举类必须实现BaseType接口");
        }

        // 提取所有枚举实例的code
        this.validCodes = Arrays.stream(enumClass.getEnumConstants())
                                .map(BaseType::getCode)
                                .filter(Objects::nonNull) // 过滤null值
                                .collect(Collectors.toSet());
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        // 允许字段为空（若必须非空需配合@NotNull）
        if (value == null) {
            return true;
        }

        // 检查值是否存在于枚举Code集合中
        return validCodes.contains(value);
    }
}
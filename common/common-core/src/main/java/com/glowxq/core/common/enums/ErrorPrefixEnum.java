package com.glowxq.core.common.enums;

import com.glowxq.core.common.enums.base.BaseType;
import lombok.Getter;

/**
 * ErrorPrefixEnum - 定义错误前缀的枚举类。
 * <p>
 * 此枚举类用于集中管理和定义应用中的错误前缀，以便统一错误代码格式。
 * </p>
 *
 * @author glowxq
 * @version 1.0
 * @since 2024-10-11
 */
@Getter
public enum ErrorPrefixEnum implements BaseType {

    // @formatter:off
    COMMON("C", "common异常"),
    ADMIN("A", "admin异常"),

    // ...其他业务模块的异常前缀
    ;
    // @formatter:on

    /**
     * 前缀标识
     */
    private final String prefix;

    /**
     * 描述
     */
    private final String description;

    ErrorPrefixEnum(String prefix, String description) {
        this.prefix = prefix;
        this.description = description;
    }

    @Override
    public Object getCode() {
        return prefix;
    }

    @Override
    public String getName() {
        return description;
    }
}

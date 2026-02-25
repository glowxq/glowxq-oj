package com.glowxq.core.common.enums;

import com.glowxq.core.common.enums.base.BaseType;
import com.glowxq.core.common.exception.common.AlertsException;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author glowxq
 * @version 1.0
 * @date 2024/6/7
 */
@Getter
@AllArgsConstructor
public enum EnvType implements BaseType {

    /**
     * 本地
     */
    Local("local", "本地环境"),

    /**
     * 开发
     */
    Dev("dev", "开发环境"),

    /**
     * 测试
     */
    Test("test", "测试环境"),

    /**
     * 生产
     */
    Prod("prod", "生产环境"),
    ;

    private final String code;

    private final String name;

    /**
     * 匹配代码
     *
     * @param code
     * @return {@link EnvType}
     */
    public static EnvType matchCode(String code) {
        for (EnvType type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        throw new AlertsException(String.format("%s 未匹配到对应 EnvType枚举类型", code));
    }
}

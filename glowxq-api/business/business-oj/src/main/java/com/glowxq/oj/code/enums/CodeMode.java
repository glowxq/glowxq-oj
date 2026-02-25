package com.glowxq.oj.code.enums;

import com.glowxq.core.common.enums.base.BaseType;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author glowxq
 * @version 1.0
 * @date 2025/1/20
 */
@AllArgsConstructor
@Getter
public enum CodeMode implements BaseType {

    Glowc("Glowc", "Glowc"),

    OJ("OJ", "OJ");

    /**
     * code
     */
    private final String code;

    /**
     * 最近类型
     */
    private final String name;

    /**
     * 根据code获取枚举
     *
     * @param code
     * @return
     */
    public static CodeMode matchCode(String code) {
        for (CodeMode pushStatus : CodeMode.values()) {
            if (pushStatus.getCode().equals(code)) {
                return pushStatus;
            }
        }
        return null;
    }
}

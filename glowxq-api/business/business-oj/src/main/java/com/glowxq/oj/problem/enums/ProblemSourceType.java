package com.glowxq.oj.problem.enums;

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
public enum ProblemSourceType implements BaseType {
    GlowOJ("GlowOJ", "GlowOJ"),
    Hoj("Hoj", "Hoj"),
    Hydro("Hydro", "Hydro"),
    Other("Other", "Other");

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
    public static ProblemSourceType matchCode(String code) {
        for (ProblemSourceType pushStatus : ProblemSourceType.values()) {
            if (pushStatus.getCode().equals(code)) {
                return pushStatus;
            }
        }
        return null;
    }
}

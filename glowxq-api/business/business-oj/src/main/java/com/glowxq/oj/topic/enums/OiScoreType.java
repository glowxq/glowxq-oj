package com.glowxq.oj.topic.enums;

import com.glowxq.core.common.enums.base.BaseType;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 得分类型
 *
 * @author glowxq
 * @version 1.0
 * @date 2025/1/20
 */
@AllArgsConstructor
@Getter
public enum OiScoreType implements BaseType {

    // 最近得分
    Recent("Recent", "最近得分"),

    // 最高得分
    Highest("Highest", "最高得分");

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
    public static OiScoreType matchCode(String code) {
        for (OiScoreType pushStatus : OiScoreType.values()) {
            if (pushStatus.getCode().equals(code)) {
                return pushStatus;
            }
        }
        return null;
    }
}

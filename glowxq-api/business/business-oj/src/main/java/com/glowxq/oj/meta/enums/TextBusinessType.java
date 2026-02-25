package com.glowxq.oj.meta.enums;

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
public enum TextBusinessType implements BaseType {

    Announcement("Announcement", "公告"),
    Temp("Temp", "临时"),

    ;

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
    public static TextBusinessType matchCode(String code) {
        for (TextBusinessType pushStatus : TextBusinessType.values()) {
            if (pushStatus.getCode().equals(code)) {
                return pushStatus;
            }
        }
        return null;
    }
}

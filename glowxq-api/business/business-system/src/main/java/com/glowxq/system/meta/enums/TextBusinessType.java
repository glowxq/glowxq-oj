package com.glowxq.system.meta.enums;

import com.glowxq.core.common.enums.base.BaseType;
import com.glowxq.system.meta.base.TextBusinessEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author glowxq
 * @version 1.0
 * @date 2025/1/20
 */
@AllArgsConstructor
@Getter
public enum TextBusinessType implements BaseType, TextBusinessEnum {

    Announcement("Announcement", "公告"),
    Agreement("Agreement", "协议"),
    Information("Information", "信息"),
    Cell("Cell", "单元格"),

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
        for (TextBusinessType textBusinessType : TextBusinessType.values()) {
            if (textBusinessType.getCode().equals(code)) {
                return textBusinessType;
            }
        }
        return null;
    }
}

package com.glowxq.system.meta.enums;

import com.glowxq.core.common.enums.base.BaseType;
import com.glowxq.system.meta.base.TextEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author glowxq
 * @version 1.0
 * @date 2025/1/20
 */
@AllArgsConstructor
@Getter
public enum TextType implements BaseType, TextEnum {
    Markdown("Markdown", "Markdown"),
    RichText("RichText", "富文本"),
    NormalText("NormalText", "普通文本"),
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
    public static TextType matchCode(String code) {
        for (TextType textType : TextType.values()) {
            if (textType.getCode().equals(code)) {
                return textType;
            }
        }
        return null;
    }
}

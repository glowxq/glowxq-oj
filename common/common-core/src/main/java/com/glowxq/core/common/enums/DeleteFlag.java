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
public enum DeleteFlag implements BaseType {

    T("T", "已经删除"),

    F("F", "未删除"),
    ;

    private final String code;

    private final String name;

    /**
     * 匹配代码
     *
     * @param code
     * @return {@link DeleteFlag}
     */
    public static DeleteFlag matchCode(String code) {
        for (DeleteFlag type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        throw new AlertsException(String.format("%s 未匹配到对应 EnvType枚举类型", code));
    }
}

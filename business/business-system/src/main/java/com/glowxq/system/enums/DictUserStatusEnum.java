package com.glowxq.system.enums;

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
public enum DictUserStatusEnum implements BaseType {

    Normal("1000001", "正常"),
    Disabled("1000002", "禁用"),
    Muted("1000003", "禁言"),
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
    public static DictUserStatusEnum matchCode(String code) {
        for (DictUserStatusEnum dictUserTypeEnum : DictUserStatusEnum.values()) {
            if (dictUserTypeEnum.getCode().equals(code)) {
                return dictUserTypeEnum;
            }
        }
        return null;
    }
}

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
public enum DictUserTypeEnum implements BaseType {

    Test("1001001", "测试用户"),
    Admin("1001002", "超级管理员"),
    Normal("1001003", "普通用户"),
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
    public static DictUserTypeEnum matchCode(String code) {
        for (DictUserTypeEnum dictUserTypeEnum : DictUserTypeEnum.values()) {
            if (dictUserTypeEnum.getCode().equals(code)) {
                return dictUserTypeEnum;
            }
        }
        return null;
    }
}

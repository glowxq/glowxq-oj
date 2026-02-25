package com.glowxq.system.meta.enums;

import com.glowxq.core.common.enums.base.BaseType;
import com.glowxq.system.meta.base.TagBindEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author glowxq
 * @version 1.0
 * @date 2025/1/20
 */
@AllArgsConstructor
@Getter
public enum SystemTagBind implements BaseType, TagBindEnum {

    User("User", "用户"),
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
    public static SystemTagBind matchCode(String code) {
        for (SystemTagBind tagBind : SystemTagBind.values()) {
            if (tagBind.getCode().equals(code)) {
                return tagBind;
            }
        }
        return null;
    }
}

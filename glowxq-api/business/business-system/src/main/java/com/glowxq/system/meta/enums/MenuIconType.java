package com.glowxq.system.meta.enums;

import com.glowxq.core.common.enums.base.BaseType;
import com.glowxq.system.meta.base.MenuIconEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author glowxq
 * @version 1.0
 * @date 2025/1/20
 */
@AllArgsConstructor
@Getter
public enum MenuIconType implements BaseType, MenuIconEnum {

    Icon("Icon", "Icon"),
    Image("Image", "图片"),

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
    public static MenuIconType matchCode(String code) {
        for (MenuIconType pushStatus : MenuIconType.values()) {
            if (pushStatus.getCode().equals(code)) {
                return pushStatus;
            }
        }
        return null;
    }
}

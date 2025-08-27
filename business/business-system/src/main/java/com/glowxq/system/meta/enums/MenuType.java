package com.glowxq.system.meta.enums;

import com.glowxq.core.common.enums.base.BaseType;
import com.glowxq.system.meta.base.MenuEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author glowxq
 * @version 1.0
 * @date 2025/1/20
 */
@AllArgsConstructor
@Getter
public enum MenuType implements BaseType, MenuEnum {

    BottomMenu("BottomMenu", "底部菜单"),

    TopMenu("TopMenu", "顶部菜单"),

    SideMenu("SideMenu", "侧边菜单"),

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
    public static MenuType matchCode(String code) {
        for (MenuType menuType : MenuType.values()) {
            if (menuType.getCode().equals(code)) {
                return menuType;
            }
        }
        return null;
    }
}

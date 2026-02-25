package com.glowxq.system.admin.enums;

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
public enum MenuMode implements BaseType {
    Client("Client", "客户端"),
    Admin("Admin", "管理后台"),
    Common("Common", "通用"),
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
    public static MenuMode matchCode(String code) {
        for (MenuMode pushStatus : MenuMode.values()) {
            if (pushStatus.getCode().equals(code)) {
                return pushStatus;
            }
        }
        return null;
    }
}

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
public enum MenuType implements BaseType {

    Client("Client", "客户端"),
    Admin("Admin", "管理端"),
    Common("Common", "通用");

    /**
     * CODE
     */
    private final String code;

    /**
     * 名字
     */
    private final String name;

    /**
     * 匹配代码
     *
     * @param code
     * @return {@link MenuType}
     */
    public static MenuType matchCode(String code) {
        for (MenuType type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        throw new AlertsException(String.format("%s 未匹配到对应 DataScopeType 枚举类型", code));
    }
}

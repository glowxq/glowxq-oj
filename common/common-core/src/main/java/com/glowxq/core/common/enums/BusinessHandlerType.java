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
public enum BusinessHandlerType implements BaseType {

    /**
     * 创建
     */
    Create("Create", "创建"),
    /**
     * 更新
     */
    Update("Update", "更新"),
    /**
     * 删除
     */
    Delete("Delete", "删除"),

    /**
     * 查询
     */
    Query("Query", "查询"),

    NONE("none", "未设置");

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
     * @return {@link BusinessHandlerType}
     */
    public static BusinessHandlerType matchCode(String code) {
        for (BusinessHandlerType type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        throw new AlertsException(String.format("%s 未匹配到对应 AppType 枚举类型", code));
    }
}

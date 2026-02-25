package com.glowxq.core.common.enums;

import com.glowxq.core.common.enums.base.BaseType;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author glowxq
 * @version 1.0
 * @date 2024/6/7
 */
@Getter
@AllArgsConstructor
public enum DataScopeType implements BaseType {

    All("All", "所有", ""),
    DeptAndChildren("DeptAndChildren", "本部门及下级部门", "dept_id"),
    Dept("Dept", "本部门", "dept_id"),
    UserCreate("UserCreate", "本人创建", "create_id"),
    JoinGroup("JoinGroup", "加入的班级", "group_id");

    /**
     * CODE
     */
    private final String code;

    /**
     * 名字
     */
    private final String name;

    /**
     * 字段
     */
    private final String field;

    public String getFieldUp() {
        return field.toUpperCase();
    }

    /**
     * 匹配代码
     *
     * @param code
     * @return {@link DataScopeType}
     */
    public static DataScopeType matchCode(String code) {
        for (DataScopeType type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return All;
    }
}

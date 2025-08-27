package com.glowxq.core.common.enums;

import com.glowxq.core.common.enums.base.BaseType;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author weiweicode
 * @version 1.0
 * @since 2025/6/11 0:24
 */
@Getter
@AllArgsConstructor
public enum ModuleEnum implements BaseType {
    System("System", "系统管理"),
    Tenant("Tenant", "租户管理"),
    Meta("Meta", "基础配置"),

    // OJ模块,
    Topic("Topic", "Oj数据"),
    GlowC("GlowC", "GlowC"),
    Judge("Judge", "测评"),

    Problem("Problem", "题目"),
    Course("Course", "课程"),
    Code("Code", "代码"),
    ;

    /**
     * CODE
     */
    private final String code;

    /**
     * 模块名称
     */
    private final String name;

    /**
     * 匹配模块枚举中文 ，若未匹配上则返回当前入参的code
     *
     * @param code 枚举code
     * @return 模块名称
     */
    public static ModuleEnum matchCode(String code) {
        for (ModuleEnum moduleEnum : ModuleEnum.values()) {
            if (moduleEnum.getCode().equals(code)) {
                return moduleEnum;
            }
        }
        return null;
    }
}

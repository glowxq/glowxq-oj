package com.glowxq.generator.core;

import com.glowxq.core.common.enums.base.BaseType;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author glowxq
 * @version 1.0
 * @date 2025/3/23
 */
@Getter
@AllArgsConstructor
public enum GenerateType implements BaseType {

    // 代码生成
    all("all", "全部"),
    db("db", "数据库"),
    server("server", "后端"),
    service("service", "接口"),
    vue("vue", "前端");

    private final String code;

    private final String name;

    public static GenerateType matchCode(String code) {
        for (GenerateType type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }
}

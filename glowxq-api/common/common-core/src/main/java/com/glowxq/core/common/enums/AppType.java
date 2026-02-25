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
public enum AppType implements BaseType {

    system("system", "基础系统"),

    oj("oj", "在线评测"),

    wingman("wingman", "陪跑"),

    rescue("rescue", "救援"),

    ;

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
     * @return {@link AppType}
     */
    public static AppType matchCode(String code) {
        for (AppType type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        throw new AlertsException(String.format("%s 未匹配到对应 AppType 枚举类型", code));
    }

    /**
     * 获取Redis prefix
     *
     * @return {@link String }
     */
    public String getRedisKeyPrefix() {
        return this.getCode() + ":";
    }

    /**
     * 是业务应用程序
     *
     * @return {@link Boolean }
     */
    public Boolean isBusinessApp() {
        return !isSystemApp();
    }

    /**
     * 是admin app
     *
     * @return {@link Boolean}
     */
    public Boolean isSystemApp() {
        return AppType.system.equals(this);
    }
}

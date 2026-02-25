package com.glowxq.system.applet.enums;

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
public enum WechatEnvironment implements BaseType {
    /**
     * 正式版
     */
    release("release", "正式版"),

    /**
     * 体验版
     */
    trial("trial", "体验版"),

    /**
     * 开发版
     */
    develop("develop", "开发版"),
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
    public static WechatEnvironment matchCode(String code) {
        for (WechatEnvironment pushStatus : WechatEnvironment.values()) {
            if (pushStatus.getCode().equals(code)) {
                return pushStatus;
            }
        }
        return null;
    }
}

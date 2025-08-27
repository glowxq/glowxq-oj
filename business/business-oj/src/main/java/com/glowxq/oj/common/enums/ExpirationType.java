package com.glowxq.oj.common.enums;

import com.glowxq.core.common.enums.base.BaseType;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 作业过期策略
 * @author glowxq
 * @version 1.0
 * @date 2025/1/20
 */
@AllArgsConstructor
@Getter
public enum ExpirationType implements BaseType {
    // 禁止提交
    AllowedTime("AllowedTime", "必须在规定时间内才能打开"),
    // 禁止提交
    BanCommit("BanCommit", "禁止提交-可以进入作业详情页面，禁止进入做题页面"),
    // 禁止查看
    BanView("BanView", "禁止查看作业详情"),
    // 扣分10%
    Deduct10("Deduct10", "扣分10%"),
    // 无策略
    None("None", "无策略");

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
    public static ExpirationType matchCode(String code) {
        for (ExpirationType expirationType : ExpirationType.values()) {
            if (expirationType.getCode().equals(code)) {
                return expirationType;
            }
        }
        return null;
    }
}

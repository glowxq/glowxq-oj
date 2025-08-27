package com.glowxq.oj.common.enums;

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
public enum RedisKeyEnum implements BaseType {

    NormalJudgeId("oj:judge:normal", "普通评测id"),

    TestJudgeId("oj:judge:test", "测试评测id"),

    LastSignDay("oj:user:lastSignDay", "最后签到时间");

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
    public static RedisKeyEnum matchCode(String code) {
        for (RedisKeyEnum pushStatus : RedisKeyEnum.values()) {
            if (pushStatus.getCode().equals(code)) {
                return pushStatus;
            }
        }
        return null;
    }

    public String buildKey(Object id) {
        return this.code + ":" + id;
    }
}

package com.glowxq.oj.topic.enums;

import com.glowxq.core.common.enums.base.BaseType;
import com.glowxq.core.common.exception.common.AlertsException;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author glowxq
 * @version 1.0
 * @date 2025/3/18
 */
@Getter
@AllArgsConstructor
public enum TopicJudgeType implements BaseType {
    ACM(0, "acm"),

    OI(1, "oi");

    private final Integer type;

    private final String name;

    /**
     * 根据code获取枚举
     *
     * @return 对应的枚举实例，未找到返回null
     */
    public static TopicJudgeType matchCode(Integer code) {
        for (TopicJudgeType type : TopicJudgeType.values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        throw new AlertsException("编程题类型转换错误:" + code);
    }

    @Override
    public Object getCode() {
        return type;
    }
}

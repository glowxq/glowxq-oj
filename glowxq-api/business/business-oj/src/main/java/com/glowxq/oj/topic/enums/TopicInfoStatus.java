package com.glowxq.oj.topic.enums;

import com.glowxq.core.common.enums.base.BaseType;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 作业/比赛过期策略枚举
 * 描述时间范围外的提交和访问规则
 *
 * @author glowxq
 * @version 1.0
 * @date 2025/4/11
 */
@Getter
@AllArgsConstructor
public enum TopicInfoStatus implements BaseType {

    NotStarted("NotStarted", "未开始"),

    InProgress("InProgress", "进行中"),

    Completed("Completed", "已完成"),
    ;

    private final String code;

    private final String name;

    public static TopicInfoStatus matchCode(String code) {
        for (TopicInfoStatus timeRangeType : TopicInfoStatus.values()) {
            if (timeRangeType.getCode().equals(code)) {
                return timeRangeType;
            }
        }
        return null;
    }
}

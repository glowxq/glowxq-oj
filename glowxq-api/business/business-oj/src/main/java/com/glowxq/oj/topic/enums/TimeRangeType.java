package com.glowxq.oj.topic.enums;

import com.glowxq.core.common.enums.base.BaseType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

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
public enum TimeRangeType implements BaseType {

    /**
     * 严格查看时间策略
     */
    StrictTimeView("StrictTimeView", "严格时间-限制查看", "必须在规定时间内才能打开主题详情"),

    /**
     * 严格提交时间策略
     */
    StrictTimeSubmit("StrictTimeSubmit", "严格时间-限制做题", "非规定时间内能打开主题详情，但是不能做题提交"),

    /**
     * 固定比例扣分
     */
    PENALTY_FIXED("PenaltyFixed", "超时固定扣分", "超时扣x%的分数（需配合扣分率参数使用）"),

    /**
     * 线性递减扣分
     */
    PENALTY_GRADUAL("PenaltyGradual", "超时递减扣分", "超时后按小时线性扣分，每小时扣x% （需配合扣分率参数使用）"),

    /**
     * 无限制策略
     */
    UNRESTRICTED("Unrestricted", "无限制策略", "永久开放提交和查看");

    private final String code;

    private final String name;

    private final String desc;

    public static TimeRangeType matchCode(String code) {
        for (TimeRangeType timeRangeType : TimeRangeType.values()) {
            if (timeRangeType.getCode().equals(code)) {
                return timeRangeType;
            }
        }
        return null;
    }

    public Boolean isStrictTime(){
        return List.of(StrictTimeView, StrictTimeSubmit).contains(this);
    }
}

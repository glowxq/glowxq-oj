package com.glowxq.oj.problem.enums;

import com.glowxq.core.common.enums.base.BaseType;
import com.glowxq.core.common.exception.common.AlertsException;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author glowxq
 * @version 1.0
 * @date 2025/1/17
 */
@Getter
@AllArgsConstructor
public enum ProblemDifficultyType implements BaseType {

    // 题目难度,0简单，1中等，2困难
    ENTRY(0, "入门"),
    EASY(1, "简单"),
    NORMAL(2, "中等"),
    HARD(3, "困难"),
    EXPERT(4, "专家");

    private final Integer code;

    private final String name;

    public static ProblemDifficultyType matchCode(int code) {
        for (ProblemDifficultyType userStatus : ProblemDifficultyType.values()) {
            if (userStatus.getCode() == code) {
                return userStatus;
            }
        }
        return null;
    }

    public static ProblemDifficultyType matchName(String name) {
        for (ProblemDifficultyType userStatus : ProblemDifficultyType.values()) {
            if (userStatus.getName().equals(name)) {
                return userStatus;
            }
        }
        throw new AlertsException("不支持该题目难度类型:" + name);
    }
}

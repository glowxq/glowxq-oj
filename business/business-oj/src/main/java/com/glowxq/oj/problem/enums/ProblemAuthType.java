package com.glowxq.oj.problem.enums;

import com.glowxq.core.common.enums.base.BaseType;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author glowxq
 * @version 1.0
 * @date 2025/1/17
 */
@Getter
@AllArgsConstructor
public enum ProblemAuthType implements BaseType {

    // 默认为1公开，2为私有，3为比赛题目
    PUBLIC(1, "公开"),
    PRIVATE(2, "私有"),
    CONTEST(3, "比赛题目");

    private final Integer code;

    private final String name;

    public static ProblemAuthType matchCode(int code) {
        for (ProblemAuthType userStatus : ProblemAuthType.values()) {
            if (userStatus.getCode() == code) {
                return userStatus;
            }
        }
        return null;
    }
}

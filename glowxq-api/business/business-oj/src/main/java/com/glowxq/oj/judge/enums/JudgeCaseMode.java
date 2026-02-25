package com.glowxq.oj.judge.enums;

import com.glowxq.core.common.enums.base.BaseType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum JudgeCaseMode implements BaseType {
    DEFAULT("default", "默认模式"),
    SUBTASK_LOWEST("subtask_lowest", "子任务最低分模式"),
    SUBTASK_AVERAGE("subtask_average", "子任务平均分模式"),
    ERGODIC_WITHOUT_ERROR("ergodic_without_error", "遍历不包含错误");

    private final String mode;

    private final String name;

    public static JudgeCaseMode getJudgeMode(String mode) {
        for (JudgeCaseMode judgeMode : JudgeCaseMode.values()) {
            if (judgeMode.getMode().equals(mode)) {
                return judgeMode;
            }
        }
        return null;
    }

    @Override
    public Object getCode() {
        return mode;
    }
}

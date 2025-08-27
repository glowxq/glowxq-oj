package com.glowxq.oj.judge.enums;

import com.glowxq.core.common.enums.base.BaseType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum JudgeMode implements BaseType {
    TEST("test", "测试模式"),
    DEFAULT("default", "默认模式"),
    SPJ("spj", "SPJ模式"),
    INTERACTIVE("interactive", "交互式模式");

    private final String mode;

    private final String name;



    public static JudgeMode getJudgeMode(String mode) {
        for (JudgeMode judgeMode : JudgeMode.values()) {
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

package com.glowxq.oj.judge.enums;

import com.glowxq.core.common.enums.base.BaseType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum JudgeDir implements BaseType {
    RUN_WORKPLACE_DIR("/judge/run", "运行工作目录","RunWorkplaceDir"),

    TEST_CASE_DIR("/judge/testcase", "测试用例目录","TestCaseDir"),

    SPJ_WORKPLACE_DIR("/judge/spj", "SPJ工作目录","SpjWorkplaceDir"),

    INTERACTIVE_WORKPLACE_DIR("/judge/interactive", "交互式工作目录","InteractiveWorkplaceDir"),

    TMPFS_DIR("/w", "临时目录","TmpfsDir");

    private final String content;

    private final String name;

    private final String code;

    public static JudgeDir matchCode(String code) {
        for (JudgeDir judgeDir : JudgeDir.values()) {
            if (judgeDir.getCode().equals(code)) {
                return judgeDir;
            }
        }
        return null;
    }
}

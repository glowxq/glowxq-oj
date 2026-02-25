package com.glowxq.oj.problem.enums;

import com.glowxq.core.common.enums.base.BaseType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ProblemApplyPublicProgress implements BaseType {

    NOT_APPLIED(0, "未申请"),
    APPLYING(1, "申请中"),
    APPROVED(2, "申请通过"),
    REJECTED(3, "申请拒绝");

    private final Integer code;

    private final String name;

    /**
     * 根据 code 匹配对应的枚举值
     *
     * @param code 要匹配的 code
     * @return 匹配到的枚举值，未匹配到返回 null
     */
    public static ProblemApplyPublicProgress matchCode(Integer code) {
        for (ProblemApplyPublicProgress progress : ProblemApplyPublicProgress.values()) {
            if (progress.getCode() == null ? code == null : progress.getCode().equals(code)) {
                return progress;
            }
        }
        return null;
    }
}
package com.glowxq.oj.problem.enums;

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
public enum ProblemCaseType implements BaseType {
    ManualEditing("ManualEditing", "手动编辑"),
    FileUpload("FileUpload", "压缩包上传"),
    ;

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
    public static ProblemCaseType matchCode(String code) {
        for (ProblemCaseType pushStatus : ProblemCaseType.values()) {
            if (pushStatus.getCode().equals(code)) {
                return pushStatus;
            }
        }
        return null;
    }
}

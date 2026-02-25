package com.glowxq.oj.judge.enums;

import com.glowxq.core.common.enums.base.BaseType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.stream.Stream;

@Getter
@AllArgsConstructor
public enum JudgeStatus implements BaseType {
    // 提交失败
    STATUS_NOT_SUBMITTED(-10, "Not Submitted"),
    STATUS_CANCELLED(-4, "Cancelled"),
    STATUS_PRESENTATION_ERROR(-3, "Presentation Error"),
    STATUS_COMPILE_ERROR(-2, "Compile Error"),
    STATUS_WRONG_ANSWER(-1, "Wrong Answer"),

    STATUS_ACCEPTED(0, "Accepted"),
    STATUS_TIME_LIMIT_EXCEEDED(1, "Time Limit Exceeded"),
    STATUS_MEMORY_LIMIT_EXCEEDED(2, "Memory Limit Exceeded"),
    STATUS_RUNTIME_ERROR(3, "Runtime Error"),
    STATUS_SYSTEM_ERROR(4, "System Error"),
    STATUS_PENDING(5, "Pending"),
    STATUS_COMPILING(6, "Compiling"),
    // 正在等待结果
    STATUS_JUDGING(7, "Judging"),
    STATUS_PARTIAL_ACCEPTED(8, "Partial Accepted"),
    STATUS_SUBMITTING(9, "Submitting"),
    STATUS_SUBMITTED_FAILED(10, "Submitted Failed"),
    STATUS_NULL(15, "No Status");

    private final Integer status;

    private final String name;

    /**
     * 匹配代码
     *
     * @param code
     * @return {@link JudgeStatus}
     */
    public static JudgeStatus matchCode(Integer code) {
        for (JudgeStatus type : values()) {
            if (type.getStatus().equals(code)) {
                return type;
            }
        }
        return null;
    }

    @Override
    public Integer getCode() {
        return status;
    }

    public static List<Integer> listPunishmentTimeStatus(){
       return Stream.of(JudgeStatus.STATUS_COMPILE_ERROR,
                JudgeStatus.STATUS_WRONG_ANSWER,
                JudgeStatus.STATUS_TIME_LIMIT_EXCEEDED,
                JudgeStatus.STATUS_MEMORY_LIMIT_EXCEEDED,
                JudgeStatus.STATUS_RUNTIME_ERROR,
                JudgeStatus.STATUS_SYSTEM_ERROR).map(JudgeStatus::getCode).toList();
    }
}

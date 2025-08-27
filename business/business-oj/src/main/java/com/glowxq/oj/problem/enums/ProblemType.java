package com.glowxq.oj.problem.enums;

import com.glowxq.core.common.enums.base.BaseType;
import com.glowxq.core.common.exception.common.AlertsException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;

/**
 * @author glowxq
 * @version 1.0
 * @date 2025/1/20
 */
@AllArgsConstructor
@Getter
public enum ProblemType implements BaseType {

    /**
     * 未知题型
     */
    unknown("unknown", "未知题型", 0,"U"),

    /**
     * 编程题
     */
    Programmer("Programmer", "编程题", 0,"P"),

    /**
     * 填空题
     */
    FillBlank("FillBlank", "填空题", 4,"F"),

    /**
     * 单选题
     */
    SingleChoice("SingleChoice", "单选题", 4,"S"),

    /**
     * 多选题
     */
    MultipleChoice("MultipleChoice", "多选题", 6,"M"),

    /**
     * 判断题
     */
    TrueFalse("TrueFalse", "判断题", 2,"T"),

    /**
     * 简答题
     */
    ShortAnswer("ShortAnswer", "简答题", 1,"S");

    /**
     * code
     */
    private final String code;

    /**
     * 题目类型名
     */
    private final String name;

    /**
     * 选项最大个数（0表示无选项）
     */
    private final Integer maxOptions;

    /**
     * 序号前缀
     */
    private final String serialPrefix;

    /**
     * 根据code获取枚举
     *
     * @param code 枚举编码
     * @return 对应的枚举实例，未找到返回null
     */
    public static ProblemType matchCode(String code) {
        for (ProblemType type : ProblemType.values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return unknown;
    }

    /**
     * 根据code获取枚举
     *
     * @param name 题目名
     * @return 对应的枚举实例，未找到返回null
     */
    public static ProblemType matchName(String name) {
        for (ProblemType type : ProblemType.values()) {
            if (type.getName().equals(name)) {
                return type;
            }
        }
        throw new AlertsException("题目类型转换错误:" + name);
    }

    public Boolean isFixed() {
        return Arrays.asList(FillBlank, SingleChoice, MultipleChoice, TrueFalse, ShortAnswer).contains(this);
    }

    public Boolean isProgram() {
        return Objects.equals(Programmer, this);
    }
}
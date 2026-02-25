package com.glowxq.oj.judge.enums;

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
public enum JudgeCompileType implements BaseType {

    /**
     * 编译特判程序
     */
    COMPILE_SPJ("CompileSpj", "编译特判程序"),

    /**
     * 编译交互程序
     */
    COMPILE_INTERACTIVE("CompileInteractive", "编译交互程序");

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
    public static JudgeCompileType matchCode(String code) {
        for (JudgeCompileType sceneType : JudgeCompileType.values()) {
            if (sceneType.getCode().equals(code)) {
                return sceneType;
            }
        }
        return null;
    }
}

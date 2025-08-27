package com.glowxq.oj.judge.enums;

import com.glowxq.core.common.enums.base.BaseType;
import com.glowxq.oj.submit.compile.impl.CompileInteractiveSubmitImpl;
import com.glowxq.oj.submit.compile.impl.CompileSpjSubmitImpl;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author glowxq
 * @version 1.0
 * @date 2025/1/20
 */
@AllArgsConstructor
@Getter
public enum SubmitType implements BaseType {

    /**
     * 普通评测
     */
    NormalJudge("NormalJudge", "普通评测", null),

    /**
     * 客观题评测
     */
    FixedJudge("FixedJudge", "普通评测", null),

    /**
     * 远程评测
     */
    RemoteJudge("RemoteJudge", "远程评测", null),

    /**
     * 在线调试
     */
    TestJudge("TestJudge", "在线调试", null),

    /**
     * 编译特判程序
     */
    CompileSpj("CompileSpj", "编译特判程序", CompileSpjSubmitImpl.class),

    /**
     * 编译交互程序
     */
    CompileInteractive("CompileInteractive", "编译交互程序", CompileInteractiveSubmitImpl.class);

    /**
     * code
     */
    private final String code;

    /**
     * 最近类型
     */
    private final String name;

    private final Class<?> clazz;

    /**
     * 根据code获取枚举
     *
     * @param code
     * @return
     */
    public static SubmitType matchCode(String code) {
        for (SubmitType sceneType : SubmitType.values()) {
            if (sceneType.getCode().equals(code)) {
                return sceneType;
            }
        }
        return null;
    }
}

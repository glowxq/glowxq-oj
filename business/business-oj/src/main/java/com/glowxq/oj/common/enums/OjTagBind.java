package com.glowxq.oj.common.enums;

import com.glowxq.core.common.enums.base.BaseType;
import com.glowxq.system.meta.base.TagBindEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author glowxq
 * @version 1.0
 * @date 2025/1/20
 */
@Getter
@AllArgsConstructor
public enum OjTagBind implements BaseType, TagBindEnum {


    Topic("Topic", "主题"),
    Course("Course", "课程"),
    Problem("Problem", "题目"),

    ;

    /**
     * code
     */
    private final String code;

    /**
     * 标签绑定业务名
     */
    private final String name;

    /**
     * 根据code获取枚举
     *
     * @param code
     * @return
     */
    public static OjTagBind matchCode(String code) {
        for (OjTagBind tagBind : OjTagBind.values()) {
            if (tagBind.getCode().equals(code)) {
                return tagBind;
            }
        }
        return null;
    }
}

package com.glowxq.oj.topic.enums;

import com.glowxq.core.common.enums.base.BaseType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

/**
 * @author glowxq
 * @version 1.0
 * @date 2025/1/20
 */
@AllArgsConstructor
@Getter
public enum TopicType implements BaseType {

    // 其它
    Other("Other", "其它", "除比赛外的种类"),

    // 测试
    Exercise("Exercise", "练习", "默认设置 无限制的随意测试"),

    // 作业
    Homework("Homework", "作业", "默认设置 明确提交时间,过期可以补交但是扣分"),

    // 比赛
    Contest("Contest", "比赛", "默认设置 过期禁止提交"),
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
     * 解释
     */
    private final String description;

    /**
     * 根据code获取枚举
     *
     * @param code
     * @return
     */
    public static TopicType matchCode(String code) {
        for (TopicType pushStatus : TopicType.values()) {
            if (pushStatus.getCode().equals(code)) {
                return pushStatus;
            }
        }
        return null;
    }

    public List<String> queryListCode() {
        if (Other.equals(this)) {
            return Arrays.stream(values()).filter(value -> !Contest.equals(value)).map(TopicType::getCode).toList();
        }
        return List.of(this.getCode());
    }
}

package com.glowxq.oj.judge.enums;

import com.glowxq.core.common.enums.base.BaseType;
import com.glowxq.oj.topic.enums.TopicType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/**
 * @author glowxq
 * @version 1.0
 * @date 2025/1/20
 */
@AllArgsConstructor
@Getter
public enum JudgeSceneType implements BaseType {

    Normal("Normal", "普通测评"),
    Exercise(TopicType.Exercise.getCode(), TopicType.Exercise.getName()),
    Homework(TopicType.Homework.getCode(), TopicType.Homework.getName()),
    Contest(TopicType.Contest.getCode(), TopicType.Contest.getName()),
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
    public static JudgeSceneType matchCode(String code) {
        for (JudgeSceneType sceneType : JudgeSceneType.values()) {
            if (sceneType.getCode().equals(code)) {
                return sceneType;
            }
        }
        return null;
    }

    public TopicType toTopicType() {
        return TopicType.matchCode(this.getCode());
    }

    public Boolean isFixedTopic(){
        return List.of(Exercise, Homework, Contest).contains(this);
    }
}

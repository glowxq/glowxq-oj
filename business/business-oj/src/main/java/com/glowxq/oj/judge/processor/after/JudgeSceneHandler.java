package com.glowxq.oj.judge.processor.after;

import com.glowxq.oj.judge.enums.JudgeSceneType;
import com.glowxq.oj.judge.pojo.po.Judge;

/**
 * @author glowxq
 * @version 1.0
 * @date 2025/3/19
 */
public interface JudgeSceneHandler {

    /**
     * 测评后的根据测评场景处理后续业务
     *
     * @param judge          最终生成的判题记录对象
     * @param judgeSceneType 判题场景类型
     */
    void judgeAfterScene(Judge judge, JudgeSceneType judgeSceneType);
}

package com.glowxq.oj.judge.processor;

import com.glowxq.oj.judge.pojo.po.Judge;

public interface JudgeProcessor {

    /**
     * 判题
     *
     * @param judge
     */
    void judge(Judge judge);
}

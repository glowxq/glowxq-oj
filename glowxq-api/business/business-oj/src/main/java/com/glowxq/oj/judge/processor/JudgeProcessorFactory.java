package com.glowxq.oj.judge.processor;

import com.glowxq.core.common.exception.common.AlertsException;
import com.glowxq.core.util.SpringUtils;
import com.glowxq.oj.judge.enums.SubmitType;
import com.glowxq.oj.judge.pojo.po.Judge;
import org.springframework.stereotype.Component;

/**
 * @author glowxq
 * @version 1.0
 * @date 2025/4/1
 */
@Component
public class JudgeProcessorFactory {

    public JudgeProcessor getJudgeProcessor(Judge judge) {
        SubmitType submitType = judge.submitType();
        switch (submitType) {
            case FixedJudge -> {
                return SpringUtils.getBean(FixedJudgeProcessor.class);
            }
            case NormalJudge -> {
                return SpringUtils.getBean(NormalJudgeProcessor.class);
            }
            case RemoteJudge -> {
                return SpringUtils.getBean(RemoteJudgeProcessor.class);
            }
            default -> throw new AlertsException("不支持的提交类型:" + submitType);
        }
    }
}

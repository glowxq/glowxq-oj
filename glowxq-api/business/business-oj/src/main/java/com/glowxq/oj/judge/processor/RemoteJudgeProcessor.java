package com.glowxq.oj.judge.processor;

import com.glowxq.oj.judge.pojo.po.Judge;
import com.glowxq.oj.judge.processor.handler.JudgeHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * @author glowxq
 * @version 1.0
 * @date 2025/4/1
 */
@Component
@RequiredArgsConstructor
public class RemoteJudgeProcessor extends BaseJudgeProcessor {

    private final JudgeHandler judgeHandler;

    @Override
    protected Judge doJudge(Judge judge) {
        // 远程测评未实现
        return null;
    }
}

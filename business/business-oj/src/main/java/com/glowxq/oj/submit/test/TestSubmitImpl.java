package com.glowxq.oj.submit.test;

import cn.hutool.core.util.IdUtil;
import com.glowxq.core.util.JsonUtils;
import com.glowxq.core.util.StringUtils;
import com.glowxq.oj.common.enums.RedisKeyEnum;
import com.glowxq.oj.judge.processor.bo.TestJudgeReq;
import com.glowxq.oj.judge.processor.bo.TestJudgeRes;
import com.glowxq.oj.judge.processor.handler.JudgeHandlerImpl;
import com.glowxq.oj.problem.pojo.po.Problem;
import com.glowxq.oj.problem.service.ProblemService;
import com.glowxq.redis.RedisUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author glowxq
 * @version 1.0
 * @date 2025/3/19
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TestSubmitImpl implements TestSubmitInterface {

    private final JudgeHandlerImpl judgeHandler;

    private final ProblemService problemService;

    @Override
    public void submitTest(TestSubmitDTO testJudgeDto) {
        String uniqueKey = RedisKeyEnum.TestJudgeId.buildKey(IdUtil.simpleUUID());
        Problem problem = problemService.getById(testJudgeDto.getProblemId());
        TestJudgeReq testJudgeReq = new TestJudgeReq();
        testJudgeReq.setMemoryLimit(problem.getMemoryLimit())
                    .setTimeLimit(problem.getTimeLimit())
                    .setStackLimit(problem.getStackLimit())
                    .setCode(testJudgeDto.getCode())
                    .setLanguage(testJudgeDto.getLanguage())
                    .setUniqueKey(uniqueKey)
                    .setExpectedOutput(testJudgeDto.getExpectedOutput())
                    .setTestCaseInput(testJudgeDto.getUserInput())
                    .setProblemJudgeMode(problem.getJudgeMode())
                    .setIsRemoveEndBlank(problem.getRemoveEndBlank() || problem.getRemote())
                    .setIsFileIO(problem.getFileIo())
                    .setIoReadFileName(problem.getIoReadFileName())
                    .setIoWriteFileName(problem.getIoWriteFileName());
        String userExtraFile = problem.getUserExtraFile();
        if (StringUtils.isNotEmpty(userExtraFile)) {
            testJudgeReq.setExtraFile((HashMap<String, String>) JsonUtils.parseObject(userExtraFile, Map.class));
        }

        TestJudgeRes testJudgeRes = judgeHandler.testJudge(testJudgeReq);
        testJudgeRes.setInput(testJudgeReq.getTestCaseInput());
        testJudgeRes.setExpectedOutput(testJudgeReq.getExpectedOutput());
        testJudgeRes.setProblemJudgeMode(testJudgeReq.getProblemJudgeMode());
        RedisUtils.setValue(testJudgeReq.getUniqueKey(), testJudgeRes, 60L, TimeUnit.SECONDS);
    }
}

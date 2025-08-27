package com.glowxq.oj.judge.processor.handler;

import com.glowxq.oj.judge.pojo.po.Judge;
import com.glowxq.oj.judge.processor.bo.LanguageConfig;
import com.glowxq.oj.judge.processor.bo.TestJudgeReq;
import com.glowxq.oj.judge.processor.bo.TestJudgeRes;
import com.glowxq.oj.judge.processor.core.Compiler;
import com.glowxq.oj.judge.processor.exception.SystemException;
import com.glowxq.oj.judge.processor.helper.FixedJudgeHelper;
import com.glowxq.oj.judge.processor.helper.LanguageConfigLoader;
import com.glowxq.oj.judge.processor.helper.NormalJudgeHelper;
import com.glowxq.oj.judge.processor.helper.TestJudgeHelper;
import com.glowxq.oj.judge.service.JudgeService;
import com.glowxq.oj.problem.pojo.po.Problem;
import com.glowxq.oj.problem.service.ProblemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
@RequiredArgsConstructor
@Slf4j(topic = "judge")
public class JudgeHandlerImpl implements JudgeHandler {

    private final JudgeService judgeService;

    private final ProblemService problemService;

    private final TestJudgeHelper testJudgeHelper;

    private final FixedJudgeHelper fixedJudgeHelper;

    private final NormalJudgeHelper normalJudgeHelper;

    private final LanguageConfigLoader languageConfigLoader;

    @Override
    public Judge judge(Judge judge) {
        judgeService.updateCompiling(judge);

        Problem problem = problemService.getById(judge.getProblemId());
        LanguageConfig languageConfig = languageConfigLoader.getLanguageConfigByName(judge.getLanguage());

        // c和c++为一倍时间和空间，其它语言为2倍时间和空间
        if (languageConfig != null && languageConfig.isNotC()) {
            problem.setTimeLimit(problem.getTimeLimit() * 2);
            problem.setMemoryLimit(problem.getMemoryLimit() * 2);
        }
        HashMap<String, Object> judgeResult = normalJudgeHelper.judge(problem, judge);
        Judge finalJudge = normalJudgeHelper.buildFinalJudge(judge, judgeResult, problem);
        judgeService.updateById(finalJudge);
        log.info("judgeId:{} judgeKey:{} finalJudge:{}", finalJudge.getId(), finalJudge.getJudgeKey(), finalJudge);
        return finalJudge;
    }

    @Override
    public Judge judgeFixed(Judge judge) {
        Judge finalJudge = fixedJudgeHelper.judgeFixed(judge);
        judgeService.updateById(finalJudge);
        log.info("判题结束:{}", finalJudge);
        return finalJudge;
    }

    @Override
    public TestJudgeRes testJudge(TestJudgeReq testJudgeReq) {
        return testJudgeHelper.testJudge(testJudgeReq);
    }

    @Override
    public Boolean compileSpj(String code, Long problemId, String spjLanguage, HashMap<String, String> extraFiles) throws SystemException {
        return Compiler.compileSpj(code, problemId, spjLanguage, extraFiles);
    }

    @Override
    public Boolean compileInteractive(String code, Long problemId, String interactiveLanguage, HashMap<String, String> extraFiles) throws SystemException {
        return Compiler.compileInteractive(code, problemId, interactiveLanguage, extraFiles);
    }
}

package com.glowxq.oj.judge.processor.handler;

import com.glowxq.oj.judge.pojo.po.Judge;
import com.glowxq.oj.judge.processor.bo.TestJudgeReq;
import com.glowxq.oj.judge.processor.bo.TestJudgeRes;
import com.glowxq.oj.judge.processor.exception.SystemException;

import java.util.HashMap;

public interface JudgeHandler {

    /**
     * 判题
     *
     * @param judge
     * @return
     */
    Judge judge(Judge judge);

    /**
     * 客观题判题
     *
     * @param judge
     * @return
     */
    Judge judgeFixed(Judge judge);

    /**
     * 测试判题
     *
     * @param testJudgeReq
     * @return
     */
    TestJudgeRes testJudge(TestJudgeReq testJudgeReq);

    /**
     * 编译spj
     *
     * @param code
     * @param problemId
     * @param spjLanguage
     * @param extraFiles
     * @return
     */
    Boolean compileSpj(String code, Long problemId, String spjLanguage, HashMap<String, String> extraFiles) throws SystemException;

    /**
     * 编译交互式
     *
     * @param code
     * @param problemId
     * @param interactiveLanguage
     * @param extraFiles
     * @return
     */
    Boolean compileInteractive(String code, Long problemId, String interactiveLanguage, HashMap<String, String> extraFiles) throws SystemException;
}

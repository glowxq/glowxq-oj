package com.glowxq.oj.judge.processor.helper;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileReader;
import cn.hutool.core.io.file.FileWriter;
import cn.hutool.json.JSONObject;
import com.glowxq.core.common.exception.common.AlertsException;
import com.glowxq.core.common.feishu.utils.FeishuMessageUtils;
import com.glowxq.oj.judge.enums.*;
import com.glowxq.oj.judge.pojo.po.Judge;
import com.glowxq.oj.judge.pojo.po.JudgeCase;
import com.glowxq.oj.judge.processor.bo.LanguageConfig;
import com.glowxq.oj.judge.processor.bo.Pair_;
import com.glowxq.oj.judge.processor.core.Compiler;
import com.glowxq.oj.judge.processor.core.JudgeRun;
import com.glowxq.oj.judge.processor.core.SandboxRun;
import com.glowxq.oj.judge.processor.exception.CompileException;
import com.glowxq.oj.judge.processor.exception.SubmitException;
import com.glowxq.oj.judge.processor.exception.SystemException;
import com.glowxq.oj.judge.processor.utils.JudgeUtils;
import com.glowxq.oj.judge.processor.utils.MessageUtils;
import com.glowxq.oj.judge.service.JudgeCaseService;
import com.glowxq.oj.judge.service.JudgeService;
import com.glowxq.oj.problem.pojo.po.Problem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.*;

@Component
@RequiredArgsConstructor
@Slf4j(topic = "judge")
public class NormalJudgeHelper {

    private final JudgeRun judgeRun;

    private final JudgeService judgeService;

    private final JudgeCaseService judgeCaseService;

    private final LanguageConfigLoader languageConfigLoader;

    private final ProblemTestCaseHelper problemTestCaseHelper;

    public HashMap<String, Object> judge(Problem problem, Judge judge) {

        HashMap<String, Object> result = new HashMap<>();
        // 编译好的临时代码文件id
        String userFileId = null;
        try {
            // 对用户源代码进行编译 获取tmpfs中的fileId
            LanguageConfig languageConfig = languageConfigLoader.getLanguageConfigByName(judge.getLanguage());
            // 有的语言可能不支持编译, 目前有js、php不支持编译
            if (languageConfig.getCompileCommand() != null) {
                userFileId = Compiler.compile(languageConfig,
                        judge.getCode(),
                        judge.getLanguage(),
                        JudgeUtils.getProblemExtraFileMap(problem, "user"));
            }
            // 测试数据文件所在文件夹
            String testCasesDir = JudgeDir.TEST_CASE_DIR.getContent() + File.separator + "problem_" + problem.getId();
            // 从文件中加载测试数据json
            JSONObject testCasesInfo = problemTestCaseHelper.loadTestCaseInfo(problem.getId(),
                    testCasesDir,
                    problem.getCaseVersion(),
                    problem.getJudgeMode(),
                    problem.getJudgeCaseMode());

            // 检查是否为spj或者interactive，同时是否有对应编译完成的文件，若不存在，就先编译生成该文件，同时也要检查版本
            boolean isOk = checkOrCompileExtraProgram(problem);
            if (!isOk) {
                result.put("code", JudgeStatus.STATUS_SYSTEM_ERROR.getStatus());
                result.put("errMsg", "The special judge or interactive program code does not exist.");
                result.put("time", 0);
                result.put("memory", 0);
                return result;
            }
            // 更新状态为评测数据中
            judgeService.updateJudging(judge);

            // 获取题目数据的评测模式
            String infoJudgeCaseMode = testCasesInfo.getStr("judgeCaseMode", JudgeCaseMode.DEFAULT.getMode());
            String judgeCaseMode = getFinalJudgeCaseMode(problem.getProgramType(), problem.getJudgeCaseMode(), infoJudgeCaseMode);

            // 开始测试每个测试点
            List<JSONObject> allCaseResultList = judgeRun.judgeAllCase(judge.getId(),
                    problem,
                    judge.getLanguage(),
                    testCasesDir,
                    testCasesInfo,
                    userFileId,
                    judge.getCode(),
                    false,
                    judgeCaseMode);

            // 对全部测试点结果进行评判,获取最终评判结果
            return getJudgeInfo(allCaseResultList, problem, judge, judgeCaseMode);
        } catch (SubmitException submitException) {
            result.put("code", JudgeStatus.STATUS_SUBMITTED_FAILED.getStatus());
            result.put("errMsg", MessageUtils.mergeNonEmptyStrings(submitException.getMessage(), submitException.getStdout(), submitException.getStderr()));
            result.put("time", 0);
            result.put("memory", 0);
            result.put("exceptionStackTrace", ExceptionUtils.getStackTrace(submitException));
            log.error("[Judge] [Submit Error] Submit Id:[{}] Problem Id:[{}], Error:[{}]",
                    judge.getId(),
                    problem.getId(),
                    submitException.getMessage(),
                    submitException);
            FeishuMessageUtils.sendInternalMessage("[Submit Error] 提交失败,请检查代码是否正确"+ ExceptionUtils.getStackTrace(submitException));
        } catch (CompileException compileException) {
            result.put("code", JudgeStatus.STATUS_COMPILE_ERROR.getStatus());
            result.put("errMsg", MessageUtils.mergeNonEmptyStrings(compileException.getStdout(), compileException.getStderr()));
            result.put("time", 0);
            result.put("memory", 0);
            result.put("exceptionStackTrace", ExceptionUtils.getStackTrace(compileException));
            log.error("[Judge] [Compile Error] Submit Id:[{}] Problem Id:[{}], Error:[{}]",
                    judge.getId(),
                    problem.getId(),
                    compileException.getMessage(),
                    compileException);
            FeishuMessageUtils.sendInternalMessage("[Compile Error]编译失败,请检查代码是否正确"+ ExceptionUtils.getStackTrace(compileException));
        } catch (Exception e) {
            result.put("code", JudgeStatus.STATUS_SYSTEM_ERROR.getStatus());
            result.put("errMsg", "Oops, something has gone wrong with the judgeServer. Please report this to administrator.");
            result.put("time", 0);
            result.put("memory", 0);
            result.put("exceptionStackTrace", ExceptionUtils.getStackTrace(e));
            log.error("[Judge] [System Runtime Error] Submit Id:[{}] Problem Id:[{}], Error:[{}]",
                    judge.getId(),
                    problem.getId(),
                    e.getMessage(),
                    e);
            FeishuMessageUtils.sendInternalMessage("[Exception]判题失败,请检查代码是否正确"+ ExceptionUtils.getStackTrace(e));
        } finally {
            // 删除tmpfs内存中的用户代码可执行文件
            if (StringUtils.isNotEmpty(userFileId)) {
                SandboxRun.delFile(userFileId);
            }
        }
        return result;
    }


    public Judge buildFinalJudge(Judge finalJudgeRes, HashMap<String, Object> judgeResult, Problem problem) {
        // 如果是编译失败、提交错误或者系统错误就有错误提醒
        if (judgeResult.get("code") == JudgeStatus.STATUS_COMPILE_ERROR.getStatus() ||
                judgeResult.get("code") == JudgeStatus.STATUS_SYSTEM_ERROR.getStatus() ||
                judgeResult.get("code") == JudgeStatus.STATUS_RUNTIME_ERROR.getStatus() ||
                judgeResult.get("code") == JudgeStatus.STATUS_SUBMITTED_FAILED.getStatus()) {
            finalJudgeRes.setErrorMessage((String) judgeResult.getOrDefault("errMsg", ""));
        }
        finalJudgeRes.setExceptionStackTrace((String) judgeResult.getOrDefault("exceptionStackTrace", ""));
        // 设置最终结果状态码
        finalJudgeRes.setStatus((Integer) judgeResult.get("code"));
        // 设置最大时间和最大空间不超过题目限制时间和空间
        // kb
        Integer memory = (Integer) judgeResult.get("memory");
        finalJudgeRes.setMemory(Math.min(memory, problem.getMemoryLimit() * 1024));
        // ms
        Integer time = (Integer) judgeResult.get("time");
        finalJudgeRes.setTime(Math.min(time, problem.getTimeLimit()));
        // score
        finalJudgeRes.setScore((Integer) judgeResult.getOrDefault("score", null));
        // oi_rank_score
        finalJudgeRes.setOiRankScore((Integer) judgeResult.getOrDefault("oiRankScore", null));

        return finalJudgeRes;
    }


    // 进行最终测试结果的判断（除编译失败外的评测状态码和时间，空间,OI题目的得分）
    private HashMap<String, Object> getJudgeInfo(List<JSONObject> testCaseResultList,
                                                 Problem problem,
                                                 Judge judge,
                                                 String judgeCaseMode) {

        boolean isACM = ContestType.TYPE_ACM.getCode().equals(problem.getProgramType());

        List<JSONObject> errorTestCaseList = new LinkedList<>();

        List<JudgeCase> allCaseResList = new LinkedList<>();

        // 记录所有测试点的结果
        testCaseResultList.forEach(jsonObject -> {
            Integer time = jsonObject.getLong("time").intValue();
            Integer memory = jsonObject.getLong("memory").intValue();
            Integer status = jsonObject.getInt("status");

            Long caseId = jsonObject.getLong("caseId", null);
            Integer groupNum = jsonObject.getInt("groupNum", null);
            Integer seq = jsonObject.getInt("seq", 0);
            String inputFileName = jsonObject.getStr("inputFileName");
            String outputFileName = jsonObject.getStr("outputFileName");
            String msg = jsonObject.getStr("errMsg");
            JudgeCase judgeCase = new JudgeCase();
            judgeCase.setTime(time)
                     .setMemory(memory)
                     .setStatus(status)
                     .setInputData(inputFileName)
                     .setOutputData(outputFileName)
                     .setProblemId(problem.getId())
                     .setUserId(judge.getUserId())
                     .setCaseId(caseId)
                     .setSeq(seq)
                     .setGroupNum(groupNum)
                     .setMode(judgeCaseMode)
                     .setJudgeId(judge.getId());

            if (!StringUtils.isEmpty(msg) && !Objects.equals(status, JudgeStatus.STATUS_COMPILE_ERROR.getStatus())) {
                judgeCase.setUserOutput(msg);
            }

            if (isACM) {
                if (!Objects.equals(status, JudgeStatus.STATUS_ACCEPTED.getStatus())) {
                    errorTestCaseList.add(jsonObject);
                }
            }
            else {
                int oiScore = jsonObject.getInt("score");
                if (Objects.equals(status, JudgeStatus.STATUS_ACCEPTED.getStatus())) {
                    judgeCase.setScore(oiScore);
                }
                else if (Objects.equals(status, JudgeStatus.STATUS_PARTIAL_ACCEPTED.getStatus())) {
                    errorTestCaseList.add(jsonObject);
                    Double percentage = jsonObject.getDouble("percentage");
                    if (percentage != null) {
                        int score = (int) Math.floor(percentage * oiScore);
                        judgeCase.setScore(score);
                    }
                    else {
                        judgeCase.setScore(0);
                    }
                }
                else {
                    errorTestCaseList.add(jsonObject);
                    judgeCase.setScore(0);
                }
            }

            allCaseResList.add(judgeCase);
        });

        // 更新到数据库
        boolean addCaseRes = judgeCaseService.saveBatch(allCaseResList);
        if (!addCaseRes) {
            log.error("题号为：%d，提交id为：%d的各个测试数据点的结果更新到数据库操作失败".formatted(problem.getId(), judge.getId()));
        }

        // 获取判题的运行时间，运行空间，OI得分
        HashMap<String, Object> result = computeResultInfo(allCaseResList,
                isACM,
                errorTestCaseList.size(),
                problem.getIoScore(),
                problem.getDifficulty(),
                judgeCaseMode);

        // 如果该题为ACM类型的题目，多个测试点全部正确则AC，否则取第一个错误的测试点的状态
        // 如果该题为OI类型的题目, 若多个测试点全部正确则AC，若全部错误则取第一个错误测试点状态，否则为部分正确
        if (errorTestCaseList.size() == 0) { // 全部测试点正确，则为AC
            result.put("code", JudgeStatus.STATUS_ACCEPTED.getStatus());
        }
        else if (isACM || errorTestCaseList.size() == testCaseResultList.size()) {
            errorTestCaseList.sort(Comparator.comparingInt(jsonObject -> jsonObject.getInt("seq")));
            result.put("code", errorTestCaseList.get(0).getInt("status"));
            result.put("errMsg", errorTestCaseList.get(0).getStr("errMsg", ""));
        }
        else {
            result.put("code", JudgeStatus.STATUS_PARTIAL_ACCEPTED.getStatus());
        }
        return result;
    }

    private String getFinalJudgeCaseMode(Integer type, String problemJudgeCaseMode, String infoJudgeCaseMode) {
        if (problemJudgeCaseMode == null) {
            return infoJudgeCaseMode;
        }
        if (Objects.equals(type, ContestType.TYPE_ACM.getCode())) {
            // ACM题目 以题目现有的judgeCaseMode为准
            return problemJudgeCaseMode;
        }
        else {
            // OI题目 涉及到可能有子任务分组评测，还是依赖info文件的配置为准
            return infoJudgeCaseMode;
        }
    }

    // 获取判题的运行时间，运行空间，OI得分
    private HashMap<String, Object> computeResultInfo(List<JudgeCase> allTestCaseResultList,
                                                      Boolean isACM,
                                                      Integer errorCaseNum,
                                                      Integer totalScore,
                                                      Integer problemDifficulty,
                                                      String judgeCaseMode) {
        HashMap<String, Object> result = new HashMap<>();
        // 用时和内存占用保存为多个测试点中最长的
        allTestCaseResultList.stream().max(Comparator.comparing(JudgeCase::getTime))
                             .ifPresent(t -> result.put("time", t.getTime()));

        allTestCaseResultList.stream().max(Comparator.comparing(JudgeCase::getMemory))
                             .ifPresent(t -> result.put("memory", t.getMemory()));

        // OI题目计算得分
        if (!isACM) {
            // 全对的直接用总分*0.1+2*题目难度
            if (errorCaseNum == 0 && JudgeCaseMode.DEFAULT.getMode().equals(judgeCaseMode)) {
                int oiRankScore = (int) Math.round(totalScore * 0.1 + 2 * problemDifficulty);
                result.put("score", totalScore);
                result.put("oiRankScore", oiRankScore);
            }
            else {
                int sumScore = 0;
                if (JudgeCaseMode.SUBTASK_LOWEST.getMode().equals(judgeCaseMode)) {
                    HashMap<Integer, Integer> groupNumMapScore = new HashMap<>();
                    for (JudgeCase testcaseResult : allTestCaseResultList) {
                        groupNumMapScore.merge(testcaseResult.getGroupNum(), testcaseResult.getScore(), Math::min);
                    }
                    for (Integer minScore : groupNumMapScore.values()) {
                        sumScore += minScore;
                    }
                }
                else if (JudgeCaseMode.SUBTASK_AVERAGE.getMode().equals(judgeCaseMode)) {
                    // 预处理 切换成Map Key: groupNum Value: <count,sum_score>
                    HashMap<Integer, Pair_<Integer, Integer>> groupNumMapScore = new HashMap<>();
                    for (JudgeCase testcaseResult : allTestCaseResultList) {
                        Pair_<Integer, Integer> pair = groupNumMapScore.get(testcaseResult.getGroupNum());
                        if (pair == null) {
                            groupNumMapScore.put(testcaseResult.getGroupNum(), new Pair_<>(1, testcaseResult.getScore()));
                        }
                        else {
                            int count = pair.getKey() + 1;
                            int score = pair.getValue() + testcaseResult.getScore();
                            groupNumMapScore.put(testcaseResult.getGroupNum(), new Pair_<>(count, score));
                        }
                    }
                    for (Pair_<Integer, Integer> pair : groupNumMapScore.values()) {
                        sumScore += (int) Math.round(pair.getValue() * 1.0 / pair.getKey());
                    }
                }
                else {
                    for (JudgeCase testcaseResult : allTestCaseResultList) {
                        sumScore += testcaseResult.getScore();
                    }
                }
                if (totalScore != 0 && sumScore > totalScore) {
                    sumScore = totalScore;
                }
                // 测试点总得分*0.1+2*题目难度*（测试点总得分/题目总分）
                int oiRankScore = (int) Math.round(sumScore * 0.1 + 2 * problemDifficulty * (sumScore * 1.0 / totalScore));
                result.put("score", sumScore);
                result.put("oiRankScore", oiRankScore);
            }
        }
        return result;
    }

    private Boolean checkOrCompileExtraProgram(Problem problem) throws SystemException {

        JudgeMode judgeMode = problem.judgeMode();

        String currentVersion = problem.getCaseVersion();

        LanguageConfig languageConfig;

        String programFilePath;

        String programVersionPath;

        switch (judgeMode) {
            case DEFAULT:
                return true;
            case SPJ:
                languageConfig = languageConfigLoader.getLanguageConfigByName("SPJ-" + problem.getSpjLanguage());

                programFilePath = JudgeDir.SPJ_WORKPLACE_DIR.getContent() + File.separator +
                        problem.getId() + File.separator + languageConfig.getExeName();

                programVersionPath = JudgeDir.SPJ_WORKPLACE_DIR.getContent() + File.separator +
                        problem.getId() + File.separator + "version";

                // 如果不存在该已经编译好的程序，则需要再次进行编译
                if (!FileUtil.exist(programFilePath) || !FileUtil.exist(programVersionPath)) {
                    boolean isCompileSpjOk = Compiler.compileSpj(problem.getSpjCode(), problem.getId(), problem.getSpjLanguage(),
                            JudgeUtils.getProblemExtraFileMap(problem, "judge"));

                    FileWriter fileWriter = new FileWriter(programVersionPath);
                    fileWriter.write(currentVersion);
                    return isCompileSpjOk;
                }

                FileReader spjVersionReader = new FileReader(programVersionPath);
                String recordSpjVersion = spjVersionReader.readString();

                // 版本变动也需要重新编译
                if (!currentVersion.equals(recordSpjVersion)) {
                    boolean isCompileSpjOk = Compiler.compileSpj(problem.getSpjCode(), problem.getId(), problem.getSpjLanguage(),
                            JudgeUtils.getProblemExtraFileMap(problem, "judge"));
                    FileWriter fileWriter = new FileWriter(programVersionPath);
                    fileWriter.write(currentVersion);
                    return isCompileSpjOk;
                }

                break;
            case INTERACTIVE:
                languageConfig = languageConfigLoader.getLanguageConfigByName("INTERACTIVE-" + problem.getSpjLanguage());
                programFilePath = JudgeDir.INTERACTIVE_WORKPLACE_DIR.getContent() + File.separator +
                        problem.getId() + File.separator + languageConfig.getExeName();

                programVersionPath = JudgeDir.INTERACTIVE_WORKPLACE_DIR.getContent() + File.separator +
                        problem.getId() + File.separator + "version";

                // 如果不存在该已经编译好的程序，则需要再次进行编译 版本变动也需要重新编译
                if (!FileUtil.exist(programFilePath) || !FileUtil.exist(programVersionPath)) {
                    boolean isCompileInteractive = Compiler.compileInteractive(problem.getSpjCode(), problem.getId(), problem.getSpjLanguage(),
                            JudgeUtils.getProblemExtraFileMap(problem, "judge"));
                    FileWriter fileWriter = new FileWriter(programVersionPath);
                    fileWriter.write(currentVersion);
                    return isCompileInteractive;
                }

                FileReader interactiveVersionFileReader = new FileReader(programVersionPath);
                String recordInteractiveVersion = interactiveVersionFileReader.readString();

                // 版本变动也需要重新编译
                if (!currentVersion.equals(recordInteractiveVersion)) {
                    boolean isCompileInteractive = Compiler.compileSpj(problem.getSpjCode(), problem.getId(), problem.getSpjLanguage(),
                            JudgeUtils.getProblemExtraFileMap(problem, "judge"));

                    FileWriter fileWriter = new FileWriter(programVersionPath);
                    fileWriter.write(currentVersion);

                    return isCompileInteractive;
                }

                break;
            default:
                throw new AlertsException("The problem mode is error:" + judgeMode);
        }

        return true;
    }
}

package com.glowxq.oj.judge.processor.helper;

import com.glowxq.core.common.exception.common.AlertsException;
import com.glowxq.core.common.exception.common.BusinessException;
import com.glowxq.core.util.StreamUtils;
import com.glowxq.oj.judge.enums.JudgeStatus;
import com.glowxq.oj.judge.pojo.po.Judge;
import com.glowxq.oj.judge.service.JudgeService;
import com.glowxq.oj.problem.enums.ProblemType;
import com.glowxq.oj.problem.pojo.po.Problem;
import com.glowxq.oj.problem.pojo.po.ProblemOption;
import com.glowxq.oj.problem.service.ProblemOptionService;
import com.glowxq.oj.problem.service.ProblemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author glowxq
 * @version 1.0
 * @date 2025/3/20
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class FixedJudgeHelper {

    private final JudgeService judgeService;

    private final ProblemService problemService;

    private final ProblemOptionService problemOptionService;

    /**
     * 评测客观题
     *
     * @param judge
     * @return
     */
    public Judge judgeFixed(Judge judge) {
        Problem problem = problemService.getById(judge.getProblemId());

        return judgeFixed(problem, judge);
    }

    private Judge judgeFixed(Problem problem, Judge judge) {
        ProblemType problemType = problem.problemType();
        boolean answer = false;
        if (!problemType.isFixed()) {
            throw new BusinessException("判题错误，题目类型异常");
        }
        List<ProblemOption> problemOptions = problemOptionService.listProblemId(problem.getId());
        List<ProblemOption> replyOptions = judge.replyOptions();
        switch (problemType) {
            // 选择类题型
            case TrueFalse:
            case SingleChoice:
            case MultipleChoice:
                // 选择的答案
                Set<String> choiceOptionIds = replyOptions.stream().filter(ProblemOption::getAnswer).map(ProblemOption::getOptionKey).collect(Collectors.toSet());
                // 题目的答案
                Set<String> answerIds = problemOptions.stream().filter(ProblemOption::getAnswer).map(ProblemOption::getOptionKey).collect(Collectors.toSet());
                // 比较答案是否完全一致
                answer = answerIds.equals(choiceOptionIds);
                JudgeStatus choiceJudgeStatus = answer ? JudgeStatus.STATUS_ACCEPTED : JudgeStatus.STATUS_WRONG_ANSWER;
                judge.setStatus(choiceJudgeStatus.getStatus());
                break;
            //     填空类题型
            case FillBlank:
                Map<String, String> replyOptionMap = StreamUtils.toMap(replyOptions, ProblemOption::getOptionKey, ProblemOption::getOptionContent);
                Map<String, String> answerOptionMap = StreamUtils.toMap(problemOptions, ProblemOption::getOptionKey, ProblemOption::getOptionContent);
                //     填空题判断逻辑

                int totalAnswerCount = answerOptionMap.size();
                int trueCount = 0;
                for (Map.Entry<String, String> answerOption : answerOptionMap.entrySet()) {
                    String key = answerOption.getKey();
                    String choiceValue = Optional.ofNullable(replyOptionMap.get(key)).orElse(com.glowxq.core.util.StringUtils.EMPTY);
                    String answerValue = answerOption.getValue();
                    answer = com.glowxq.core.util.StringUtils.equalsAnyIgnoreCase(choiceValue, answerValue);
                    if (answer) {
                        trueCount++;
                    }
                }
                JudgeStatus fillJudgeStatus = totalAnswerCount == trueCount ? JudgeStatus.STATUS_ACCEPTED : JudgeStatus.STATUS_WRONG_ANSWER;
                judge.setStatus(fillJudgeStatus.getStatus());
                break;
            default:
                throw new AlertsException("判题错误，题目类型异常");
        }
        return judge;
    }

    private Judge judgeFixed2(Problem problem, Judge judge) {
        ProblemType problemType = problem.problemType();
        boolean answer = false;
        if (!problemType.isFixed()) {
            throw new BusinessException("判题错误，题目类型异常");
        }
        List<ProblemOption> problemOptions = problemOptionService.listProblemId(problem.getId());
        List<ProblemOption> replyOptions = judge.replyOptions();
        switch (problemType) {
            // 选择类题型
            case TrueFalse:
            case SingleChoice:
            case MultipleChoice:
                // 选择的答案
                Set<Long> choiceOptionIds = replyOptions.stream().map(ProblemOption::getId).collect(Collectors.toSet());
                // 题目的答案
                Set<Long> answerIds = problemOptions.stream().filter(ProblemOption::getAnswer).map(ProblemOption::getId).collect(Collectors.toSet());
                // 比较答案是否完全一致
                answer = answerIds.equals(choiceOptionIds);
                JudgeStatus choiceJudgeStatus = answer ? JudgeStatus.STATUS_ACCEPTED : JudgeStatus.STATUS_WRONG_ANSWER;
                judge.setStatus(choiceJudgeStatus.getStatus());
                break;
            //     填空类题型
            case FillBlank:
                Map<Long, String> replyOptionMap = StreamUtils.toMap(replyOptions, ProblemOption::getId, ProblemOption::getOptionContent);
                Map<Long, String> answerOptionMap = StreamUtils.toMap(problemOptions, ProblemOption::getId, ProblemOption::getOptionContent);
                //     填空题判断逻辑

                int totalAnswerCount = answerOptionMap.size();
                int trueCount = 0;
                for (Map.Entry<Long, String> answerOption : answerOptionMap.entrySet()) {
                    Long key = answerOption.getKey();
                    String choiceValue = Optional.ofNullable(replyOptionMap.get(key)).orElse(StringUtils.EMPTY);
                    String answerValue = answerOption.getValue();
                    answer = StringUtils.equalsAnyIgnoreCase(choiceValue, answerValue);
                    if (answer) {
                        trueCount++;
                    }
                }
                JudgeStatus fillJudgeStatus = totalAnswerCount == trueCount ? JudgeStatus.STATUS_ACCEPTED : JudgeStatus.STATUS_WRONG_ANSWER;
                judge.setStatus(fillJudgeStatus.getStatus());
                break;
            default:
                throw new BusinessException("判题错误，题目类型异常");
        }
        return judge;
    }
}

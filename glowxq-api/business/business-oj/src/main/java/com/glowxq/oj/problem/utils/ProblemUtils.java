package com.glowxq.oj.problem.utils;

import com.glowxq.oj.judge.enums.JudgeMode;
import com.glowxq.oj.problem.pojo.dto.ProblemBO;
import com.glowxq.oj.problem.pojo.dto.ProblemCreateUpdateDTO;
import com.glowxq.oj.problem.pojo.dto.ProblemOptionBO;
import com.glowxq.oj.problem.pojo.po.Problem;
import com.glowxq.oj.problem.pojo.po.ProblemOption;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author glowxq
 * @version 1.0
 * @date 2025/3/20
 */
public class ProblemUtils {

    public static void resetProblemInfo(ProblemCreateUpdateDTO dto) {
        ProblemBO problemBo = dto.getProblemBo();
        // 默认则 将特判程序或交互程序代码设置为空
        JudgeMode judgeMode = dto.judgeMode();
        if (JudgeMode.DEFAULT.equals(judgeMode)) {
            problemBo.setSpjLanguage(null);
            problemBo.setSpjCode(null);
        }
        // 设置测试样例的版本号
        problemBo.setCaseVersion(String.valueOf(System.currentTimeMillis()));
        dto.setProblemBo(problemBo);
    }

    @NotNull
    public static List<ProblemOption> buildProblemOptions(ProblemCreateUpdateDTO dto, Problem problem) {
        List<ProblemOptionBO> optionsDtoList = dto.getOptions();
        List<ProblemOption> problemOptionList = optionsDtoList.stream().map(option -> {
            ProblemOption problemOption = option.buildEntity();
            problemOption.setProblemId(problem.getId());
            problemOption.setProblemType(problem.getProblemType());
            problemOption.setProblemKey(problem.getProblemKey());
            return problemOption;
        }).toList();
        return problemOptionList;
    }

    /**
     * 为判断题生成默认选项（"对"和"错"）
     * 当前端未传递判断题选项时自动生成，默认"对"为正确答案
     *
     * @param dto 题目创建DTO
     * @return 默认的判断题选项列表
     */
    public static List<ProblemOptionBO> buildDefaultTrueFalseOptions(ProblemCreateUpdateDTO dto) {
        ProblemOptionBO optionA = new ProblemOptionBO();
        optionA.setOptionKey("A");
        optionA.setOptionContent("对");
        optionA.setScore(10);
        optionA.setAnswer(true);

        ProblemOptionBO optionB = new ProblemOptionBO();
        optionB.setOptionKey("B");
        optionB.setOptionContent("错");
        optionB.setScore(0);
        optionB.setAnswer(false);

        return List.of(optionA, optionB);
    }
}

package com.glowxq.oj.submit.normal.dto.param;

import com.glowxq.oj.judge.enums.JudgeSceneType;
import com.glowxq.oj.judge.enums.SubmitType;
import com.glowxq.oj.problem.enums.ProblemType;
import com.glowxq.oj.problem.pojo.po.ProblemOption;
import com.glowxq.oj.submit.SubmitDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * @author glowxq
 * @version 1.0
 * @date 2025/3/19
 */
public interface FixedNormalSubmitParam extends SubmitDTO {

    @NotBlank(message = "题目id不能为空")
    Long getProblemId();

    @NotNull(message = "测评场景类型不能为空")
    JudgeSceneType getJudgeSceneType();

    @NotNull(message = "提交类型不能为空")
    SubmitType getSubmitType();

    /**
     * 提交类型业务ID 比赛、训练、班级等等
     */
    @NotBlank(message = "提交类型业务ID 不能为空")
    Long getBusinessId();

    @NotNull(message = "题目类型不能为空")
    ProblemType getProblemType();

    /**
     * 客观题 答题内容
     */
    @NotNull(message = "题目类型不能为空")
    List<ProblemOption> getReplyOptions();
}

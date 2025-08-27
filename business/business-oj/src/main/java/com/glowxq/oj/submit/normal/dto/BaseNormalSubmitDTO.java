package com.glowxq.oj.submit.normal.dto;

import com.glowxq.oj.judge.enums.JudgeSceneType;
import com.glowxq.oj.judge.enums.SubmitType;
import com.glowxq.oj.problem.enums.ProblemType;
import com.glowxq.oj.submit.SubmitDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author glowxq
 * @version 1.0
 * @date 2025/3/19
 */
@Data
public class BaseNormalSubmitDTO implements SubmitDTO {

    @NotBlank(message = "题目id不能为空")
    private Long problemId;

    @NotNull(message = "测评场景类型不能为空")
    private JudgeSceneType judgeSceneType;

    @NotNull(message = "提交类型不能为空")
    private SubmitType submitType;

    /**
     * 提交类型业务ID 比赛、训练、班级等等
     */
    @NotBlank(message = "提交类型业务ID 不能为空")
    private Long businessId;

    @NotNull(message = "题目类型不能为空")
    private ProblemType problemType;
}

package com.glowxq.oj.submit.normal.dto.param;

import com.glowxq.oj.judge.enums.JudgeSceneType;
import com.glowxq.oj.judge.enums.SubmitType;
import com.glowxq.oj.problem.enums.ProblemType;
import com.glowxq.oj.submit.SubmitDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.URL;

/**
 * @author glowxq
 * @version 1.0
 * @date 2025/3/19
 */
interface ProgramNormalSubmitParam extends SubmitDTO {

    @NotBlank(message = "题目id不能为空")
    Long getProblemId();

    @NotBlank(message = "代码语言选择不能为空")
    String getLanguage();

    @NotBlank(message = "提交的代码不能为空")
    @Length(min = 1, max = 10000, message = "提交的代码长度不能超1～10000")
    String getCode();

    @NotNull(message = "测评场景类型不能为空")
    JudgeSceneType getJudgeSceneType();

    @NotNull(message = "提交类型不能为空")
    SubmitType getSubmitType();

    @NotNull(message = "提交类型业务ID 不能为空")
    Long getBusinessId();

    @URL
    String getFlowImage();

    @NotNull(message = "题目类型不能为空")
    ProblemType getProblemType();
}

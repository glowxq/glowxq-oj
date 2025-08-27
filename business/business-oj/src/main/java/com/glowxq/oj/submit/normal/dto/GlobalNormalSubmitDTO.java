package com.glowxq.oj.submit.normal.dto;

import com.glowxq.oj.judge.enums.JudgeSceneType;
import com.glowxq.oj.judge.enums.SubmitType;
import com.glowxq.oj.problem.enums.ProblemType;
import com.glowxq.oj.problem.pojo.po.ProblemOption;
import com.glowxq.oj.submit.normal.dto.param.GlobalNormalSubmitParam;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.URL;

import java.util.List;

/**
 * @author glowxq
 * @version 1.0
 * @date 2025/3/19
 */
@Data
public class GlobalNormalSubmitDTO implements GlobalNormalSubmitParam {

    @NotBlank(message = "题目id不能为空")
    private Long problemId;

    @NotBlank(message = "代码语言选择不能为空")
    private String language;

    @NotBlank(message = "提交的代码不能为空")
    @Length(min = 1, max = 10000, message = "提交的代码长度不能超1～10000")
    private String code;

    @NotNull(message = "测评场景类型不能为空")
    private JudgeSceneType judgeSceneType;

    @NotNull(message = "提交类型不能为空")
    private SubmitType submitType;

    /**
     * 提交类型业务ID 比赛、训练、班级等等
     */
    @NotBlank(message = "提交类型业务ID 不能为空")
    private Long businessId;

    @URL
    private String flowImage;

    @NotNull(message = "是否为远程判题不能为空")
    private Boolean isRemote;

    @NotNull(message = "题目类型不能为空")
    private ProblemType problemType;

    /**
     * 客观题 答题内容
     */
    private List<ProblemOption> replyOptions;
}

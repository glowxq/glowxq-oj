package com.glowxq.oj.submit.test;

import com.glowxq.oj.judge.enums.JudgeSceneType;
import com.glowxq.oj.judge.processor.bo.TestJudgeReq;
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
public class TestSubmitDTO implements SubmitDTO {

    /**
     * 题目id
     */
    @NotNull(message = "题目id不能为空")
    private Long problemId;

    /**
     * 评测场景类型
     */
    @NotNull(message = "评测场景类型不能为空")
    private JudgeSceneType judgeSceneType;

    /**
     * 代码
     */
    @NotBlank(message = "代码不能为空")
    private String code;

    /**
     * 编程语言
     */
    @NotBlank(message = "编程语言不能为空")
    private String language;

    /**
     * 输入
     */
    @NotBlank(message = "输入不能为空")
    private String userInput;

    /**
     * 预期输出
     */
    private String expectedOutput;

    /**
     * 是否为原创OJ的题目
     */
    private Boolean isRemoteJudge;

    /**
     * text/x-csrc 用于鉴别语言
     */
    private String mode;

    private TestJudgeReq testJudgeReq;

    ;
}

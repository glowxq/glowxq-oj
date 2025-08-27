package com.glowxq.oj.submit.compile.bo;

import com.glowxq.oj.judge.enums.SubmitType;
import com.glowxq.oj.submit.SubmitDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.HashMap;

/**
 * @author glowxq
 * @version 1.0
 * @date 2025/3/20
 */
@Data
public class CompileSubmitDTO implements SubmitDTO {

    /**
     * 题目id
     */
    private Long problemId;

    /**
     * 代码
     */
    @NotBlank(message = "代码不能为空")
    private String code;

    /**
     * 评测类型
     */
    @NotNull(message = "评测类型不能为空")
    private SubmitType submitType;

    /**
     * 编程语言
     */
    @NotBlank(message = "编程语言不能为空")
    private String language;

    /**
     * 编译所需的额外文件，key:文件名,value:文件内容
     */
    private HashMap<String, String> extraFiles;
}

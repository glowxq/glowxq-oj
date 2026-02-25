package com.glowxq.oj.problem.pojo.dto;

import com.glowxq.core.common.valid.annotation.InEnum;
import com.glowxq.oj.problem.enums.ProblemSourceType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "Problem上传DTO")
public class ProblemImportProgramDTO {

    /**
     * URL
     */
    @NotBlank(message = "URL不能为空")
    private String fileUrl;

    /**
     * 题目来源
     */
    @InEnum(enumClass = ProblemSourceType.class)
    @NotNull(message = "题目来源必填")
    private ProblemSourceType problemSourceType;

    /**
     * 是否需要插入不存在的标签
     */
    @NotNull(message = "是否需要插入不存在的标签必填")
    private Boolean needInsertUnExistTag;
}

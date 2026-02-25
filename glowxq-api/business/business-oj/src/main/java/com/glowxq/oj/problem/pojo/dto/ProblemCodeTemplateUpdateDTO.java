package com.glowxq.oj.problem.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * <p>
 * ProblemCodeTemplate修改DTO
 * </p>
 *
 * @author glowxq
 * @since 2025-03-18
 */
@Data
@Schema(description = "ProblemCodeTemplate修改DTO")
public class ProblemCodeTemplateUpdateDTO {

    @Schema(description = "")
    private Long id;

    @Schema(description = "题目ID")
    private Long problemId;

    @Schema(description = "语言ID")
    private Long languageId;

    @Schema(description = "模版代码")
    private String code;

    @Schema(description = "状态")
    private Boolean enable;
}
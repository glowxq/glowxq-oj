package com.glowxq.oj.problem.pojo.dto;

import com.glowxq.core.common.entity.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * <p>
 * ProblemCodeTemplate查询DTO
 * </p>
 *
 * @author glowxq
 * @since 2025-03-18
 */
@Data
@Schema(description = "ProblemCodeTemplate查询DTO")
public class ProblemCodeTemplateListDTO extends PageQuery {

    @Schema(description = "题目ID")
    private Long problemId;

    @Schema(description = "语言ID")
    private Long languageId;

    @Schema(description = "模版代码")
    private String code;

    @Schema(description = "状态")
    private Boolean enable;
}
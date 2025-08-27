package com.glowxq.oj.problem.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * <p>
 * ProblemLanguage修改DTO
 * </p>
 *
 * @author glowxq
 * @since 2025-03-17
 */
@Data
@Schema(description = "ProblemLanguage修改DTO")
public class ProblemLanguageUpdateDTO {

    @Schema(description = "ID")
    private String id;

    @Schema(description = "题目ID")
    private Long problemId;

    @Schema(description = "语言名")
    private String name;

    @Schema(description = "题目KEY")
    private String problemKey;

    @Schema(description = "题目语言")
    private String problemLanguage;

    @Schema(description = "语言ID")
    private Long languageId;
}
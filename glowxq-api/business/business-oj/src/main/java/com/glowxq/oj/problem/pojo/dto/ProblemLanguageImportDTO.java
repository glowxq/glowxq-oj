package com.glowxq.oj.problem.pojo.dto;

import cn.idev.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * <p>
 * ProblemLanguage导入DTO
 * </p>
 *
 * @author glowxq
 * @since 2025-03-17
 */
@Data
@Schema(description = "ProblemLanguage导入DTO")
public class ProblemLanguageImportDTO {

    @ExcelProperty(value = "题目ID")
    @Schema(description = "题目ID")
    private Long problemId;

    @ExcelProperty(value = "题目KEY")
    @Schema(description = "题目KEY")
    private String problemKey;

    @Schema(description = "语言名")
    private String name;

    @ExcelProperty(value = "题目语言")
    @Schema(description = "题目语言")
    private String problemLanguage;

    @ExcelProperty(value = "语言ID")
    @Schema(description = "语言ID")
    private Long languageId;
}
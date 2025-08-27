package com.glowxq.oj.problem.pojo.dto;

import cn.idev.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * <p>
 * ProblemCodeTemplate导入DTO
 * </p>
 *
 * @author glowxq
 * @since 2025-03-18
 */
@Data
@Schema(description = "ProblemCodeTemplate导入DTO")
public class ProblemCodeTemplateImportDTO {

    @ExcelProperty(value = "题目ID")
    @Schema(description = "题目ID")
    private Long problemId;

    @ExcelProperty(value = "语言ID")
    @Schema(description = "语言ID")
    private Long languageId;

    @ExcelProperty(value = "模版代码")
    @Schema(description = "模版代码")
    private String code;

    @ExcelProperty(value = "状态")
    @Schema(description = "状态")
    private Boolean enable;
}
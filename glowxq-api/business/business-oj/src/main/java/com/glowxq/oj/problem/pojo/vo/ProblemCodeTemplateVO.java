package com.glowxq.oj.problem.pojo.vo;

import cn.idev.excel.annotation.ExcelIgnore;
import cn.idev.excel.annotation.ExcelProperty;
import com.glowxq.core.common.entity.base.BaseVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * <p>
 * ProblemCodeTemplate返回vo
 * </p>
 *
 * @author glowxq
 * @since 2025-03-18
 */
@Data
@Schema(description = "ProblemCodeTemplate返回vo")
public class ProblemCodeTemplateVO implements BaseVO {

    @ExcelIgnore
    @Schema(description = "")
    private Long id;

    @ExcelProperty(value = "题目ID")
    @Schema(description = "题目ID")
    private Long problemId;

    @ExcelProperty(value = "语言ID")
    @Schema(description = "语言ID")
    private Long languageId;

    @Schema(description = "语言名")
    private String name;

    @ExcelProperty(value = "模版代码")
    @Schema(description = "模版代码")
    private String code;

    @ExcelProperty(value = "状态")
    @Schema(description = "状态")
    private Boolean enable;
}
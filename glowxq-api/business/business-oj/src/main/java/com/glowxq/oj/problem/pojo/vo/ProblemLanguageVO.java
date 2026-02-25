package com.glowxq.oj.problem.pojo.vo;

import cn.idev.excel.annotation.ExcelIgnore;
import cn.idev.excel.annotation.ExcelProperty;
import com.glowxq.core.common.entity.base.BaseVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * <p>
 * ProblemLanguage返回vo
 * </p>
 *
 * @author glowxq
 * @since 2025-03-17
 */
@Data
@Schema(description = "ProblemLanguage返回vo")
public class ProblemLanguageVO implements BaseVO {

    @ExcelIgnore
    @Schema(description = "ID")
    private String id;

    @ExcelProperty(value = "题目ID")
    @Schema(description = "题目ID")
    private Long problemId;

    @ExcelProperty(value = "题目KEY")
    @Schema(description = "题目KEY")
    private String problemKey;

    @ExcelProperty(value = "题目语言")
    @Schema(description = "题目语言")
    private String problemLanguage;

    @ExcelProperty(value = "语言ID")
    @Schema(description = "语言ID")
    private Long languageId;
}
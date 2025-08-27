package com.glowxq.oj.problem.pojo.vo;

import cn.idev.excel.annotation.ExcelIgnore;
import cn.idev.excel.annotation.ExcelProperty;
import com.glowxq.core.common.entity.base.BaseVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * <p>
 * ProblemBlack返回vo
 * </p>
 *
 * @author glowxq
 * @since 2025-04-11
 */
@Data
@Schema(description = "ProblemBlack返回vo")
public class ProblemBlackVO implements BaseVO{

    @ExcelIgnore
    @Schema(description =  "")
    private Long id;

    @ExcelProperty(value = "题目ID")
    @Schema(description =  "题目ID")
    private Long problemId;

    @ExcelProperty(value = "拉黑对象ID")
    @Schema(description =  "拉黑对象ID")
    private Long businessId;

    @ExcelProperty(value = "拉黑对象名")
    @Schema(description =  "拉黑对象名")
    private String businessName;

    @ExcelProperty(value = "拉黑对象类型")
    @Schema(description =  "拉黑对象类型")
    private String type;

}
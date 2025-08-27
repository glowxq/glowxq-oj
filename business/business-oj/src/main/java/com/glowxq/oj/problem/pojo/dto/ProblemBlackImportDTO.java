package com.glowxq.oj.problem.pojo.dto;

import cn.idev.excel.annotation.ExcelProperty;
import com.glowxq.core.common.entity.base.BaseDTO;
import com.glowxq.core.util.BeanCopyUtils;
import com.glowxq.oj.problem.pojo.po.ProblemBlack;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * <p>
 * ProblemBlack导入DTO
 * </p>
 *
 * @author glowxq
 * @since 2025-04-11
 */
@Data
@Schema(description = "ProblemBlack导入DTO")
public class ProblemBlackImportDTO implements BaseDTO {

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

    @Override
    public ProblemBlack buildEntity() {
        return BeanCopyUtils.copy(this, ProblemBlack.class);
    }
}
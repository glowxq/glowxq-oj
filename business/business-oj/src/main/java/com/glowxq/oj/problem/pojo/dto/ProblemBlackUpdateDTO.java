package com.glowxq.oj.problem.pojo.dto;

import com.glowxq.core.common.entity.base.BaseDTO;
import com.glowxq.core.util.BeanCopyUtils;
import com.glowxq.oj.problem.pojo.po.ProblemBlack;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * <p>
 * ProblemBlack修改DTO
 * </p>
 *
 * @author glowxq
 * @since 2025-04-11
 */
@Data
@Schema(description = "ProblemBlack修改DTO")
public class ProblemBlackUpdateDTO implements BaseDTO {

    @Schema(description =  "")
    private Long id;

    @Schema(description =  "题目ID")
    private Long problemId;

    @Schema(description =  "拉黑对象ID")
    private Long businessId;

    @Schema(description =  "拉黑对象名")
    private String businessName;

    @Schema(description =  "拉黑对象类型")
    private String type;

    @Override
    public ProblemBlack buildEntity() {
        return BeanCopyUtils.copy(this, ProblemBlack.class);
    }
}
package com.glowxq.oj.problem.pojo.dto;

import com.glowxq.core.common.entity.base.BaseDTO;
import com.glowxq.core.util.BeanCopyUtils;
import com.glowxq.oj.problem.pojo.po.ProblemCodeTemplate;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * <p>
 * ProblemCodeTemplate添加DTO
 * </p>
 *
 * @author glowxq
 * @since 2025-03-18
 */
@Data
@Schema(description = "ProblemCodeTemplate添加DTO")
public class ProblemCodeTemplateCreateDTO implements BaseDTO {

    @Schema(description = "语言ID")
    @NotNull
    private Long languageId;

    @Schema(description = "名称")
    @NotBlank
    private String name;

    @Schema(description = "模版代码")
    @NotBlank
    private String code;

    @Override
    public ProblemCodeTemplate buildEntity() {
        return BeanCopyUtils.copy(this, ProblemCodeTemplate.class);
    }
}
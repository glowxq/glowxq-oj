package com.glowxq.oj.problem.pojo.dto;

import com.glowxq.core.common.entity.base.BaseDTO;
import com.glowxq.core.util.BeanCopyUtils;
import com.glowxq.oj.problem.pojo.po.ProblemOption;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * <p>
 * ProblemOption添加DTO
 * </p>
 *
 * @author glowxq
 * @since 2025-03-17
 */
@Data
@Schema(description = "ProblemOption添加DTO")
public class ProblemOptionBO implements BaseDTO {

    @Schema(description = "选项标识(如A/B/C/D)")
    @NotNull
    private String optionKey;

    @Schema(description = "选项得分")
    @NotNull
    private Integer score;

    @Schema(description = "富文本选项内容")
    @NotBlank
    private String optionContent;

    @Schema(description = "是否正确答案(0-否|1-是)")
    @NotNull
    private Boolean answer;

    @Override
    public ProblemOption buildEntity() {
        return BeanCopyUtils.copy(this, ProblemOption.class);
    }
}
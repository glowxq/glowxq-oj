package com.glowxq.oj.problem.pojo.dto;

import com.glowxq.core.common.entity.base.BaseDTO;
import com.glowxq.core.common.valid.annotation.InEnum;
import com.glowxq.core.util.BeanCopyUtils;
import com.glowxq.oj.problem.enums.ProblemCaseType;
import com.glowxq.oj.problem.pojo.po.ProblemCase;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * <p>
 * ProblemCase添加DTO
 * </p>
 *
 * @author glowxq
 * @since 2025-03-17
 */
@Data
@Schema(description = "ProblemCase添加DTO")
public class ProblemCaseBO implements BaseDTO {

    @Schema(description = "测试样例的输入")
    @NotBlank
    private String input;

    @Schema(description = "测试样例的输出")
    @NotBlank
    private String output;

    @Schema(description = "用例类型")
    @NotBlank
    @InEnum(enumClass = ProblemCaseType.class)
    private String type;

    @Schema(description = "该测试样例的IO得分")
    private Integer score;

    @Schema(description = "subtask分组的编号")
    private Integer groupNum;

    @Override
    public ProblemCase buildEntity() {
        return BeanCopyUtils.copy(this, ProblemCase.class);
    }
}
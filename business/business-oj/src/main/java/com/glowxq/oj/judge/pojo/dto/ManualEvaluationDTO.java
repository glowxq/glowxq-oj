package com.glowxq.oj.judge.pojo.dto;

import com.glowxq.oj.judge.enums.JudgeStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * <p>
 * Judge修改DTO
 * </p>
 *
 * @author glowxq
 * @since 2025-03-17
 */
@Data
@Schema(description = "Judge修改DTO")
public class ManualEvaluationDTO {

    @Schema(description = "ID")
    @NotNull
    private String id;

    @Schema(description = "结果码具体参考文档")
    @NotNull
    private Integer judgeStatus;

    @Schema(description = "得分")
    private Integer score;

    public JudgeStatus judgeStatus() {
        return JudgeStatus.matchCode(judgeStatus);
    }


}
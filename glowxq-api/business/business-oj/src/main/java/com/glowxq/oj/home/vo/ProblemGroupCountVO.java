package com.glowxq.oj.home.vo;

import com.glowxq.oj.problem.enums.ProblemType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * @author glowxq
 * @version 1.0
 * @date 2025/5/27
 */
@Data
public class ProblemGroupCountVO implements Serializable {

    @Schema(description = "题型")
    private ProblemType problemType;

    @Schema(description = "数量")
    private Long number;
}

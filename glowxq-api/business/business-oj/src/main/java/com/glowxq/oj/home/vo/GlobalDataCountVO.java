package com.glowxq.oj.home.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * @author glowxq
 * @version 1.0
 * @date 2025/5/27
 */
@Data
public class GlobalDataCountVO implements Serializable {

    @Schema(description = "用户数量")
    private Long userCount;

    @Schema(description = "作业比赛题集数量")
    private Long topicCount;

    @Schema(description = "题目数量")
    private Long problemCount;

    @Schema(description = "Ac数量")
    private Long acCount;

    @Schema(description = "测评数量")
    private Long judgeCount;

    @Schema(description = "班级数量")
    private Long groupCount;

    @Schema(description = "课程数量")
    private Long courseCount;
}

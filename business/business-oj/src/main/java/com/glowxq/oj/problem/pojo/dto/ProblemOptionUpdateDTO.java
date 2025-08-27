package com.glowxq.oj.problem.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * <p>
 * ProblemOption修改DTO
 * </p>
 *
 * @author glowxq
 * @since 2025-03-17
 */
@Data
@Schema(description = "ProblemOption修改DTO")
public class ProblemOptionUpdateDTO {

    @Schema(description = "ID")
    private String id;

    @Schema(description = "题目ID")
    private Long problemId;

    @Schema(description = "题目KEY")
    private String problemKey;

    @Schema(description = "选项标识(如A/B/C/D)")
    private String optionKey;

    @Schema(description = "题目类型(单选/多选/判断/填空)")
    private String problemType;

    @Schema(description = "选项得分")
    private Integer score;

    @Schema(description = "富文本选项内容")
    private String optionContent;

    @Schema(description = "是否正确答案(0-否|1-是)")
    private Boolean answer;
}
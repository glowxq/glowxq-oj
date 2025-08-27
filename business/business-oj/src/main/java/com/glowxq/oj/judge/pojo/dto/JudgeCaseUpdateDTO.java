package com.glowxq.oj.judge.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * <p>
 * JudgeCase修改DTO
 * </p>
 *
 * @author glowxq
 * @since 2025-03-17
 */
@Data
@Schema(description = "JudgeCase修改DTO")
public class JudgeCaseUpdateDTO {

    @Schema(description =  "ID")
    private String id;

    @Schema(description =  "提交判题id")
    private String judgeId;

    @Schema(description =  "自定义提交ID")
    private String judgeKey;

    @Schema(description =  "用户id")
    private String userId;

    @Schema(description =  "题目ID")
    private Long problemId;

    @Schema(description =  "题目KEY")
    private String problemKey;

    @Schema(description =  "测试样例id")
    private String caseId;

    @Schema(description =  "具体看结果码")
    private Integer status;

    @Schema(description =  "测试该样例所用时间ms")
    private Integer time;

    @Schema(description =  "测试该样例所用空间KB")
    private Integer memory;

    @Schema(description =  "IO得分")
    private Integer score;

    @Schema(description =  "subtask分组的组号")
    private Integer groupNum;

    @Schema(description =  "排序")
    private Integer seq;

    @Schema(description =  "default,subtask_lowest,subtask_average")
    private String mode;

    @Schema(description =  "样例输入|比赛不可看")
    private String inputData;

    @Schema(description =  "样例输出|比赛不可看")
    private String outputData;

    @Schema(description =  "用户样例输出|比赛不可看")
    private String userOutput;

}
package com.glowxq.oj.judge.pojo.dto;

import cn.idev.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * <p>
 * JudgeCase导入DTO
 * </p>
 *
 * @author glowxq
 * @since 2025-03-17
 */
@Data
@Schema(description = "JudgeCase导入DTO")
public class JudgeCaseImportDTO {

    @ExcelProperty(value = "提交判题id")
    @Schema(description =  "提交判题id")
    private String judgeId;

    @ExcelProperty(value = "自定义提交ID")
    @Schema(description =  "自定义提交ID")
    private String judgeKey;

    @ExcelProperty(value = "用户id")
    @Schema(description =  "用户id")
    private String userId;

    @ExcelProperty(value = "题目ID")
    @Schema(description =  "题目ID")
    private Long problemId;

    @ExcelProperty(value = "题目KEY")
    @Schema(description =  "题目KEY")
    private String problemKey;

    @ExcelProperty(value = "测试样例id")
    @Schema(description =  "测试样例id")
    private String caseId;

    @ExcelProperty(value = "具体看结果码")
    @Schema(description =  "具体看结果码")
    private Integer status;

    @ExcelProperty(value = "测试该样例所用时间ms")
    @Schema(description =  "测试该样例所用时间ms")
    private Integer time;

    @ExcelProperty(value = "测试该样例所用空间KB")
    @Schema(description =  "测试该样例所用空间KB")
    private Integer memory;

    @ExcelProperty(value = "IO得分")
    @Schema(description =  "IO得分")
    private Integer score;

    @ExcelProperty(value = "subtask分组的组号")
    @Schema(description =  "subtask分组的组号")
    private Integer groupNum;

    @ExcelProperty(value = "排序")
    @Schema(description =  "排序")
    private Integer seq;

    @ExcelProperty(value = "default,subtask_lowest,subtask_average")
    @Schema(description =  "default,subtask_lowest,subtask_average")
    private String mode;

    @ExcelProperty(value = "样例输入|比赛不可看")
    @Schema(description =  "样例输入|比赛不可看")
    private String inputData;

    @ExcelProperty(value = "样例输出|比赛不可看")
    @Schema(description =  "样例输出|比赛不可看")
    private String outputData;

    @ExcelProperty(value = "用户样例输出|比赛不可看")
    @Schema(description =  "用户样例输出|比赛不可看")
    private String userOutput;

}
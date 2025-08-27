package com.glowxq.oj.problem.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * <p>
 * ProblemCase修改DTO
 * </p>
 *
 * @author glowxq
 * @since 2025-03-17
 */
@Data
@Schema(description = "ProblemCase修改DTO")
public class ProblemCaseUpdateDTO {

    @Schema(description = "ID")
    private String id;

    @Schema(description = "题目ID")
    private Long problemId;

    @Schema(description = "题目KEY")
    private String problemKey;

    @Schema(description = "测试样例的输入")
    private String input;

    @Schema(description = "测试样例的输出")
    private String output;

    @Schema(description = "在线测试样例的输入")
    private String inputUrl;

    @Schema(description = "在线测试样例的输出")
    private String outputUrl;

    @Schema(description = "用例类型(LocalFile-本地文件|OssFile-Oss文件|DataText-数据库存储)")
    private String type;

    @Schema(description = "该测试样例的IO得分")
    private Integer score;

    @Schema(description = "0禁用|1启用")
    private Boolean enable;

    @Schema(description = "subtask分组的编号")
    private Integer groupNum;
}
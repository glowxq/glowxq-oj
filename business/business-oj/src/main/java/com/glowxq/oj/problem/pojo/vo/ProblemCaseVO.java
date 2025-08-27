package com.glowxq.oj.problem.pojo.vo;

import cn.idev.excel.annotation.ExcelIgnore;
import cn.idev.excel.annotation.ExcelProperty;
import com.glowxq.core.common.entity.base.BaseVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * <p>
 * ProblemCase返回vo
 * </p>
 *
 * @author glowxq
 * @since 2025-03-17
 */
@Data
@Schema(description = "ProblemCase返回vo")
public class ProblemCaseVO implements BaseVO {

    @ExcelIgnore
    @Schema(description = "ID")
    private String id;

    @ExcelProperty(value = "题目ID")
    @Schema(description = "题目ID")
    private Long problemId;

    @ExcelProperty(value = "题目KEY")
    @Schema(description = "题目KEY")
    private String problemKey;

    @ExcelProperty(value = "测试样例的输入")
    @Schema(description = "测试样例的输入")
    private String input;

    @ExcelProperty(value = "测试样例的输出")
    @Schema(description = "测试样例的输出")
    private String output;

    @ExcelProperty(value = "在线测试样例的输入")
    @Schema(description = "在线测试样例的输入")
    private String inputUrl;

    @ExcelProperty(value = "在线测试样例的输出")
    @Schema(description = "在线测试样例的输出")
    private String outputUrl;

    @ExcelProperty(value = "用例类型(LocalFile-本地文件|OssFile-Oss文件|DataText-数据库存储)")
    @Schema(description = "用例类型(LocalFile-本地文件|OssFile-Oss文件|DataText-数据库存储)")
    private String type;

    @ExcelProperty(value = "该测试样例的IO得分")
    @Schema(description = "该测试样例的IO得分")
    private Integer score;

    @ExcelProperty(value = "0禁用|1启用")
    @Schema(description = "0禁用|1启用")
    private Boolean enable;

    @ExcelProperty(value = "subtask分组的编号")
    @Schema(description = "subtask分组的编号")
    private Integer groupNum;
}
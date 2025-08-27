package com.glowxq.oj.problem.pojo.vo;

import cn.idev.excel.annotation.ExcelIgnore;
import cn.idev.excel.annotation.ExcelProperty;
import com.glowxq.core.common.entity.base.BaseVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * <p>
 * ProblemOption返回vo
 * </p>
 *
 * @author glowxq
 * @since 2025-03-17
 */
@Data
@Schema(description = "ProblemOption返回vo")
public class ProblemOptionVO implements BaseVO {

    @ExcelIgnore
    @Schema(description = "ID")
    private String id;

    @ExcelProperty(value = "题目ID")
    @Schema(description = "题目ID")
    private Long problemId;

    @ExcelProperty(value = "题目KEY")
    @Schema(description = "题目KEY")
    private String problemKey;

    @ExcelProperty(value = "选项标识(如A/B/C/D)")
    @Schema(description = "选项标识(如A/B/C/D)")
    private String optionKey;

    @ExcelProperty(value = "题目类型(单选/多选/判断/填空)")
    @Schema(description = "题目类型(单选/多选/判断/填空)")
    private String problemType;

    @ExcelProperty(value = "选项得分")
    @Schema(description = "选项得分")
    private Integer score;

    @ExcelProperty(value = "富文本选项内容")
    @Schema(description = "富文本选项内容")
    private String optionContent;

    @ExcelProperty(value = "是否正确答案(0-否|1-是)")
    @Schema(description = "是否正确答案(0-否|1-是)")
    private Boolean answer;
}
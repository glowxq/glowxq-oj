package com.glowxq.oj.topic.pojo.vo;

import cn.idev.excel.annotation.ExcelIgnore;
import cn.idev.excel.annotation.ExcelProperty;
import com.glowxq.core.common.entity.base.BaseVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * <p>
 * TopicProblem返回vo
 * </p>
 *
 * @author glowxq
 * @since 2025-04-12
 */
@Data
@Schema(description = "TopicProblem返回vo")
public class TopicProblemVO implements BaseVO{

    @ExcelIgnore
    @Schema(description =  "")
    private Long id;

    @ExcelProperty(value = "主题ID")
    @Schema(description =  "主题ID")
    private Long topicId;

    @ExcelProperty(value = "题目ID")
    @Schema(description =  "题目ID")
    private Long problemId;

    @ExcelProperty(value = "必填")
    @Schema(description =  "必填")
    private Boolean required;

}
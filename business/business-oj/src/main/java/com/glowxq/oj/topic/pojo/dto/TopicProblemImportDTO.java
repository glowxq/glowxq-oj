package com.glowxq.oj.topic.pojo.dto;

import cn.idev.excel.annotation.ExcelProperty;
import com.glowxq.core.common.entity.base.BaseDTO;
import com.glowxq.core.util.BeanCopyUtils;
import com.glowxq.oj.topic.pojo.po.TopicProblem;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * <p>
 * TopicProblem导入DTO
 * </p>
 *
 * @author glowxq
 * @since 2025-04-12
 */
@Data
@Schema(description = "TopicProblem导入DTO")
public class TopicProblemImportDTO implements BaseDTO {

    @ExcelProperty(value = "主题ID")
    @Schema(description =  "主题ID")
    private Long topicId;

    @ExcelProperty(value = "题目ID")
    @Schema(description =  "题目ID")
    private Long problemId;

    @ExcelProperty(value = "必填")
    @Schema(description =  "必填")
    private Boolean required;

    @Override
    public TopicProblem buildEntity() {
        return BeanCopyUtils.copy(this, TopicProblem.class);
    }
}
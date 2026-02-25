package com.glowxq.oj.topic.pojo.dto;

import com.glowxq.core.common.entity.base.BaseDTO;
import com.glowxq.core.util.BeanCopyUtils;
import com.glowxq.oj.topic.pojo.po.TopicProblem;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * <p>
 * TopicProblem添加DTO
 * </p>
 *
 * @author glowxq
 * @since 2025-04-12
 */
@Data
@Schema(description = "TopicProblem添加DTO")
public class TopicProblemCreateDTO implements BaseDTO {

   @Schema(description =  "主题ID")
   private Long topicId;

   @Schema(description =  "题目ID")
   private Long problemId;

   @Schema(description =  "必填")
   private Boolean required;

    @Override
    public TopicProblem buildEntity() {
        return BeanCopyUtils.copy(this, TopicProblem.class);
    }
}
package com.glowxq.oj.topic.pojo.dto;

import com.glowxq.core.common.entity.base.BaseDTO;
import com.glowxq.core.common.valid.annotation.InEnum;
import com.glowxq.core.util.BeanCopyUtils;
import com.glowxq.oj.topic.enums.TopicJudgeType;
import com.glowxq.oj.topic.pojo.po.Topic;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * <p>
 * Topic添加DTO
 * </p>
 *
 * @author glowxq
 * @since 2025-04-12
 */
@Data
@Schema(description = "Topic添加DTO")
public class TopicRankDTO implements BaseDTO {

    @Schema(description = "主题ID")
    private Integer topicId;

    @Schema(description = "主题测评类型 ACM/OI")
    @InEnum(enumClass = TopicJudgeType.class)
    private Integer topicJudgeType = TopicJudgeType.ACM.getType();

    public TopicJudgeType topicJudgeType() {
        return TopicJudgeType.matchCode(topicJudgeType);
    }

    @Override
    public Topic buildEntity() {
        return BeanCopyUtils.copy(this, Topic.class);
    }
}
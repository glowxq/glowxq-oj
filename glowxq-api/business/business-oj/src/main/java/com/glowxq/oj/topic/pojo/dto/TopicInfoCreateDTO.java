package com.glowxq.oj.topic.pojo.dto;

import com.glowxq.core.common.entity.base.BaseDTO;
import com.glowxq.core.util.BeanCopyUtils;
import com.glowxq.oj.topic.pojo.po.TopicInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * <p>
 * TopicInfo添加DTO
 * </p>
 *
 * @author glowxq
 * @since 2025-04-12
 */
@Data
@Schema(description = "TopicInfo添加DTO")
public class TopicInfoCreateDTO implements BaseDTO {

    @Schema(description = "主题ID")
    private Long topicId;

    @Schema(description = "主题状态")
    private String status;

    @Override
    public TopicInfo buildEntity() {
        return BeanCopyUtils.copy(this, TopicInfo.class);
    }
}
package com.glowxq.oj.topic.pojo.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

/**
 * @author glowxq
 * @version 1.0
 * @date 2025/4/18
 */
@Data
public class TopicSubmitInfoDTO implements Serializable {

    @NotNull
    private Long topicId;
}

package com.glowxq.oj.topic.pojo.po;

import com.glowxq.core.common.entity.base.BaseEntity;
import com.glowxq.mysql.EntityChangeListener;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.time.LocalDateTime;

/**
 * <p>
 * 主题题目
 * </p>
 *
 * @author glowxq
 * @since 2025-04-12
 */
@Data
@Table(value = "topic_problem", onInsert = EntityChangeListener.class, onUpdate = EntityChangeListener.class)
@Schema(description = "主题题目")
public class TopicProblem implements BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    @Schema(description = "")
    private Long id;

    @Schema(description = "主题ID")
    private Long topicId;

    @Schema(description = "题目ID")
    private Long problemId;

    @Schema(description = "必填")
    private Boolean required;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "租户ID")
    private String tenantId;

    @Schema(description = "创建人ID")
    private Long createId;

    @Schema(description = "更新人ID")
    private Long updateId;
}
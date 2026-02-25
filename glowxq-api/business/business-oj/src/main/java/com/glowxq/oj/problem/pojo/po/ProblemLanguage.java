package com.glowxq.oj.problem.pojo.po;

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
 * 题目语言
 * </p>
 *
 * @author glowxq
 * @since 2025-03-17
 */
@Data
@Table(value = "problem_language", onInsert = EntityChangeListener.class, onUpdate = EntityChangeListener.class)
@Schema(description = "题目语言")
public class ProblemLanguage implements BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    @Schema(description = "ID")
    private Long id;

    @Schema(description = "题目ID")
    private Long problemId;

    @Schema(description = "题目KEY")
    private String problemKey;

    @Schema(description = "题目语言")
    private String problemLanguage;

    @Schema(description = "语言ID")
    private Long languageId;

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
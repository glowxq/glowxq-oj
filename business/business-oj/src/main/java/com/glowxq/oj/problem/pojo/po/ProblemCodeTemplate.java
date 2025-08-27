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
 * 题目代码模版
 * </p>
 *
 * @author glowxq
 * @since 2025-03-18
 */
@Data
@Table(value = "problem_code_template", onInsert = EntityChangeListener.class, onUpdate = EntityChangeListener.class)
@Schema(description = "题目代码模版")
public class ProblemCodeTemplate implements BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    @Schema(description = "")
    private Long id;

    @Schema(description = "题目ID")
    private Long problemId;

    @Schema(description = "语言ID")
    private Long languageId;

    @Schema(description = "模版代码")
    private String code;

    @Schema(description = "语言名")
    private String name;

    @Schema(description = "状态")
    private Boolean enable;

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
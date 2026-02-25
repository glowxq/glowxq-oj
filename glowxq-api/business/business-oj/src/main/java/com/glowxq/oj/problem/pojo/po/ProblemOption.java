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
 * 题目选项
 * </p>
 *
 * @author glowxq
 * @since 2025-03-17
 */
@Data
@Table(value = "problem_option", onInsert = EntityChangeListener.class, onUpdate = EntityChangeListener.class)
@Schema(description = "题目选项")
public class ProblemOption implements BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    @Schema(description = "ID")
    private Long id;

    @Schema(description = "题目ID")
    private Long problemId;

    @Schema(description = "题目KEY")
    private String problemKey;

    @Schema(description = "选项标识(如A/B/C/D)")
    private String optionKey;

    @Schema(description = "题目类型(单选/多选/判断/填空)")
    private String problemType;

    @Schema(description = "选项得分")
    private Integer score;

    @Schema(description = "富文本选项内容")
    private String optionContent;

    @Schema(description = "是否正确答案(0-否|1-是)")
    private Boolean answer;

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
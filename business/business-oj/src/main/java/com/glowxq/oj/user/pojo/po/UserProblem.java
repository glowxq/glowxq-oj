package com.glowxq.oj.user.pojo.po;

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
* 用户题目数据
* </p>
*
* @author glowxq
* @since 2025-04-03
*/
@Data
@Table(value = "user_problem", onInsert = EntityChangeListener.class, onUpdate = EntityChangeListener.class)
@Schema(description = "用户题目数据")
public class UserProblem implements BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    @Schema(description ="")
    private Long id;

    @Schema(description ="用户ID")
    private Long userId;

    @Schema(description ="题目ID")
    private Long problemId;

    @Schema(description ="题目Key")
    private String problemKey;

    @Schema(description ="题目标题ID")
    private String problemTitle;

    @Schema(description ="测评ID")
    private Long judgeId;

    @Schema(description ="测评状态")
    private Integer judgeStatus;

    @Schema(description ="作业分数")
    private Integer score;

    @Schema(description ="AC的代码")
    private String code;

    @Schema(description ="非编程题作答内容")
    private String options;

    @Schema(description ="流程图URL")
    private String flowImage;

    @Schema(description ="题目类型")
    private String problemType;

    @Schema(description ="创建时间")
    private LocalDateTime createTime;

    @Schema(description ="更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "租户ID")
    private String tenantId;

    @Schema(description ="是否删除")
    private String delFlag;

    @Schema(description ="创建人ID")
    private Long createId;

    @Schema(description ="更新人ID")
    private Long updateId;

}
package com.glowxq.oj.group.pojo.po;

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
 * 班级表
 * </p>
 *
 * @author glowxq
 * @since 2025-03-27
 */
@Data
@Table(value = "group", onInsert = EntityChangeListener.class, onUpdate = EntityChangeListener.class)
@Schema(description = "班级表")
public class Group implements BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    @Schema(description = "")
    private Long id;

    @Schema(description = "班级名")
    private String name;

    @Schema(description = "班级代码")
    private String code;

    @Schema(description = "负责人ID")
    private String principalUserId;

    @Schema(description = "负责人姓名")
    private String principalName;

    @Schema(description = "班级描述")
    private String description;

    @Schema(description = "班级颜色")
    private String color;

    @Schema(description = "字体颜色")
    private String textColor;

    @Schema(description = "启用")
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
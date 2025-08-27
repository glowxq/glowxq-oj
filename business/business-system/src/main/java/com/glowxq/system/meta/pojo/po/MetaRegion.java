package com.glowxq.system.meta.pojo.po;

import com.glowxq.core.common.entity.base.BaseEntity;
import com.glowxq.mysql.EntityChangeListener;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.time.LocalDateTime;

/**
 * <p>
 * 区域地址
 * </p>
 *
 * @author glowxq
 * @since 2025-06-20
 */
@Data
@Table(value = "meta_region", onInsert = EntityChangeListener.class, onUpdate = EntityChangeListener.class)
@Schema(description = "区域地址")
public class MetaRegion implements BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Schema(description = "地址编码")
    private Long id;

    @Schema(description = "父编码")
    private Long parentId;

    @Schema(description = "祖级列表")
    private String ancestors;

    @Schema(description = "地址名")
    private String name;

    @Schema(description = "地址拼音")
    private String pinyin;

    @Schema(description = "拼音前缀")
    private String pinyinPrefix;

    @Schema(description = "地址等级")
    private Integer level;

    @Schema(description = "启用")
    private Boolean enable;

    @Column(isLogicDelete = true)
    @Schema(description = "逻辑删除")
    private String delFlag;

    @Schema(description = "创建人")
    private Long createId;

    @Schema(description = "更新人")
    private Long updateId;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "租户ID")
    private String tenantId;
}
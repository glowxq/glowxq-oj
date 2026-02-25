package com.glowxq.system.meta.pojo.po;

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
* 分类
* </p>
*
* @author glowxq
* @since 2025-04-23
*/
@Data
@Table(value = "meta_category", onInsert = EntityChangeListener.class, onUpdate = EntityChangeListener.class)
@Schema(description = "分类")
public class MetaCategory implements BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    @Schema(description ="分类ID")
    private Long id;

    @Schema(description ="分类名称")
    private String name;

    @Schema(description ="父级ID")
    private Long pid;

    @Schema(description = "层级")
    private Long deep;

    @Schema(description = "排序")
    private Long sort;

    @Schema(description ="启用")
    private Boolean enable;

    @Schema(description ="是否有子级")
    private Boolean hasChildren;

    @Schema(description ="是否锁定")
    private Boolean lock;


    @Schema(description ="删除标识")
    private String delFlag;

    @Schema(description ="备注")
    private String remark;

    @Schema(description ="创建人ID")
    private Long createId;

    @Schema(description ="更新人ID")
    private Long updateId;

    @Schema(description ="创建时间")
    private LocalDateTime createTime;

    @Schema(description ="更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "租户ID")
    private String tenantId;
}
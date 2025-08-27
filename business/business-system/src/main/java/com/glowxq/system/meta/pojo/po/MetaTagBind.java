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
 * 绑定标签权限
 * </p>
 *
 * @author glowxq
 * @since 2025-04-23
 */
@Data
@Table(value = "meta_tag_bind", onInsert = EntityChangeListener.class, onUpdate = EntityChangeListener.class)
@Schema(description = "绑定标签权限")
public class MetaTagBind implements BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    @Schema(description = "")
    private Long id;

    @Schema(description = "标签ID")
    private Long tagId;

    @Schema(description = "标签绑定的业务ID")
    private Long businessId;

    @Schema(description = "绑定类型")
    private String type;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "租户ID")
    private String tenantId;

    @Schema(description = "是否删除")
    private String delFlag;

    @Schema(description = "创建人ID")
    private Long createId;

    @Schema(description = "更新人ID")
    private Long updateId;
}
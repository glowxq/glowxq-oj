package com.glowxq.oj.group.pojo.po;

import com.glowxq.core.common.entity.base.BaseEntity;
import com.glowxq.mysql.EntityChangeListener;
import com.glowxq.oj.common.enums.OjTagBind;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.time.LocalDateTime;

/**
 * <p>
 * 班级绑定数据
 * </p>
 *
 * @author glowxq
 * @since 2025-03-28
 */
@Data
@Table(value = "group_bind", onInsert = EntityChangeListener.class, onUpdate = EntityChangeListener.class)
@Schema(description = "班级绑定数据")
public class GroupBind implements BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    @Schema(description = "")
    private Long id;

    @Schema(description = "班级ID")
    private Long groupId;

    @Schema(description = "班级绑定的业务ID")
    private Long businessId;

    /**
     * @see OjTagBind
     */
    @Schema(description = "绑定类型")
    private String type;

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

    public OjTagBind bindType() {
        return OjTagBind.matchCode(type);
    }
}
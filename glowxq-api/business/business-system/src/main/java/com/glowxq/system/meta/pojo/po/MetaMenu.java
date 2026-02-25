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
* 菜单
* </p>
*
* @author glowxq
* @since 2025-04-23
*/
@Data
@Table(value = "meta_menu", onInsert = EntityChangeListener.class, onUpdate = EntityChangeListener.class)
@Schema(description = "菜单")
public class MetaMenu implements BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    @Schema(description ="底部菜单ID")
    private Long id;

    @Schema(description ="底部菜单名称")
    private String name;

    @Schema(description ="选中图标")
    private String activeIcon;

    @Schema(description ="未选中图标")
    private String inactiveIcon;

    @Schema(description ="图标类型")
    private String iconType;

    @Schema(description ="菜单类型")
    private String type;

    @Schema(description ="菜单路径")
    private String path;

    @Schema(description ="状态")
    private Boolean enable;

    @Schema(description ="排序")
    private Integer sort;

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
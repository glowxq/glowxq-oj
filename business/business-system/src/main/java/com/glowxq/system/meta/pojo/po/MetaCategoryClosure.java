package com.glowxq.system.meta.pojo.po;

import com.glowxq.core.common.entity.base.BaseEntity;
import com.glowxq.mysql.EntityChangeListener;
import com.mybatisflex.annotation.Table;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;

/**
* <p>
* 分类祖籍关系表
* </p>
*
* @author glowxq
* @since 2025-04-24
*/
@Data
@Table(value = "meta_category_closure", onInsert = EntityChangeListener.class, onUpdate = EntityChangeListener.class)
@Schema(description = "分类祖籍关系表")
public class MetaCategoryClosure implements BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description ="祖先节点ID")
    private Long ancestorId;

    @Schema(description ="后代节点ID")
    private Long descendantId;

    @Schema(description ="祖先节点到后代节点的距离")
    private Integer depth;

}
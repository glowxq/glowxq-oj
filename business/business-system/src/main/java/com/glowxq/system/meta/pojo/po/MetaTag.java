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
* 标签
* </p>
*
* @author glowxq
* @since 2025-04-23
*/
@Data
@Table(value = "meta_tag", onInsert = EntityChangeListener.class, onUpdate = EntityChangeListener.class)
@Schema(description = "标签")
public class MetaTag implements BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    @Schema(description ="")
    private Long id;

    @Schema(description ="分类ID")
    private Long categoryId;

    @Schema(description ="标签名字")
    private String name;

    @Schema(description ="背景")
    private String backgroundColor;

    @Schema(description ="字体颜色")
    private String textColor;

    @Schema(description ="镂空样式")
    private Boolean plain;

    @Schema(description ="启用")
    private Boolean enable;

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
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
* 图片
* </p>
*
* @author glowxq
* @since 2025-04-25
*/
@Data
@Table(value = "meta_image", onInsert = EntityChangeListener.class, onUpdate = EntityChangeListener.class)
@Schema(description = "图片")
public class MetaImage implements BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    @Schema(description ="")
    private Long id;

    @Schema(description ="图片名字")
    private String name;

    @Schema(description ="图片key")
    private String imageKey;

    @Schema(description ="业务类型")
    private String businessType;

    @Schema(description ="图片URL")
    private String url;

    @Schema(description ="图片介绍")
    private String content;

    @Schema(description ="跳转链接")
    private String skipUrl;

    @Schema(description ="排序(降序)")
    private Integer sort;

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
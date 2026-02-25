package com.glowxq.system.meta.pojo.po;

import com.glowxq.core.common.entity.base.BaseEntity;
import com.glowxq.mysql.EntityChangeListener;
import com.glowxq.system.meta.enums.TextBusinessType;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.time.LocalDateTime;

/**
* <p>
* 文本
* </p>
*
* @author glowxq
* @since 2025-04-23
*/
@Data
@Table(value = "meta_text", onInsert = EntityChangeListener.class, onUpdate = EntityChangeListener.class)
@Schema(description = "文本")
public class MetaText implements BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    @Schema(description ="")
    private Long id;

    @Schema(description ="text名字")
    private String name;

    @Schema(description ="文本key")
    private String textKey;

    @Schema(description ="文本类型")
    private String textType;

    @Schema(description ="图标")
    private String icon;

    /**
     * 业务类型
     * @see TextBusinessType
     */
    @Schema(description ="业务类型")
    private String businessType;



    @Schema(description ="文本标题")
    private String title;

    @Schema(description ="跳转URL")
    private String skipUrl;

    @Schema(description ="文本内容")
    private String content;

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
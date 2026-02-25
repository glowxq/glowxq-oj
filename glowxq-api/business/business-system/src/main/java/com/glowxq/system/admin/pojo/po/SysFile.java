package com.glowxq.system.admin.pojo.po;

import com.fasterxml.jackson.annotation.JsonProperty;
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
 * 文件表
 * </p>
 *
 * @author glowxq-admin
 * @since 2024-11-25
 */
@Data
@Table(value = "sys_file", onInsert = EntityChangeListener.class, onUpdate = EntityChangeListener.class)
@Schema(description = "文件表")
public class SysFile implements BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    @Schema(description = "文件ID")
    private Long id;

    @Schema(description = "文件名")
    private String filename;

    @Schema(description = "目录标识")
    private String dirTag;

    @Schema(description = "文件大小")
    private Long size;

    @Schema(description = "文件域名")
    private String url;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "对象名（唯一）")
    private String objectName;

    @Schema(description = "文件类型")
    private String contextType;

    @JsonProperty(value = "eTag")
    @Schema(description = "eTag标识")
    private String eTag;

    @Schema(description = "创建人")
    private Long createId;

}

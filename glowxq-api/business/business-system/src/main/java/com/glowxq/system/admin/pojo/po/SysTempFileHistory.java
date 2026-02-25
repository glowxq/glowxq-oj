package com.glowxq.system.admin.pojo.po;

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
 * 模版文件历史
 * </p>
 *
 * @author glowxq-admin
 * @since 2024-12-05
 */
@Data
@Table(value = "sys_temp_file_history", onInsert = EntityChangeListener.class, onUpdate = EntityChangeListener.class)
@Schema(description = "模版文件历史")
public class SysTempFileHistory implements BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    @Schema(description = "ID")
    private Long id;

    @Schema(description = "模版文件ID")
    private Integer sysTempFileId;

    @Schema(description = "文件ID")
    private Integer sysFileId;

    @Schema(description = "模版名")
    private String tempName;

    @Schema(description = "地址")
    private String url;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建人")
    private Long createId;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新人")
    private Long updateId;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "租户ID")
    private String tenantId;

}
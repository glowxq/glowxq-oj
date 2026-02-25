package com.glowxq.oj.code.pojo.po;

import com.glowxq.core.common.entity.base.BaseEntity;
import com.glowxq.mysql.EntityChangeListener;
import com.glowxq.oj.code.enums.CodeMode;
import com.glowxq.oj.code.enums.CodeMonitorStatus;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.time.LocalDateTime;

/**
 * <p>
 * 代码监控
 * </p>
 *
 * @author glowxq
 * @since 2025-04-04
 */
@Data
@Table(value = "code_monitor", onInsert = EntityChangeListener.class, onUpdate = EntityChangeListener.class)
@Schema(description = "代码监控")
public class CodeMonitor implements BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    @Schema(description = "")
    private Long id;

    @Schema(description = "被监控用户id")
    private Long monitorUserId;

    @Schema(description = "覆盖用户id")
    private Long overlayUserId;

    @Schema(description = "被监控人电话")
    private String monitorPhone;

    @Schema(description = "覆盖人电话")
    private String overlayPhone;

    @Schema(description = "被监控人")
    private String monitorName;

    @Schema(description = "覆盖人")
    private String overlayName;

    @Schema(description = "被监控代码")
    private String monitorCode;

    @Schema(description = "覆盖代码")
    private String overlayCode;

    @Schema(description = "代码模式")
    private String codeMode;

    @Schema(description = "监控状态")
    private String monitorStatus;

    @Schema(description = "版本")
    private Integer version;

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

    public CodeMode codeMode() {
        return CodeMode.matchCode(codeMode);
    }

    public CodeMonitorStatus monitorStatus() {
        return CodeMonitorStatus.matchCode(monitorStatus);
    }

    public void plusVersion() {
        this.version++;
    }
}
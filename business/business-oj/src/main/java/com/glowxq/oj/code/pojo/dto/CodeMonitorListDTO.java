package com.glowxq.oj.code.pojo.dto;

import com.glowxq.core.common.entity.PageQuery;
import com.glowxq.core.common.entity.base.BaseDTO;
import com.glowxq.core.util.BeanCopyUtils;
import com.glowxq.oj.code.pojo.po.CodeMonitor;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * <p>
 * CodeMonitor查询DTO
 * </p>
 *
 * @author glowxq
 * @since 2025-04-04
 */
@Data
@Schema(description = "CodeMonitor查询DTO")
public class CodeMonitorListDTO extends PageQuery implements BaseDTO {

    @Schema(description = "搜索关键字")
    private String searchKey;

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



    @Override
    public CodeMonitor buildEntity() {
        return BeanCopyUtils.copy(this, CodeMonitor.class);
    }
}
package com.glowxq.oj.code.pojo.dto;

import cn.idev.excel.annotation.ExcelProperty;
import com.glowxq.core.common.entity.base.BaseDTO;
import com.glowxq.core.util.BeanCopyUtils;
import com.glowxq.oj.code.pojo.po.CodeMonitor;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * <p>
 * CodeMonitor导入DTO
 * </p>
 *
 * @author glowxq
 * @since 2025-04-04
 */
@Data
@Schema(description = "CodeMonitor导入DTO")
public class CodeMonitorImportDTO implements BaseDTO {

    @ExcelProperty(value = "被监控用户id")
    @Schema(description =  "被监控用户id")
    private Long monitorUserId;

    @ExcelProperty(value = "覆盖用户id")
    @Schema(description =  "覆盖用户id")
    private Long overlayUserId;

    @ExcelProperty(value = "被监控人电话")
    @Schema(description =  "被监控人电话")
    private String monitorPhone;

    @ExcelProperty(value = "覆盖人电话")
    @Schema(description =  "覆盖人电话")
    private String overlayPhone;

    @ExcelProperty(value = "被监控人")
    @Schema(description =  "被监控人")
    private String monitorName;

    @ExcelProperty(value = "覆盖人")
    @Schema(description =  "覆盖人")
    private String overlayName;

    @ExcelProperty(value = "被监控代码")
    @Schema(description =  "被监控代码")
    private String monitorCode;

    @ExcelProperty(value = "覆盖代码")
    @Schema(description =  "覆盖代码")
    private String overlayCode;

    @ExcelProperty(value = "代码模式")
    @Schema(description =  "代码模式")
    private String codeMode;

    @ExcelProperty(value = "监控状态")
    @Schema(description =  "监控状态")
    private String monitorStatus;

    @ExcelProperty(value = "版本")
    @Schema(description =  "版本")
    private Integer version;

    @Override
    public CodeMonitor buildEntity() {
        return BeanCopyUtils.copy(this, CodeMonitor.class);
    }
}
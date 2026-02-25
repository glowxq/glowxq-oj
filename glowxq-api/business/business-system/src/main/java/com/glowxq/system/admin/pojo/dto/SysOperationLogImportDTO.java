package com.glowxq.system.admin.pojo.dto;

import cn.idev.excel.annotation.ExcelProperty;
import com.glowxq.core.common.entity.base.BaseDTO;
import com.glowxq.core.util.BeanCopyUtils;
import com.glowxq.system.admin.pojo.po.SysOperationLog;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
/**
 * <p>
 * SysOperationLog导入DTO
 * </p>
 *
 * @author glowxq
 * @since 2025-06-15
 */
@Data
@Schema(description = "SysOperationLog导入DTO")
public class SysOperationLogImportDTO implements BaseDTO {

    @ExcelProperty(value = "用户id")
    @Schema(description =  "用户id")
    private Long userId;

    @ExcelProperty(value = "链路追踪ID")
    @Schema(description =  "链路追踪ID")
    private String traceId;

    @ExcelProperty(value = "业务模块跟踪ID")
    @Schema(description =  "业务模块跟踪ID")
    private String spanId;

    @ExcelProperty(value = "用户名称")
    @Schema(description =  "用户名称")
    private String username;

    @ExcelProperty(value = "ip")
    @Schema(description =  "ip")
    private String ip;

    @ExcelProperty(value = "方法")
    @Schema(description =  "方法")
    private String method;

    @ExcelProperty(value = "接口")
    @Schema(description =  "接口")
    private String uri;

    @ExcelProperty(value = "请求头")
    @Schema(description =  "请求头")
    private String header;

    @ExcelProperty(value = "模块名称")
    @Schema(description =  "模块名称")
    private String module;

    @ExcelProperty(value = "操作描述")
    @Schema(description =  "操作描述")
    private String description;

    @ExcelProperty(value = "参数")
    @Schema(description =  "参数")
    private String param;

    @ExcelProperty(value = "请求参数json")
    @Schema(description =  "请求参数json")
    private String request;

    @ExcelProperty(value = "响应参数json")
    @Schema(description =  "响应参数json")
    private String response;

    @ExcelProperty(value = "存在错误")
    @Schema(description =  "存在错误")
    private Boolean error;

    @ExcelProperty(value = "操作类型")
    @Schema(description =  "操作类型")
    private String businessType;

    @ExcelProperty(value = "错误信息")
    @Schema(description =  "错误信息")
    private String errorMessage;

    @ExcelProperty(value = "操作耗时（ms）")
    @Schema(description =  "操作耗时（ms）")
    private Long costTime;

    @ExcelProperty(value = "租户id")
    @Schema(description =  "租户id")
    private String tenantId;

    @Override
    public SysOperationLog buildEntity() {
        return BeanCopyUtils.copy(this, SysOperationLog.class);
    }
}
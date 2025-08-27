package com.glowxq.system.admin.pojo.dto;

import com.glowxq.core.common.entity.PageQuery;
import com.glowxq.core.common.entity.base.BaseDTO;
import com.glowxq.core.util.BeanCopyUtils;
import com.glowxq.system.admin.pojo.po.SysOperationLog;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
/**
 * <p>
 * SysOperationLog查询DTO
 * </p>
 *
 * @author glowxq
 * @since 2025-06-15
 */
@Data
@Schema(description = "SysOperationLog查询DTO")
public class SysOperationLogListDTO extends PageQuery implements BaseDTO {

    @Schema(description =  "用户id")
    private Long userId;

    @Schema(description =  "链路追踪ID")
    private String traceId;

    @Schema(description =  "业务模块跟踪ID")
    private String spanId;

    @Schema(description =  "用户名称")
    private String username;

    @Schema(description =  "ip")
    private String ip;

    @Schema(description =  "方法")
    private String method;

    @Schema(description =  "接口")
    private String uri;

    @Schema(description =  "请求头")
    private String header;

    @Schema(description =  "模块名称")
    private String module;

    @Schema(description =  "操作描述")
    private String description;

    @Schema(description =  "参数")
    private String param;

    @Schema(description =  "请求参数json")
    private String request;

    @Schema(description =  "响应参数json")
    private String response;

    @Schema(description =  "存在错误")
    private Boolean error;

    @Schema(description =  "操作类型")
    private String businessType;

    @Schema(description =  "错误信息")
    private String errorMessage;

    @Schema(description =  "操作耗时（ms）")
    private Long costTime;

    @Schema(description =  "租户id")
    private String tenantId;

    @Override
    public SysOperationLog buildEntity() {
        return BeanCopyUtils.copy(this, SysOperationLog.class);
    }
}
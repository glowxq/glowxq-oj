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
* 操作日志
* </p>
*
* @author glowxq
* @since 2025-06-15
*/
@Data
@Table(value = "sys_operation_log", onInsert = EntityChangeListener.class, onUpdate = EntityChangeListener.class)
@Schema(description = "操作日志")
public class SysOperationLog implements BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    @Schema(description ="主键ID")
    private Long id;

    @Schema(description ="用户id")
    private Long userId;

    @Schema(description ="链路追踪ID")
    private String traceId;

    @Schema(description ="业务模块跟踪ID")
    private String spanId;

    @Schema(description ="用户名称")
    private String username;

    @Schema(description ="ip")
    private String ip;

    @Schema(description ="方法")
    private String method;

    @Schema(description ="接口")
    private String uri;

    @Schema(description ="请求头")
    private String header;

    @Schema(description ="模块名称")
    private String module;

    @Schema(description ="操作描述")
    private String description;

    @Schema(description ="参数")
    private String param;

    @Schema(description ="请求参数json")
    private String request;

    @Schema(description ="响应参数json")
    private String response;

    @Schema(description ="存在错误")
    private Boolean error;

    @Schema(description ="操作类型")
    private String businessType;

    @Schema(description ="错误信息")
    private String errorMessage;

    @Schema(description ="操作耗时（ms）")
    private Long costTime;

    @Schema(description ="创建时间")
    private LocalDateTime createTime;

    @Schema(description ="更新时间")
    private LocalDateTime updateTime;


    @Schema(description ="是否删除")
    private String delFlag;

    @Schema(description ="租户id")
    private String tenantId;

    @Schema(description ="创建人ID")
    private Long createId;

    @Schema(description ="更新人ID")
    private Long updateId;

}
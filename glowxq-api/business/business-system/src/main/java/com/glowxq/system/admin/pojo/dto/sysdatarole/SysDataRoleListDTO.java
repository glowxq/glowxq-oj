package com.glowxq.system.admin.pojo.dto.sysdatarole;

import com.glowxq.core.common.entity.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * <p>
 * SysDataRole查询DTO
 * </p>
 *
 * @author glowxq-admin
 * @since 2024-07-09
 */
@Data
@Schema(description = "SysDataRole查询DTO")
public class SysDataRoleListDTO extends PageQuery {

    @Schema(description = "角色名称")
    private String roleName;

    @Schema(description = "是否锁定")
    private String isLock;

}
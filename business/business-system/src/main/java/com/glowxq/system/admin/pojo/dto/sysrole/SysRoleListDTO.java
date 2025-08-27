package com.glowxq.system.admin.pojo.dto.sysrole;

import com.glowxq.core.common.entity.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author glowxq
 * @since 2023/8/24 15:28
 */
@Data
@Schema(description = "角色查询")
public class SysRoleListDTO extends PageQuery {

    @Schema(description = "角色名称")
    private String roleName;

    @Schema(description = "数据权限")
    private String dataScope;


}

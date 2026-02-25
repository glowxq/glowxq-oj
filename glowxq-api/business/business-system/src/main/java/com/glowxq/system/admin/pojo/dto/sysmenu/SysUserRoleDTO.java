package com.glowxq.system.admin.pojo.dto.sysmenu;

import com.glowxq.core.common.valid.annotation.NotZero;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author glowxq
 * @since 2023/8/28 15:49
 */
@Data
@Schema(description = "用户角色变更")
@NoArgsConstructor
@AllArgsConstructor
public class SysUserRoleDTO {

    @NotZero(message = "用户id不能为空")
    @Schema(description = "用户id", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long userId;

    @Schema(description = "角色id数组")
    private List<Long> roleIds;

    public SysUserRoleDTO(Long userId, Long... roleIds) {
        this.userId = userId;
        this.roleIds = List.of(roleIds);
    }
}

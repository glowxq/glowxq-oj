package com.glowxq.system.admin.pojo.dto.sysuser;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * SysUserPasswordDTO
 *
 * @author glowxq
 * @since 2023/8/30
 */
@Data
public class SysUserPasswordDTO {

    @Schema(description = "原始密码")
    private String oldPwd;

    @Schema(description = "新密码")
    private String newPwd;
}

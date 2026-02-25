package com.glowxq.security.pojo;

import com.glowxq.core.common.enums.MenuType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author glowxq
 * @since 2024/1/22 15:35
 * @version 1.0
 */
@Data
@Schema(description = "通用用户返回")
public class LoginVO {

    @Schema(description = "userId")
    private Long userId;

    @Schema(description = "access_token")
    private String accessToken;

    @Schema(description = "授权令牌 access_token 的有效期")
    private Long expireIn;

    @Schema(description = "用户信息")
    private Object userInfo;

    @Schema(description = "租户ID")
    private String tenantId;

    @Schema(description = "菜单类型")
    private MenuType menuType = MenuType.Admin;

}

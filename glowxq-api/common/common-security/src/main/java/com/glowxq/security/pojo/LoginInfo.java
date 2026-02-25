package com.glowxq.security.pojo;

import com.glowxq.core.common.valid.annotation.InEnum;
import com.glowxq.security.enums.GrantType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 登录信息
 *
 * @author glowxq
 * @version 1.0
 * @since 2024/1/22 9:38
 */
@Data
public class LoginInfo {

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "密码")
    private String password;

    @Schema(description = "客户端id", requiredMode = Schema.RequiredMode.REQUIRED)
    private String clientId;

    @Schema(description = "授权类型", requiredMode = Schema.RequiredMode.REQUIRED)
    @InEnum(enumClass = GrantType.class)
    private String grantType;

    @Schema(description = "一键登录code")
    private String code;

    @Schema(description = "微信小程序-自动注册")
    private Boolean autoRegister;

    public GrantType grantType() {
        return GrantType.matchCode(grantType);
    }
}

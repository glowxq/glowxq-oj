package com.glowxq.core.common.entity;

import com.glowxq.core.common.enums.DataScopeType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * BaseUserInfo - 用于表示用户信息的类。
 *
 * @author glowxq
 * @version 1.0
 * @since 2023-12-12
 */
@Data
public class BaseUserInfo {

    @Schema(description = "用户id")
    private Long id;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "姓名")
    private String name;

    @Schema(description = "昵称")
    private String nickname;

    @Schema(description = "小程序用户的唯一标识")
    private String openid;

    @Schema(description = "业务code")
    private String code;

    @Schema(description = "绑定code")
    private String bindCode;

    @Schema(description = "链接")
    private String url;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "LOGO")
    private String logo;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "数据权限")
    private String dataScope;

    @Schema(description = "租户ID")
    private String tenantId;

    public DataScopeType dataScope() {
        return DataScopeType.matchCode(dataScope);
    }
}

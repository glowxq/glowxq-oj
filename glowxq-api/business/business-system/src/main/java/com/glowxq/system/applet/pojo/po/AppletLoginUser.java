package com.glowxq.system.applet.pojo.po;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * MiniLoginUser
 *
 * @author glowxq
 * @version 1.0
 * @since 2024/4/28 14:15
 */
@Data
public class AppletLoginUser {

    @Schema(description = "用户ID(applet_user表主键)")
    private Long id;

    @Schema(description = "用户ID(sys_user表主键)")
    private Long sysUserId;

    @Schema(description = "小程序openid")
    private String openid;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "昵称")
    private String nickname;

    @Schema(description = "手机号,手机号为空则未绑定")
    private String phone;

    @Schema(description = "LOGO")
    private String logo;

    @Schema(description = "邮箱，邮箱为空则未绑定")
    private String email;
}

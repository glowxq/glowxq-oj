package com.glowxq.system.applet.pojo.dto;

import com.glowxq.core.common.entity.base.BaseDTO;
import com.glowxq.core.util.BeanCopyUtils;
import com.glowxq.core.util.StringUtils;
import com.glowxq.system.applet.pojo.po.AppletUser;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @author glowxq
 * @version 1.0
 * @date 2025/4/25
 */
@Data
public class RegisterDTO implements BaseDTO {

    @Schema(description = "昵称")
    private String nickname;

    @Schema(description = "微信登录code")
    @NotBlank(message = "微信登录code不能为空")
    private String loginCode;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "小程序手机号快速认证组件 https://developers.weixin.qq.com/miniprogram/dev/OpenApiDoc/user-info/phone-number/getPhoneNumber.html")
    private String phoneCode;

    @Schema(description = "账号如果不填默认取手机号")
    private String username;

    @Schema(description = "密码 不填默认为123456")
    private String password;

    @Schema(description = "绑定分销码")
    private String bindCode;

    @Schema(description = "地址")
    private String address;

    @Schema(description = "真实姓名")
    private String name;

    @Schema(description = "头像地址")
    private String logo;

    @Override
    public AppletUser buildEntity() {
        return BeanCopyUtils.copy(this, AppletUser.class);
    }

    public String autoUsername() {
        return StringUtils.defaultIfBlank(username, phone);
    }

    public String autoPassword() {
        return StringUtils.defaultIfBlank(password, "123456");
    }
}

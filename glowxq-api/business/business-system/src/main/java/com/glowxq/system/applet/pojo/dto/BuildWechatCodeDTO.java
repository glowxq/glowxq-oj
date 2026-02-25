package com.glowxq.system.applet.pojo.dto;

import com.glowxq.system.applet.enums.WechatEnvironment;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @author glowxq
 * @version 1.0
 * @date 2025/4/25
 */
@Data
public class BuildWechatCodeDTO {

    @Schema(description = "昵称")
    private String page;

    @Schema(description = "微信登录code")
    @NotBlank(message = "微信登录code不能为空")
    private WechatEnvironment environment;

    @Schema(description = "参数 例如 code=xxx")
    private String scene;
}

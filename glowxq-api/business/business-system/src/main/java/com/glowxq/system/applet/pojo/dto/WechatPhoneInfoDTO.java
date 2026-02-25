package com.glowxq.system.applet.pojo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class WechatPhoneInfoDTO {

    @NotBlank
    private String phoneCode;
}

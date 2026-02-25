package com.glowxq.wechat.applet.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 微信access_token返回结果
 * 
 * @author glowxq
 * @since 2024/4/26 11:12
 * @version 1.0
 */
@Data
public class AccessTokenResult extends ErrorMessage {

    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("expires_in")
    private Integer expiresIn;

}

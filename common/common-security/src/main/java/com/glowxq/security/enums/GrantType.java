package com.glowxq.security.enums;

import com.glowxq.core.common.enums.base.BaseType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@AllArgsConstructor
public enum GrantType implements BaseType {

    password("password", "密码模式"),
    sms("sms", "短信模式"),
    applet("applet", "小程序模式"),
    third("third", "第三方模式"),
    ;

    private final String code;

    private final String name;

    public static GrantType matchCode(String grantType) {
        for (GrantType type : GrantType.values()) {
            if (type.getCode().equals(grantType)) {
                return type;
            }
        }
        return null;
    }
}

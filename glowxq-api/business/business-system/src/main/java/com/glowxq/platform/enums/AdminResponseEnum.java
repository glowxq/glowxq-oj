package com.glowxq.platform.enums;

import com.glowxq.core.common.enums.ErrorPrefixEnum;
import com.glowxq.core.common.exception.common.BusinessExceptionCustomAssert;

/**
 * 异常枚举类 适用于Admin模块的
 */
public enum AdminResponseEnum implements BusinessExceptionCustomAssert {

    // @formatter:off
    MENU_NAME_EXISTS(1001, "Menu路由名称已存在"),
    ;
    // @formatter:on

    /**
     * 返回码
     */
    private Integer code;

    /**
     * 返回消息
     */
    private String message;

    AdminResponseEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * 自定义断言，支持提供错误码和消息
     *
     * @param code    自定义错误码
     * @param message 自定义错误消息
     * @return 当前枚举常量
     */
    public AdminResponseEnum message(Integer code, String message) {
        this.setCode(code);
        this.setMessage(message);
        return this;
    }

    @Override
    public Integer getCode() {
        return this.code;
    }

    private void setCode(Integer code) {
        this.code = code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    private void setMessage(String message) {
        this.message = message;
    }

    @Override
    public ErrorPrefixEnum getCodePrefixEnum() {
        return ErrorPrefixEnum.ADMIN;
    }
}

package com.glowxq.core.common.exception.common;

import com.glowxq.core.common.enums.CommonResponseEnum;
import lombok.Getter;

import java.io.Serial;

@Getter
public class BaseException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    private final IResponseEnum responseEnum;

    private final Object[] args;

    public BaseException(IResponseEnum responseEnum, Object[] args, String message) {
        super(message);
        this.responseEnum = responseEnum;
        this.args = args;
    }

    public BaseException(IResponseEnum responseEnum, Object[] args, String message, Throwable cause) {
        super(message, cause);
        this.responseEnum = responseEnum;
        this.args = args;
    }

    public BaseException(IResponseEnum responseEnum,  String message, Throwable cause) {
        super(message, cause);
        this.responseEnum = responseEnum;
        this.args = null;
    }

    public BaseException(String message, Throwable cause) {
        super(message, cause);
        this.responseEnum = CommonResponseEnum.UNKNOWN;
        this.args = null;
    }

}

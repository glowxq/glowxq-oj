package com.glowxq.core.common.exception.common;

import com.glowxq.core.common.enums.CommonResponseEnum;

import java.io.Serial;

public class BusinessException extends BaseException {

    @Serial
    private static final long serialVersionUID = 1L;

    public BusinessException(String message) {
        super(CommonResponseEnum.BUSINESS_ERROR, null, message);
    }

    public BusinessException(IResponseEnum responseEnum, String message) {
        super(responseEnum, null, message);
    }

    public BusinessException(IResponseEnum responseEnum, Object[] args, String message) {
        super(responseEnum, args, message);
    }

    public BusinessException(IResponseEnum responseEnum, Object[] args, String message, Throwable cause) {
        super(responseEnum, args, message, cause);
    }
}

package com.glowxq.core.common.exception.common;

import com.glowxq.core.common.enums.CommonResponseEnum;

import java.io.IOException;
import java.io.Serial;

public class AlertsException extends BaseException {

    @Serial
    private static final long serialVersionUID = 1L;

    public AlertsException(String message) {
        super(CommonResponseEnum.ALERTS_ERROR, null, message);
    }

    public AlertsException(IResponseEnum responseEnum, Object[] args, String message) {
        super(responseEnum, args, message);
    }

    public AlertsException(IResponseEnum responseEnum, Object[] args, String message, Throwable cause) {
        super(responseEnum, args, message, cause);
    }

    public AlertsException(Exception e) {
        super(e.getMessage(), e);
    }

    public AlertsException(CommonResponseEnum commonResponseEnum, Exception e) {
        super(commonResponseEnum, e.getMessage(), e);
    }

    public AlertsException(String message, Exception e) {
        super(message, e);
    }
}

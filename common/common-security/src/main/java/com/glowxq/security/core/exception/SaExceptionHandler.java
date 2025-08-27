package com.glowxq.security.core.exception;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import com.glowxq.core.common.entity.ApiResult;
import com.glowxq.core.common.enums.CommonResponseEnum;
import com.glowxq.core.common.exception.BaseExceptionHandle;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author glowxq
 * @since 2024/2/4 15:42
 */
@Order(Integer.MIN_VALUE)
@Slf4j
@RestControllerAdvice
public class SaExceptionHandler extends BaseExceptionHandle {

    @ExceptionHandler(NotLoginException.class)
    public ApiResult<Void> handlerNotLoginException(NotLoginException e, HttpServletRequest request, HttpServletResponse response) {
        super.packErrorLog(request, response, e);
        String message;
        if (e.getType().equals(NotLoginException.NOT_TOKEN)) {
            message = "未能读取到有效 token";
        }
        else if (e.getType().equals(NotLoginException.INVALID_TOKEN) || e.getType().equals(NotLoginException.TOKEN_FREEZE)) {
            message = "您的登录信息已过期，请重新登录以继续访问。";
            // [ do something ...] websocket close
            // sendWsClose();
        }
        else if (e.getType().equals(NotLoginException.TOKEN_TIMEOUT)) {
            message = "token 已过期";
        }
        else if (e.getType().equals(NotLoginException.BE_REPLACED)) {
            message = "token 已被顶下线";
        }
        else if (e.getType().equals(NotLoginException.KICK_OUT)) {
            message = "token 已被踢下线";
        }
        else if (e.getType().equals(NotLoginException.NO_PREFIX)) {
            message = "未按照指定前缀提交 token";
        }
        else {
            message = "当前会话未登录";
        }
        log.error("[SaExceptionHandler] {} error:{}", message, e.getMessage(), e);
        return new ApiResult<>(CommonResponseEnum.INVALID_TOKEN.getCodePrefixEnum().getPrefix() + CommonResponseEnum.INVALID_TOKEN.getCode(), message);
    }

    @ExceptionHandler(NotPermissionException.class)
    public ApiResult<Void> handlerNotPermissionException(NotPermissionException e) {
        return ApiResult.error(CommonResponseEnum.INVALID_PERMISSION);
    }
}

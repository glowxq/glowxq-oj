package com.glowxq.oj.common;

import com.glowxq.core.common.entity.ApiResult;
import com.glowxq.core.common.enums.CommonResponseEnum;
import com.glowxq.core.common.exception.BaseExceptionHandle;
import com.glowxq.core.common.feishu.utils.FeishuMessageUtils;
import com.glowxq.oj.judge.processor.exception.CompileException;
import com.glowxq.oj.judge.processor.exception.SubmitException;
import com.glowxq.oj.judge.processor.exception.SystemException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常捕获处理
 *
 * @author glowxq
 * @since 2022/8/23 10:56
 */
@RestControllerAdvice
@Slf4j
public class OJExceptionHandler extends BaseExceptionHandle {

    @ExceptionHandler(SystemException.class)
    public ResponseEntity<ApiResult<Object>> submitExceptionHandler(SystemException e, HttpServletRequest request, HttpServletResponse response) {
        String message = super.packErrorLog(request, response, e);
        FeishuMessageUtils.sendInternalMessage(message);
        ApiResult<Object> error = ApiResult.error(CommonResponseEnum.UNKNOWN);
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(SubmitException.class)
    public ResponseEntity<ApiResult<Object>> submitExceptionHandler(SubmitException e, HttpServletRequest request, HttpServletResponse response) {
        String message = super.packErrorLog(request, response, e);
        FeishuMessageUtils.sendInternalMessage(message);
        ApiResult<Object> error = ApiResult.error(CommonResponseEnum.UNKNOWN);
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(CompileException.class)
    public ResponseEntity<ApiResult<Object>> compileExceptionHandler(CompileException e, HttpServletRequest request, HttpServletResponse response) {
        String message = super.packErrorLog(request, response, e);
        FeishuMessageUtils.sendInternalMessage(message);
        ApiResult<Object> error = ApiResult.error(CommonResponseEnum.UNKNOWN);
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

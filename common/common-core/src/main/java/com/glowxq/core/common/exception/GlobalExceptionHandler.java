package com.glowxq.core.common.exception;

import com.glowxq.core.common.entity.ApiResult;
import com.glowxq.core.common.enums.CommonResponseEnum;
import com.glowxq.core.common.exception.common.AlertsException;
import com.glowxq.core.common.exception.common.BaseException;
import com.glowxq.core.common.exception.common.BusinessException;
import com.glowxq.core.common.feishu.utils.FeishuMessageUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常捕获处理
 *
 * @author glowxq
 * @since 2022/8/23 10:56
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends BaseExceptionHandle {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResult<Object>> exceptionHandler(Exception e, HttpServletRequest request, HttpServletResponse response) {
        String message = super.packErrorLog(request, response, e);
        FeishuMessageUtils.sendInternalMessage(message);
        ApiResult<Object> error = ApiResult.error(CommonResponseEnum.UNKNOWN);
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * 参数绑定异常
     *
     * @param e 参数绑定异常
     * @return 异常结果
     */
    @ExceptionHandler(value = RuntimeException.class)
    public ResponseEntity<ApiResult<Object>> handleRuntimeException(RuntimeException e, HttpServletRequest request, HttpServletResponse response) {
        String message = super.packErrorLog(request, response, e);
        FeishuMessageUtils.sendInternalMessage(message);
        ApiResult<Object> error = ApiResult.error(CommonResponseEnum.UNKNOWN);
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * 业务异常
     *
     * @param e 异常
     * @return 异常结果
     */
    @ExceptionHandler(value = BusinessException.class)
    @ResponseBody
    public ApiResult<Void> handleBusinessException(BusinessException e, HttpServletRequest request, HttpServletResponse response) {
        String message = super.packErrorLog(request, response, e);
        return new ApiResult<>(getCode(e), e.getMessage());
    }

    /**
     * 自定义异常
     *
     * @param e 异常
     * @return 异常结果
     */
    @ExceptionHandler(value = BaseException.class)
    @ResponseBody
    public ApiResult<Void> handleBaseException(BaseException e, HttpServletRequest request, HttpServletResponse response) {
        String message = super.packErrorLog(request, response, e);
        FeishuMessageUtils.sendInternalMessage(message);
        return new ApiResult<>(getCode(e), e.getMessage());
    }

    /**
     * 自定义异常
     *
     * @param e 异常
     * @return 异常结果
     */
    @ExceptionHandler(value = AlertsException.class)
    @ResponseBody
    public ApiResult<Void> handleAlertsException(AlertsException e, HttpServletRequest request, HttpServletResponse response) {
        String message = super.packErrorLog(request, response, e);
        FeishuMessageUtils.sendInternalMessage(message);
        return new ApiResult<>(getCode(e), e.getMessage());
    }

    /**
     * 参数校验异常
     *
     * @param e 参数校验异常
     * @return 异常结果
     */
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResult<Object>> handleValidException(MethodArgumentNotValidException e, HttpServletRequest request, HttpServletResponse response) {
        String message = super.packErrorLog(request, response, e);
        FeishuMessageUtils.sendInternalMessage(message);
        ApiResult<Object> apiResult = wrapperBindingResult(e);
        return new ResponseEntity<>(apiResult, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    /**
     * 参数绑定异常
     *
     * @param e 参数绑定异常
     * @return 异常结果
     */
    @ExceptionHandler(value = BindException.class)
    public ResponseEntity<ApiResult<Object>> handleBindException(BindException e, HttpServletRequest request, HttpServletResponse response) {
        String message = super.packErrorLog(request, response, e);
        FeishuMessageUtils.sendInternalMessage(message);
        ApiResult<Object> apiResult = wrapperBindingResult(e);
        return new ResponseEntity<>(apiResult, HttpStatus.UNPROCESSABLE_ENTITY);
    }
}

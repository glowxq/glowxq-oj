package com.glowxq.excel.core;

import cn.idev.excel.exception.ExcelAnalysisException;
import com.glowxq.core.common.entity.ApiResult;
import com.glowxq.core.common.enums.CommonResponseEnum;
import com.glowxq.core.common.exception.BaseExceptionHandle;
import com.glowxq.core.common.feishu.utils.FeishuMessageUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author glowxq
 * @since 2024/12/27 16:21
 */
@Order(Integer.MIN_VALUE)
@RestControllerAdvice
public class ExcelExceptionHandler extends BaseExceptionHandle {

    @ExceptionHandler(value = ExcelAnalysisException.class)
    public ApiResult<Object> handlerNotPermissionException(ExcelAnalysisException e, HttpServletRequest request, HttpServletResponse response) {
        String msg = super.packErrorLog(request, response, e);
        FeishuMessageUtils.sendInternalMessage(msg);

        CommonResponseEnum message = CommonResponseEnum.EXCEL_IMPORT_ERROR.message(e.getMessage());
        return ApiResult.error(message);
    }

}

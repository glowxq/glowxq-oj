package com.glowxq.core.common.filter;

import com.glowxq.core.common.filter.trace.TraceLogContext;
import com.glowxq.core.common.filter.wrapper.RepeatedlyRequestWrapper;
import com.glowxq.core.util.ServletUtils;
import com.glowxq.core.util.StringUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.util.Map;

/**
 * @author glowxq
 * @version 1.0
 * @date 2024/10/9
 */
@Slf4j
public class RRLogFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String spanId = TraceLogContext.getSpanId();
        log.info("filter 层面增加过滤器 spanId:{}", spanId);
        // 异步请求不记录日志
        boolean asyncReq = isAsyncDispatch(request) || isAsyncStarted(request);
        String contentType = request.getContentType();
        if (asyncReq || StringUtils.isBlank(contentType) || !StringUtils.startsWithIgnoreCase(contentType, MediaType.APPLICATION_JSON_VALUE)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 包装请求响应
        RepeatedlyRequestWrapper repeatableRequestWrapper = ServletUtils.toRepeatableRequestWrapper(request);
        ContentCachingResponseWrapper wrapperResponse = ServletUtils.toContentCachingResponseWrapper(response);

        try {
            filterChain.doFilter(repeatableRequestWrapper, wrapperResponse);
            this.logRequestTrace(repeatableRequestWrapper, wrapperResponse);
        } finally {
            wrapperResponse.copyBodyToResponse();
            TraceLogContext.clear();
        }
    }

    /**
     * 日志
     *
     * @param wrapperRequest  wrapper 请求
     * @param wrapperResponse wrapper 响应
     */
    protected void logRequestTrace(RepeatedlyRequestWrapper wrapperRequest, ContentCachingResponseWrapper wrapperResponse) throws IOException {
        log.info("Request >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        String ip = ServletUtils.getClientIP(wrapperRequest);
        log.info("Request-IP:{}", ip);
        String uri = wrapperRequest.getRequestURI();
        log.info("Request-URL:{}", uri);
        String method = wrapperRequest.getMethod();
        log.info("Request-Method:{}", method);
        Map<String, String> param = ServletUtils.getParamMap(wrapperRequest);
        log.info("Request-Param:{}", param);
        Map<String, String> headers = ServletUtils.getHeaders(wrapperRequest);
        log.info("Request-Header:{}", headers);
        String requestBody = wrapperRequest.getRepeatedlyRequestBody();
        log.info("Request-Body:{}", requestBody);
        log.info("Request <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");

        log.info("Response >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        String responseBody = ServletUtils.getResponseBody(wrapperResponse);
        log.info("Response-Body:{}", responseBody);
        log.info("Response <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
    }
}

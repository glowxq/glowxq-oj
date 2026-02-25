package com.glowxq.tenant.interceptor;

import com.glowxq.tenant.constants.TenantConstants;
import com.glowxq.tenant.utils.TenantUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * 租户拦截器 - 处理多租户HTTP请求
 * <p>
 * 该拦截器负责从HTTP请求中提取租户ID，并将其设置到TenantContext中，
 * 以便后续的业务逻辑可以获取到当前请求的租户信息。
 * 在请求完成后，清理租户上下文，防止内存泄漏。
 * </p>
 *
 * @author glowxq
 * @version 1.0
 * @date 2025/6/5
 */
@Slf4j
public class TenantInterceptor implements HandlerInterceptor {

    /**
     * 请求前处理 - 从请求中提取并设置租户ID
     * <p>
     * 在Controller方法执行前被调用，负责：
     * 1. 从HTTP请求中提取租户ID
     * 2. 将租户ID设置到TenantContext中
     * </p>
     *
     * @param request 当前HTTP请求
     * @param response HTTP响应
     * @param handler 请求处理器
     * @return true表示继续处理请求，false表示中断请求处理
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info("租户拦截器处理请求: {}", request.getRequestURI());

        // 通过 request 去获取租户 ID
        String tenantId = getTenantIdByRequest(request);
        log.info("从请求中提取的租户ID: {}", tenantId);

        // 设置租户ID到上下文中
        TenantUtils.setTenantId(tenantId);
        log.info("已将租户ID: {} 设置到TenantContext中", tenantId);

        return true; // 继续处理请求
    }

    /**
     * 请求处理后调用 - 在视图渲染前执行
     * <p>
     * 在Controller方法执行后、视图渲染前被调用
     * 目前不执行任何操作，保留方法以符合HandlerInterceptor接口
     * </p>
     *
     * @param request 当前HTTP请求
     * @param response HTTP响应
     * @param handler 请求处理器
     * @param modelAndView 模型和视图对象，可能为null
     */
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable ModelAndView modelAndView) throws Exception {
        log.info("租户拦截器postHandle: {}", request.getRequestURI());
    }

    /**
     * 请求完成后调用 - 清理租户上下文
     * <p>
     * 在整个请求结束后被调用，负责清理TenantContext，防止内存泄漏
     * 无论请求是否有异常，都会执行此方法
     * </p>
     *
     * @param request 当前HTTP请求
     * @param response HTTP响应
     * @param handler 请求处理器
     * @param ex 处理过程中抛出的异常，如果没有则为null
     */
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {
        log.info("租户拦截器完成请求: {}, 清理租户上下文", request.getRequestURI());
        if (ex != null) {
            log.info("请求处理过程中发生异常: {}", ex.getMessage());
        }

        // 清理租户上下文，防止内存泄漏
        TenantUtils.remove();
        log.info("租户上下文已清理");
    }

    /**
     * 从HTTP请求中提取租户ID
     * <p>
     * 按照以下优先级从请求中提取租户ID：
     * 1. 首先从请求头中获取
     * 2. 如果请求头中没有，则从请求参数中获取
     * </p>
     *
     * @param request HTTP请求对象
     * @return 提取到的租户ID，如果未找到则返回null
     */
    private String getTenantIdByRequest(HttpServletRequest request) {
        // 从请求头中获取租户ID
        String header = request.getHeader(TenantConstants.TENANT_ID_HEADER);
        log.info("从请求头[{}]中获取租户ID: {}", TenantConstants.TENANT_ID_HEADER, header);

        // 从请求参数中获取租户ID
        String parameter = request.getParameter(TenantConstants.TENANT_ID_FILED);
        log.info("从请求参数[{}]中获取租户ID: {}", TenantConstants.TENANT_ID_FILED, parameter);

        // 优先使用请求头中的值，如果为空则使用参数中的值
        String tenantId = StringUtils.defaultIfBlank(header, parameter);
        log.info("最终确定的租户ID: {}", tenantId);

        return tenantId;
    }
}
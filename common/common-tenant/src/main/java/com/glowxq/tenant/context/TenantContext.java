package com.glowxq.tenant.context;

import com.alibaba.ttl.TransmittableThreadLocal;
import lombok.extern.slf4j.Slf4j;

/**
 * 租户上下文 - 用于存储和获取当前线程的租户信息
 * <p>
 * 使用TransmittableThreadLocal确保在线程池环境中租户信息可以正确传递
 *
 * @author glowxq
 * @version 1.0
 * @date 2025/6/5
 */
@Slf4j
public class TenantContext {

    /**
     * 存储当前线程租户ID的ThreadLocal变量
     * 使用TransmittableThreadLocal确保在异步调用中能正确传递租户信息
     */
    protected static final TransmittableThreadLocal<String> TenantLocal = new TransmittableThreadLocal<>();

    /**
     * 获取当前线程的租户ID
     *
     * @return 当前租户ID，如未设置则返回null
     */
    public static String getTenantId() {
        String tenantId = TenantLocal.get();
        log.debug("从租户上下文获取租户ID: {}", tenantId);
        return tenantId;
    }

    /**
     * 设置当前线程的租户ID
     *
     * @param tenantId 租户ID
     */
    public static void setTenantId(String tenantId) {
        log.debug("设置租户上下文的租户ID: {}", tenantId);
        TenantLocal.set(tenantId);
    }

    /**
     * 清除当前线程的租户信息
     * 在请求结束时应当调用此方法，防止内存泄漏
     */
    public static void remove() {
        log.debug("清除租户上下文");
        TenantLocal.remove();
    }
}

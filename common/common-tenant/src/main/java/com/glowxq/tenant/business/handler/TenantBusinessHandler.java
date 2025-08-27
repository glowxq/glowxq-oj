package com.glowxq.tenant.business.handler;

import com.glowxq.tenant.business.pojo.po.Tenant;

/**
 * @author glowxq
 * @version 1.0
 * @date 2025/6/6
 */
public interface TenantBusinessHandler {

    /**
     * 创建新租户后需要处理的事情
     *
     * @param tenant
     */
    void newTenantBusiness(Tenant tenant);
}

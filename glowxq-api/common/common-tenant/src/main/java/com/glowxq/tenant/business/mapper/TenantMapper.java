package com.glowxq.tenant.business.mapper;

import com.glowxq.tenant.business.pojo.po.Tenant;
import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.query.QueryWrapper;

/**
* <p>
* 租户 Mapper 接口
* </p>
*
* @author glowxq
* @since 2025-06-06
*/
public interface TenantMapper extends BaseMapper<Tenant> {

    /**
     * 根据租户ID获取租户信息
     *
     * @param tenantId 租户ID
     * @return 租户信息，如不存在则返回null
     */
    default Tenant getByTenantId(String tenantId) {
        QueryWrapper qw = QueryWrapper.create();
        qw.eq(Tenant::getTenantId, tenantId);
        return selectOneByQuery(qw);
    }

    /**
     * 根据租户编码获取租户信息
     *
     * @param tenantCode 租户编码
     * @return 租户信息，如不存在则返回null
     */
    default Tenant getByTenantCode(String tenantCode) {
        QueryWrapper qw = QueryWrapper.create();
        qw.eq(Tenant::getTenantCode, tenantCode);
        return selectOneByQuery(qw);
    }

    /**
     * 根据自定义域名获取租户信息
     *
     * @param customDomain 自定义域名
     * @return 租户信息，如不存在则返回null
     */
    default Tenant getByCustomDomain(String customDomain) {
        QueryWrapper qw = QueryWrapper.create();
        qw.eq(Tenant::getCustomDomain, customDomain);
        return selectOneByQuery(qw);
    }

    default Tenant getByContactPhone(String contactPhone) {
        QueryWrapper qw = QueryWrapper.create();
        qw.eq(Tenant::getContactPhone, contactPhone);
        return selectOneByQuery(qw);
    }

    default Tenant getByTenantKey(String tenantKey) {
        Tenant tenant = this.getByTenantId(tenantKey);
        if (tenant == null) {
            tenant = this.getByTenantCode(tenantKey);
        }
        if (tenant == null) {
            tenant = this.getByCustomDomain(tenantKey);
        }
        if (tenant == null) {
            tenant = this.getByContactPhone(tenantKey);
        }
        return tenant;
    }
}
package com.glowxq.tenant.business.service;

import com.glowxq.core.common.entity.PageResult;
import com.glowxq.core.common.entity.SelectIdsDTO;
import com.glowxq.tenant.business.pojo.dto.TenantCreateDTO;
import com.glowxq.tenant.business.pojo.dto.TenantListDTO;
import com.glowxq.tenant.business.pojo.dto.TenantUpdateDTO;
import com.glowxq.tenant.business.pojo.po.Tenant;
import com.glowxq.tenant.business.pojo.vo.TenantVO;
import com.mybatisflex.core.service.IService;

import java.util.List;

/**
 * <p>
 * 租户 Service
 * </p>
 *
 * @author glowxq
 * @since 2025-06-06
 */
public interface TenantService extends IService<Tenant> {

    Tenant checkTenantThrow(String tenantKey);

    Tenant create(TenantCreateDTO dto);

    void update(TenantUpdateDTO dto);

    PageResult<TenantVO> page(TenantListDTO dto);

    List<TenantVO> list(TenantListDTO dto);

    void remove(SelectIdsDTO dto);

    TenantVO getByTenantKey(Long id);

    TenantVO getByTenantKey(String tenantKey);

    Tenant getByTenantId(String tenantId);
}
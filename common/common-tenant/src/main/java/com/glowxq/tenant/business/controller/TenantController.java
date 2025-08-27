package com.glowxq.tenant.business.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaIgnore;
import com.glowxq.core.common.annotation.OperationLog;
import com.glowxq.core.common.api.BaseApi;
import com.glowxq.core.common.entity.ApiPageResult;
import com.glowxq.core.common.entity.ApiResult;
import com.glowxq.core.common.entity.PageResult;
import com.glowxq.core.common.entity.SelectIdsDTO;
import com.glowxq.core.common.enums.BusinessHandlerType;
import com.glowxq.core.common.enums.ModuleEnum;
import com.glowxq.tenant.business.handler.TenantBusinessHandler;
import com.glowxq.tenant.business.pojo.dto.TenantCreateDTO;
import com.glowxq.tenant.business.pojo.dto.TenantListDTO;
import com.glowxq.tenant.business.pojo.dto.TenantUpdateDTO;
import com.glowxq.tenant.business.pojo.po.Tenant;
import com.glowxq.tenant.business.pojo.vo.TenantConfigVO;
import com.glowxq.tenant.business.pojo.vo.TenantVO;
import com.glowxq.tenant.business.service.TenantService;
import com.glowxq.tenant.config.TenantConfig;
import com.glowxq.tenant.utils.TenantUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

/**
 * tenant/租户 Api
 *
 * @author glowxq
 * @since 2025-06-06
 */
@Tag(name = "租户")
@Slf4j
@RestController
@RequestMapping
@RequiredArgsConstructor
public class TenantController extends BaseApi {

    private final TenantService tenantService;

    private final TenantBusinessHandler tenantBusinessHandler;

    private final  TenantConfig tenantConfig;

    @SaIgnore
    @Operation(summary = "查询租户功能是否启用")
    @GetMapping("/tenant/enable/status")
    public ApiResult<TenantConfigVO> enableStatus() {
        TenantConfigVO tenantConfigVO = new TenantConfigVO(tenantConfig.getEnable(), tenantConfig.getIgnoreTable());
        return ApiResult.success(tenantConfigVO);
    }

    @Operation(summary = "新增")
    @SaCheckPermission(value = "tenant.create")
    @Transactional(rollbackFor = Exception.class)
    @OperationLog(desc = "租户新增", module = ModuleEnum.Tenant, handleType = BusinessHandlerType.Create)
    @PostMapping("/tenant/create")
    public ApiResult<Void> create(@RequestBody TenantCreateDTO dto) {
        Tenant tenant = tenantService.create(dto);
        TenantUtils.changeTenant(tenant.getTenantId(), () -> {
            log.info("租户创建成功: {}, ID: {}, 租户ID: {}", tenant.getTenantName(), tenant.getId(), tenant.getTenantId());
            tenantBusinessHandler.newTenantBusiness(tenant);
            log.info("租户业务处理完毕");
            return null;
        });
        return ApiResult.success();
    }

    @Operation(summary = "修改")
    @SaCheckPermission(value = "tenant.update")
    @OperationLog(desc = "租户修改", module = ModuleEnum.Tenant, handleType = BusinessHandlerType.Update)
    @PutMapping("/tenant/update")
    public ApiResult<Void> update(@RequestBody TenantUpdateDTO dto) {
        tenantService.update(dto);
        return ApiResult.success();
    }

    @Operation(summary = "删除")
    @SaCheckPermission(value = "tenant.remove")
    @OperationLog(desc = "租户删除", module = ModuleEnum.Tenant, handleType = BusinessHandlerType.Delete)
    @DeleteMapping("/tenant/remove")
    public ApiResult<Void> remove(@RequestBody SelectIdsDTO dto) {
        tenantService.remove(dto);
        return ApiResult.success();
    }

    @Operation(summary = "列表查询")
    @SaCheckPermission(value = "tenant.query_table")
    @OperationLog(desc = "租户列表查询", module = ModuleEnum.Tenant, handleType = BusinessHandlerType.Query)
    @GetMapping("/tenant/list")
    public ApiResult<PageResult<TenantVO>> list(TenantListDTO dto) {
        return ApiPageResult.success(tenantService.page(dto));
    }

    @Operation(summary = "详情")
    @SaCheckPermission(value = "tenant.query_table")
    @OperationLog(desc = "租户详情查询", module = ModuleEnum.Tenant, handleType = BusinessHandlerType.Query)
    @GetMapping("/tenant/detail")
    public ApiResult<TenantVO> detail(@RequestParam Long id) {
        return ApiResult.success(tenantService.getByTenantKey(id));
    }
}
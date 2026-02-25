package com.glowxq.tenant.business.api;

import cn.dev33.satoken.annotation.SaIgnore;
import com.glowxq.core.common.api.BaseApi;
import com.glowxq.core.common.entity.ApiPageResult;
import com.glowxq.core.common.entity.ApiResult;
import com.glowxq.core.common.entity.PageResult;
import com.glowxq.core.util.BeanCopyUtils;
import com.glowxq.tenant.business.pojo.dto.ClientTenantUpdateDTO;
import com.glowxq.tenant.business.pojo.dto.TenantListDTO;
import com.glowxq.tenant.business.pojo.dto.TenantUpdateDTO;
import com.glowxq.tenant.business.pojo.vo.TenantConfigVO;
import com.glowxq.tenant.business.pojo.vo.TenantVO;
import com.glowxq.tenant.business.service.TenantService;
import com.glowxq.tenant.config.TenantConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * tenant/租户 Api
 *
 * @author glowxq
 * @since 2025-06-06
 */
@Tag(name = "租户")
@SaIgnore
@RestController
@RequestMapping("/client")
@RequiredArgsConstructor
public class TenantApi extends BaseApi {

    private final TenantService tenantService;

    @Autowired
    private TenantConfig tenantConfig;

    @Operation(summary = "查询租户功能是否启用")
    @PostMapping("/tenant/enableStatus")
    public ApiResult<TenantConfigVO> enableStatus() {
        TenantConfigVO tenantConfigVO = new TenantConfigVO(tenantConfig.getEnable(), tenantConfig.getIgnoreTable());
        return ApiResult.success(tenantConfigVO);
    }

    @Operation(summary = "修改")
    @PutMapping("/tenant/update")
    public ApiResult<Void> update(@RequestBody ClientTenantUpdateDTO dto) {
        TenantUpdateDTO updateDTO = BeanCopyUtils.copy(dto, TenantUpdateDTO.class);
        tenantService.update(updateDTO);
        return ApiResult.success();
    }

    @Operation(summary = "列表查询")
    @GetMapping("/tenant/list")
    public ApiResult<PageResult<TenantVO>> list() {
        TenantListDTO dto = new TenantListDTO();
        dto.setShow(Boolean.TRUE);
        dto.setEnable(Boolean.TRUE);
        return ApiPageResult.success(tenantService.list(dto));
    }

    @Operation(summary = "详情")
    @GetMapping("/tenant/getByTenantKey")
    public ApiResult<TenantVO> getByTenantKey(@RequestParam String tenantKey) {
        return ApiResult.success(tenantService.getByTenantKey(tenantKey));
    }
}
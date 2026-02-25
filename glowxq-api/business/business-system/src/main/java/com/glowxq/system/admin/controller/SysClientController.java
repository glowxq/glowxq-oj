package com.glowxq.system.admin.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.glowxq.core.common.annotation.OperationLog;
import com.glowxq.core.common.constant.GlobalConstant;
import com.glowxq.core.common.entity.ApiPageResult;
import com.glowxq.core.common.entity.ApiResult;
import com.glowxq.core.common.entity.PageResult;
import com.glowxq.core.common.entity.SelectIdsDTO;
import com.glowxq.core.common.enums.ModuleEnum;
import com.glowxq.security.pojo.ClientVO;
import com.glowxq.system.admin.pojo.dto.sysclient.SysClientCreateDTO;
import com.glowxq.system.admin.pojo.dto.sysclient.SysClientListDTO;
import com.glowxq.system.admin.pojo.dto.sysclient.SysClientUpdateDTO;
import com.glowxq.system.admin.pojo.vo.sysclient.SysClientVO;
import com.glowxq.system.admin.service.SysClientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 系统授权表 Controller
 * </p>
 *
 * @author glowxq
 * @since 2024-01-22
 */
@Tag(name = "系统授权表")
@RestController
@RequestMapping("sys-client")
@RequiredArgsConstructor
public class SysClientController {

    private final SysClientService sysClientService;

    @Operation(summary = "新增系统授权")
    @SaCheckPermission(value = "sys.client.create", orRole = GlobalConstant.SUPER_ROLE)
    @PostMapping
    @OperationLog(module = ModuleEnum.System)
    public ApiResult<Void> create(@RequestBody SysClientCreateDTO dto) {
        sysClientService.create(dto);
        return ApiResult.success();
    }

    @Operation(summary = "修改系统授权")
    @SaCheckPermission(value = "sys.client.update", orRole = GlobalConstant.SUPER_ROLE)
    @PutMapping
    @OperationLog(module = ModuleEnum.System)
    public ApiResult<Void> update(@RequestBody SysClientUpdateDTO dto) {
        sysClientService.update(dto);
        return ApiResult.success();
    }

    @Operation(summary = "删除系统授权")
    @SaCheckPermission(value = "sys.client.remove", orRole = GlobalConstant.SUPER_ROLE)
    @DeleteMapping
    @OperationLog(module = ModuleEnum.System)
    public ApiResult<Void> remove(@RequestBody SelectIdsDTO dto) {
        sysClientService.remove(dto);
        return ApiResult.success();
    }

    @Operation(summary = "列表查询")
    @SaCheckPermission(value = "sys.client.query_table", orRole = GlobalConstant.SUPER_ROLE)
    @GetMapping
    public ApiResult<PageResult<SysClientVO>> list(SysClientListDTO dto) {
        return ApiPageResult.success(sysClientService.page(dto));
    }

    @Operation(summary = "详情")
    @SaCheckPermission(value = "sys.client.query_table", orRole = GlobalConstant.SUPER_ROLE)
    @GetMapping("/{id}")
    public ApiResult<ClientVO> detail(@PathVariable Object id) {
        return ApiResult.success(sysClientService.detail(id));
    }

}

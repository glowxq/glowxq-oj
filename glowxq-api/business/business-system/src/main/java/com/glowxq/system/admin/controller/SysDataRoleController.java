package com.glowxq.system.admin.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.glowxq.core.common.annotation.OperationLog;
import com.glowxq.core.common.constant.GlobalConstant;
import com.glowxq.core.common.entity.ApiPageResult;
import com.glowxq.core.common.entity.ApiResult;
import com.glowxq.core.common.entity.PageResult;
import com.glowxq.core.common.entity.SelectIdsDTO;
import com.glowxq.core.common.enums.ModuleEnum;
import com.glowxq.system.admin.pojo.dto.sysdatarole.SysDataRoleCreateDTO;
import com.glowxq.system.admin.pojo.dto.sysdatarole.SysDataRoleListDTO;
import com.glowxq.system.admin.pojo.dto.sysdatarole.SysDataRoleUpdateDTO;
import com.glowxq.system.admin.pojo.vo.sysdatarole.SysDataRoleMenuVO;
import com.glowxq.system.admin.pojo.vo.sysdatarole.SysDataRoleVO;
import com.glowxq.system.admin.service.SysDataRoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 数据权限管理 Controller
 * </p>
 *
 * @author glowxq-admin
 * @since 2024-07-09
 */
@Tag(name = "数据权限管理")
@RestController
@RequestMapping("sys-data-role")
@RequiredArgsConstructor
public class SysDataRoleController {

    private final SysDataRoleService sysDataRoleService;

    @Operation(summary = "新增数据权限管理")
    @SaCheckPermission(value = "sys.data.role.create", orRole = GlobalConstant.SUPER_ROLE)
    @PostMapping
    @OperationLog(module = ModuleEnum.System)
    public ApiResult<Void> create(@RequestBody SysDataRoleCreateDTO dto) {
        sysDataRoleService.create(dto);
        return ApiResult.success();
    }

    @Operation(summary = "修改数据权限管理")
    @SaCheckPermission(value = "sys.data.role.update", orRole = GlobalConstant.SUPER_ROLE)
    @PutMapping
    @OperationLog(module = ModuleEnum.System)
    public ApiResult<Void> update(@RequestBody SysDataRoleUpdateDTO dto) {
        sysDataRoleService.update(dto);
        return ApiResult.success();
    }

    @Operation(summary = "删除数据权限管理")
    @SaCheckPermission(value = "sys.data.role.remove", orRole = GlobalConstant.SUPER_ROLE)
    @DeleteMapping
    @OperationLog(module = ModuleEnum.System)
    public ApiResult<Void> remove(@RequestBody SelectIdsDTO dto) {
        sysDataRoleService.remove(dto);
        return ApiResult.success();
    }

    @Operation(summary = "列表查询")
    @SaCheckPermission(value = "sys.data.role.query_table", orRole = GlobalConstant.SUPER_ROLE)
    @GetMapping
    public ApiResult<PageResult<SysDataRoleVO>> list(SysDataRoleListDTO dto) {
        return ApiPageResult.success(sysDataRoleService.page(dto));
    }

    @Operation(summary = "详情")
    @SaCheckPermission(value = "sys.data.role.query_table", orRole = GlobalConstant.SUPER_ROLE)
    @GetMapping("/{id}")
    public ApiResult<SysDataRoleVO> detail(@PathVariable Object id) {
        return ApiResult.success(sysDataRoleService.detail(id));
    }

    @Operation(summary = "数据权限角色菜单信息查询")
    @SaCheckPermission(value = "sys.data.role.query_table", orRole = GlobalConstant.SUPER_ROLE)
    @GetMapping("/menu")
    public ApiResult<SysDataRoleMenuVO> findRoleMenuByRoleId() {
        return ApiResult.success(sysDataRoleService.queryDataRoleMenu());
    }

}

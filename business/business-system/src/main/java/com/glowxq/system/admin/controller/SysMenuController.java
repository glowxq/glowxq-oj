package com.glowxq.system.admin.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.stp.StpUtil;
import com.glowxq.core.common.annotation.DebounceIgnore;
import com.glowxq.core.common.annotation.OperationLog;
import com.glowxq.core.common.constant.GlobalConstant;
import com.glowxq.core.common.entity.ApiResult;
import com.glowxq.core.common.entity.SelectIdsDTO;
import com.glowxq.core.common.enums.ModuleEnum;
import com.glowxq.core.common.valid.annotation.InEnum;
import com.glowxq.system.admin.enums.MenuMode;
import com.glowxq.system.admin.pojo.dto.sysmenu.GenButtonDTO;
import com.glowxq.system.admin.pojo.dto.sysmenu.MenuPermissionDTO;
import com.glowxq.system.admin.pojo.dto.sysmenu.SysMenuCreateDTO;
import com.glowxq.system.admin.pojo.dto.sysmenu.SysMenuListDTO;
import com.glowxq.system.admin.pojo.po.SysMenu;
import com.glowxq.system.admin.pojo.vo.sysmenu.MenuPermissionVO;
import com.glowxq.system.admin.pojo.vo.sysmenu.MenuTreeVO;
import com.glowxq.system.admin.pojo.vo.sysmenu.SysMenuVO;
import com.glowxq.system.admin.service.SysMenuService;
import com.glowxq.system.admin.service.SysPermissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * <p>
 * 系统菜单表 前端控制器
 * </p>
 *
 * @author glowxq
 * @since 2022-10-01
 */

@Tag(name = "系统菜单管理")
@RequiredArgsConstructor
@RestController
@RequestMapping("/sys-menu")
public class SysMenuController {

    private final SysMenuService sysMenuService;

    private final SysPermissionService sysPermissionService;

    @Operation(summary = "添加菜单")
    @SaCheckPermission(value = "sys.menu.create_btn", orRole = GlobalConstant.SUPER_ROLE)
    @PostMapping
    @OperationLog(module = ModuleEnum.System)
    public ApiResult<Void> create(@Valid @RequestBody SysMenuCreateDTO dto) {
        sysMenuService.create(dto);
        return ApiResult.success();
    }

    @Operation(summary = "修改菜单")
    @SaCheckPermission(value = "sys.menu.update_btn", orRole = GlobalConstant.SUPER_ROLE)
    @PutMapping
    @OperationLog(module = ModuleEnum.System)
    public ApiResult<Void> update(@Valid @RequestBody SysMenuCreateDTO dto) {
        sysMenuService.update(dto);
        return ApiResult.success();
    }

    @Operation(summary = "批量删除")
    @SaCheckPermission(value = "sys.menu.delete_btn", orRole = GlobalConstant.SUPER_ROLE)
    @DeleteMapping
    @OperationLog(module = ModuleEnum.System)
    public ApiResult<Void> remove(@RequestBody SelectIdsDTO dto) {
        sysMenuService.remove(dto);
        return ApiResult.success();
    }

    @Operation(summary = "列表查询")
    @SaCheckPermission(value = "sys.menu.query_table", orRole = GlobalConstant.SUPER_ROLE)
    @GetMapping
    public ApiResult<List<SysMenuVO>> list(SysMenuListDTO dto) {
        return ApiResult.success(sysMenuService.menuList(dto));
    }

    @Operation(summary = "详情")
    @SaCheckPermission(value = "sys.menu.query_table", orRole = GlobalConstant.SUPER_ROLE)
    @GetMapping("{id}")
    public ApiResult<SysMenu> detail(@PathVariable String id) {
        return ApiResult.success(sysMenuService.detail(id));
    }

    @Operation(summary = "查询上级菜单")
    @GetMapping("tree")
    public ApiResult<List<MenuTreeVO>> queryParentListTree(@RequestParam(required = false) String nodeId) {
        return ApiResult.success(sysMenuService.getSimpleMenuTree(nodeId));
    }

    @DebounceIgnore
    @Operation(summary = "查询用户具有的菜单")
    @GetMapping("/menu")
    public ApiResult<List<SysMenuVO>> queryMenuByUserId(@RequestHeader(defaultValue = "Common",name = "Menutype") @InEnum(enumClass = MenuMode.class) String menuType) {
        Long userId = StpUtil.getLoginIdAsLong();
        List<SysMenuVO> menuList = sysMenuService.findMenuListByUserId(userId, menuType);
        return ApiResult.success(menuList);
    }

    @Operation(summary = "查询菜单按钮权限是否存在")
    @GetMapping("/btn/exists")
    public ApiResult<MenuPermissionVO> findBtnPermission(MenuPermissionDTO dto) {
        return ApiResult.success(sysMenuService.hasExistsPermissions(dto));
    }

    @DebounceIgnore
    @Operation(summary = "查询全部按钮权限")
    @GetMapping("/btn/permissions")
    public ApiResult<List<String>> findBtnPermission() {
        return ApiResult.success(sysMenuService.findPermission());
    }

    @Operation(summary = "导出sql")
    @SaCheckPermission(value = "sys.menu.sql_btn", orRole = GlobalConstant.SUPER_ROLE)
    @PostMapping("/sql/export")
    @OperationLog(module = ModuleEnum.System)
    public ApiResult<String> exportMenuSql(@RequestBody SelectIdsDTO dto) {
        return ApiResult.success(sysMenuService.exportMenuSql(dto));
    }

    @Operation(summary = "生成权限按钮")
    @SaCheckPermission(value = "sys.menu.gen_btn", orRole = GlobalConstant.SUPER_ROLE)
    @PostMapping("/sql/genButtons")
    public ApiResult<String> genButtons(@RequestBody GenButtonDTO dto) {
        sysMenuService.genButtons(dto);
        return ApiResult.success();
    }

    @DebounceIgnore
    @Operation(summary = "查询用户角色", description = "如果用户是超级管理员（user_tag_cd='1001002'），输出 'admin'；否则输出用户的角色id")
    @GetMapping("/user/roles")
    public ApiResult<List<String>> findUserRoles() {
        Set<String> roles = sysPermissionService.getRoles(StpUtil.getLoginIdAsLong());
        return ApiResult.success(new ArrayList<>(roles));
    }

    @Operation(summary = "修改数据权限状态")
    @SaCheckPermission(value = "sys.menu.update_btn", orRole = GlobalConstant.SUPER_ROLE)
    @PutMapping("/datarole/change/{id}")
    @OperationLog(module = ModuleEnum.System)
    public ApiResult<Void> changeDataRole(@PathVariable String id) {
        sysMenuService.changeMenuDataScope(id);
        return ApiResult.success();
    }
}

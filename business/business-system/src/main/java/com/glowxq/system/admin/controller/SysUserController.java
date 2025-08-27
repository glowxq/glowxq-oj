package com.glowxq.system.admin.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import com.glowxq.core.common.annotation.OperationLog;
import com.glowxq.core.common.constant.GlobalConstant;
import com.glowxq.core.common.entity.ApiPageResult;
import com.glowxq.core.common.entity.ApiResult;
import com.glowxq.core.common.entity.PageResult;
import com.glowxq.core.common.entity.SelectIdsDTO;
import com.glowxq.core.common.enums.ModuleEnum;
import com.glowxq.core.common.valid.annotation.NotZero;
import com.glowxq.core.datascope.DataScopeHandle;
import com.glowxq.redis.WebsocketRedisService;
import com.glowxq.system.admin.pojo.dto.SearchUserDTO;
import com.glowxq.system.admin.pojo.dto.sysmenu.SysUserRoleDTO;
import com.glowxq.system.admin.pojo.dto.sysuser.*;
import com.glowxq.system.admin.pojo.po.SysUser;
import com.glowxq.system.admin.pojo.vo.SearchUserVO;
import com.glowxq.system.admin.pojo.vo.sysdept.DeptTreeVO;
import com.glowxq.system.admin.pojo.vo.sysuser.SysUserRoleVO;
import com.glowxq.system.admin.pojo.vo.sysuser.SysUserVO;
import com.glowxq.system.admin.pojo.vo.sysuser.UserOptionVO;
import com.glowxq.system.admin.service.SysDeptService;
import com.glowxq.system.admin.service.SysUserDataRoleService;
import com.glowxq.system.admin.service.SysUserService;
import com.glowxq.tenant.utils.TenantUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 系统用户表 前端控制器
 * </p>
 *
 * @author glowxq
 * @since 2022-10-01
 */
@Tag(name = "用户管理")
@RestController
@RequestMapping("/sys-user")
@RequiredArgsConstructor
public class SysUserController {

    private final SysUserService sysUserService;

    private final SysDeptService sysDeptService;

    private final WebsocketRedisService websocketRedisService;

    private final SysUserDataRoleService sysUserDataRoleService;

    @Operation(summary = "添加用户")
    @SaCheckPermission(value = "sys.user.create_btn", orRole = GlobalConstant.SUPER_ROLE)
    @PostMapping
    @OperationLog(module = ModuleEnum.System)
    public ApiResult<Void> create(@Valid @RequestBody SysUserCreateDTO dto) {
        sysUserService.create(dto);
        return ApiResult.success();
    }

    @Operation(summary = "修改用户")
    @SaCheckPermission(value = "sys.user.update_btn", orRole = GlobalConstant.SUPER_ROLE)
    @PutMapping
    @OperationLog(module = ModuleEnum.System)
    public ApiResult<Void> update(@Valid @RequestBody SysUserUpdateDTO dto) {
        sysUserService.update(dto);
        return ApiResult.success();
    }

    @Operation(summary = "批量删除")
    @SaCheckPermission(value = "sys.user.delete_btn", orRole = GlobalConstant.SUPER_ROLE)
    @DeleteMapping
    @OperationLog(module = ModuleEnum.System)
    public ApiResult<Void> remove(@RequestBody SelectIdsDTO dto) {
        sysUserService.remove(dto);
        return ApiResult.success();
    }

    @Operation(summary = "根据关键词搜索用户")
    @PostMapping("/searchUserList")
    @DataScopeHandle
    public ApiPageResult<PageResult<SearchUserVO>> searchUserList(@RequestBody SearchUserDTO searchUserDto) {
        PageResult<SearchUserVO> sysUserPageResult = sysUserService.searchUserList(searchUserDto);
        return ApiPageResult.success(sysUserPageResult);
    }

    @Operation(summary = "查询分页列表")
    @SaCheckPermission(value = "sys.user.query_table", orRole = GlobalConstant.SUPER_ROLE)
    @PostMapping("/listPage")
    @DataScopeHandle
    public ApiPageResult<PageResult<SysUserVO>> listPage(@RequestBody @Valid SysUserListDTO dto) {
        dto.setTenantId(TenantUtils.getTenantId());
        return ApiPageResult.success(sysUserService.page(dto));
    }

    @Operation(summary = "用户详情")
    @SaCheckPermission(value = "sys.user.query_table", orRole = GlobalConstant.SUPER_ROLE)
    @GetMapping("{id}")
    public ApiResult<SysUserVO> detail(@PathVariable Long id) {
        SysUserVO detail = sysUserService.detail(id);
        return ApiResult.success(detail);
    }

    @Operation(summary = "用户角色信息查询-（穿梭框）")
    @SaCheckPermission(value = "sys.user.role_set_btn", orRole = GlobalConstant.SUPER_ROLE)
    @GetMapping("role")
    public ApiResult<SysUserRoleVO> findUserRole(@NotZero @RequestParam Long userId) {
        return ApiResult.success(sysUserService.findSysUserRole(userId));
    }

    @Operation(summary = "用户角色信息修改 -（穿梭框）")
    @SaCheckPermission(value = "sys.user.role_set_btn", orRole = GlobalConstant.SUPER_ROLE)
    @PutMapping("role")
    public ApiResult<Void> changeUserRole(@Valid @RequestBody SysUserRoleDTO dto) {
        sysUserService.changeSysUserRole(dto);
        return ApiResult.success();
    }

    @Operation(summary = "登录信息查询")
    @GetMapping("/userinfo")
    public ApiResult<SysUserVO> getUserInfo() {
        return ApiResult.success(sysUserService.getUserInfo());
    }

    @Operation(summary = "（个人）修改密码")
    @PutMapping("/password")
    public ApiResult<Void> changePassword(@Valid @RequestBody SysUserPasswordDTO dto) {
        sysUserService.changePassword(dto);
        return ApiResult.success();
    }

    @Operation(summary = "重置账户密码")
    @PutMapping("/reset/password/{userId}")
    public ApiResult<Void> resetPassword(@PathVariable Long userId) {
        sysUserService.resetPassword(userId);
        return ApiResult.success();
    }

    @Operation(summary = "账户解锁")
    @SaCheckPermission(value = "sys.user.unlock_btn", orRole = GlobalConstant.SUPER_ROLE)
    @PostMapping("/unlock")
    public ApiResult<Void> unlock(@RequestBody SelectIdsDTO dto) {
        sysUserService.unlock(dto);
        return ApiResult.success();
    }

    @Operation(summary = "绑定（批量）用户和部门")
    @SaCheckPermission(value = "sys.user.dept_set_btn", orRole = GlobalConstant.SUPER_ROLE)
    @PostMapping("/dept/bind")
    public ApiResult<Void> bindDept(@RequestBody UserDeptDTO dto) {
        sysUserService.bindUserDept(dto);
        return ApiResult.success();
    }

    @GetMapping("/dept/tree")
    @Operation(summary = "账户管理-部门树形列表")
    public ApiResult<List<DeptTreeVO>> tree() {
        return ApiResult.success(sysDeptService.getDepartmentTreeWithAdditionalNodes());
    }

    @Operation(summary = "用户信息-下拉列表")
    @SaCheckPermission(value = {"sys.user.query_table", "sys.dept.query_table"}, mode = SaMode.OR, orRole = GlobalConstant.SUPER_ROLE)
    @GetMapping("options")
    public ApiResult<List<UserOptionVO>> getUserOptions() {
        return ApiResult.success(sysUserService.getUserOptions());
    }

    @Operation(summary = "用户数据角色信息查询-（穿梭框）")
    @SaCheckPermission(value = "sys.user.data_role_set_btn", orRole = GlobalConstant.SUPER_ROLE)
    @GetMapping("datarole")
    public ApiResult<SysUserRoleVO> findUserDataRole(@NotZero @RequestParam Long userId) {
        return ApiResult.success(sysUserDataRoleService.queryRoleMenu(userId));
    }

    @Operation(summary = "用户数据角色信息修改 -（穿梭框）")
    @SaCheckPermission(value = "sys.user.data_role_set_btn", orRole = GlobalConstant.SUPER_ROLE)
    @PutMapping("datarole")
    public ApiResult<Void> changeDataUserRole(@Valid @RequestBody SysUserRoleDTO dto) {
        sysUserDataRoleService.changeRole(dto);
        return ApiResult.success();
    }
}

package com.glowxq.system.admin.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.glowxq.core.common.annotation.OperationLog;
import com.glowxq.core.common.constant.GlobalConstant;
import com.glowxq.core.common.entity.ApiResult;
import com.glowxq.core.common.entity.SelectIdsDTO;
import com.glowxq.core.common.enums.ModuleEnum;
import com.glowxq.system.admin.pojo.dto.sysdept.SysDeptCreateDTO;
import com.glowxq.system.admin.pojo.dto.sysdept.SysDeptListDTO;
import com.glowxq.system.admin.pojo.dto.sysdept.SysDeptUpdateDTO;
import com.glowxq.system.admin.pojo.po.SysDept;
import com.glowxq.system.admin.pojo.vo.sysdept.DeptTreeVO;
import com.glowxq.system.admin.pojo.vo.sysdept.SysDeptLeaderVO;
import com.glowxq.system.admin.pojo.vo.sysdept.SysDeptVO;
import com.glowxq.system.admin.service.SysDeptService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 部门表 Controller
 * </p>
 *
 * @author glowxq
 * @since 2024-03-20
 */
@Tag(name = "部门表")
@RestController
@RequestMapping
@RequiredArgsConstructor
public class SysDeptController {

    private final SysDeptService sysDeptService;

    @Operation(summary = "新增部门")
    @SaCheckPermission(value = "sys.dept.create", orRole = GlobalConstant.SUPER_ROLE)
    @PostMapping("/sys-dept")
    @OperationLog(module = ModuleEnum.System)
    public ApiResult<Void> create(@RequestBody SysDeptCreateDTO dto) {
        sysDeptService.create(dto);
        return ApiResult.success();
    }

    @Operation(summary = "修改部门")
    @SaCheckPermission(value = "sys.dept.update", orRole = GlobalConstant.SUPER_ROLE)
    @PutMapping("/sys-dept")
    @OperationLog(module = ModuleEnum.System)
    public ApiResult<Void> update(@RequestBody SysDeptUpdateDTO dto) {
        sysDeptService.update(dto);
        return ApiResult.success();
    }

    @Operation(summary = "删除部门")
    @SaCheckPermission(value = "sys.dept.remove", orRole = GlobalConstant.SUPER_ROLE)
    @DeleteMapping("/sys-dept")
    @OperationLog(module = ModuleEnum.System)
    public ApiResult<Void> remove(@RequestBody SelectIdsDTO dto) {
        sysDeptService.remove(dto);
        return ApiResult.success();
    }

    @Operation(summary = "列表查询")
    @SaCheckPermission(value = "sys.dept.query_table", orRole = GlobalConstant.SUPER_ROLE)
    @GetMapping("/sys-dept")
    public ApiResult<List<SysDeptVO>> list(SysDeptListDTO dto) {
        return ApiResult.success(sysDeptService.list(dto));
    }

    @Operation(summary = "详情")
    @SaCheckPermission(value = "sys.dept.query_table", orRole = GlobalConstant.SUPER_ROLE)
    @GetMapping("/sys-dept/{id}")
    public ApiResult<SysDeptVO> detail(@PathVariable Object id) {
        return ApiResult.success(sysDeptService.detail(id));
    }

    @Operation(summary = "根据关键词或地区id筛选")
    @GetMapping("/sys-dept/searchDeptList")
    public ApiResult<List<SysDept>> searchDeptList(@RequestParam(required = false) String searchKey, @RequestParam(required = false) Long regionId) {
        return ApiResult.success(sysDeptService.searchDeptList(searchKey, regionId));
    }

    @Operation(summary = "树形列表")
    @GetMapping("/sys-dept/tree")
    public ApiResult<List<DeptTreeVO>> tree(
            @Parameter(description = "需要排除的节点ID") @RequestParam(required = false) Integer excludeNodeId,
            @Parameter(description = "是否添加根节点") @RequestParam(required = false) Boolean appendRoot) {
        return ApiResult.success(sysDeptService.getDeptTree(excludeNodeId, appendRoot, false));
    }

    @Operation(summary = "负责人穿梭框-全部用户")
    @GetMapping("/sys-dept/leader")
    public ApiResult<SysDeptLeaderVO> leader() {
        return ApiResult.success(sysDeptService.findSysUserDeptLeader());
    }
}

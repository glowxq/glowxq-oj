package com.glowxq.system.admin.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.glowxq.core.common.annotation.OperationLog;
import com.glowxq.core.common.constant.GlobalConstant;
import com.glowxq.core.common.entity.ApiPageResult;
import com.glowxq.core.common.entity.ApiResult;
import com.glowxq.core.common.entity.PageResult;
import com.glowxq.core.common.entity.SelectIdsDTO;
import com.glowxq.core.common.enums.ModuleEnum;
import com.glowxq.system.admin.pojo.dto.sysconfig.SysConfigCreateDTO;
import com.glowxq.system.admin.pojo.dto.sysconfig.SysConfigListDTO;
import com.glowxq.system.admin.pojo.dto.sysconfig.SysConfigUpdateDTO;
import com.glowxq.system.admin.pojo.po.SysConfig;
import com.glowxq.system.admin.service.SysConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 参数配置表 前端控制器
 * </p>
 *
 * @author glowxq
 * @since 2023-11-23
 */
@Tag(name = "参数配置")
@RestController
@RequestMapping("/sys-config")
@RequiredArgsConstructor
public class SysConfigController {

    private final SysConfigService sysConfigService;

    @Operation(summary = "新增参数配置")
    @SaCheckPermission(value = "sys.config.add_btn", orRole = GlobalConstant.SUPER_ROLE)
    @PostMapping
    @OperationLog(module = ModuleEnum.System)
    public ApiResult<Void> create(@RequestBody SysConfigCreateDTO dto) {
        sysConfigService.create(dto);
        return ApiResult.success();
    }

    @Operation(summary = "修改参数配置")
    @SaCheckPermission(value = "sys.config.update_btn", orRole = GlobalConstant.SUPER_ROLE)
    @PutMapping
    @OperationLog(module = ModuleEnum.System)
    public ApiResult<Void> update(@RequestBody SysConfigUpdateDTO dto) {
        sysConfigService.update(dto);
        return ApiResult.success();
    }

    @Operation(summary = "删除参数配置")
    @SaCheckPermission(value = "sys.config.delete_btn", orRole = GlobalConstant.SUPER_ROLE)
    @DeleteMapping
    @OperationLog(module = ModuleEnum.System)
    public ApiResult<Void> remove(@RequestBody SelectIdsDTO dto) {
        sysConfigService.remove(dto);
        return ApiResult.success();
    }

    @Operation(summary = "列表查询")
    @SaCheckPermission(value = "sys.config.query_table", orRole = GlobalConstant.SUPER_ROLE)
    @GetMapping
    public ApiResult<PageResult<SysConfig>> list(SysConfigListDTO dto) {
        return ApiPageResult.success(sysConfigService.list(dto));
    }

    @Operation(summary = "详情")
    @SaCheckPermission(value = "sys.config.query_table", orRole = GlobalConstant.SUPER_ROLE)
    @GetMapping("/{id}")
    public ApiResult<SysConfig> detail(@PathVariable Object id) {
        return ApiResult.success(sysConfigService.detail(id));
    }

}

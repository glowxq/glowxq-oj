package com.glowxq.system.admin.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.glowxq.core.common.annotation.OperationLog;
import com.glowxq.core.common.constant.GlobalConstant;
import com.glowxq.core.common.entity.ApiPageResult;
import com.glowxq.core.common.entity.ApiResult;
import com.glowxq.core.common.entity.PageResult;
import com.glowxq.core.common.entity.SelectIdsDTO;
import com.glowxq.core.common.enums.ModuleEnum;
import com.glowxq.system.admin.pojo.dto.sysdict.SysDictTypeAddDTO;
import com.glowxq.system.admin.pojo.dto.sysdict.SysDictTypeListDTO;
import com.glowxq.system.admin.pojo.dto.sysdict.SysDictTypeUpDTO;
import com.glowxq.system.admin.pojo.po.SysDictType;
import com.glowxq.system.admin.pojo.vo.sysdict.DictTypeVO;
import com.glowxq.system.admin.service.SysDictTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 字典类型 前端控制器
 * </p>
 *
 * @author glowxq
 * @since 2023-08-18
 */
@Tag(name = "字典类型管理")
@RestController
@RequestMapping("/sys-dict-type")
@RequiredArgsConstructor
public class SysDictTypeController {

    private final SysDictTypeService sysDictTypeService;

    @Operation(summary = "字典类型新增")
    @SaCheckPermission(value = "sys.dict.add_type_btn", orRole = GlobalConstant.SUPER_ROLE)
    @PostMapping
    @OperationLog(module = ModuleEnum.System)
    public ApiResult<Void> create(@Valid @RequestBody SysDictTypeAddDTO dto) {
        sysDictTypeService.create(dto);
        return ApiResult.success();
    }

    @Operation(summary = "字典类型修改")
    @SaCheckPermission(value = "sys.dict.update_type_btn", orRole = GlobalConstant.SUPER_ROLE)
    @PutMapping
    @OperationLog(module = ModuleEnum.System)
    public ApiResult<Void> update(@Valid @RequestBody SysDictTypeUpDTO dto) {
        sysDictTypeService.update(dto);
        return ApiResult.success();
    }

    @Operation(summary = "删除、批量删除")
    @SaCheckPermission(value = "sys.dict.delete_type_btn", orRole = GlobalConstant.SUPER_ROLE)
    @DeleteMapping
    @OperationLog(module = ModuleEnum.System)
    public ApiResult<Void> disable(@RequestBody SelectIdsDTO dto) {
        sysDictTypeService.remove(dto);
        return ApiResult.success();
    }

    @Operation(summary = "字段类型详情查询")
    @GetMapping("{id}")
    public ApiResult<SysDictType> detail(@PathVariable Long id) {
        return ApiResult.success(sysDictTypeService.detail(id));
    }

    @Operation(summary = "列表查询")
    @SaCheckPermission(value = "sys.dict.query_table", orRole = GlobalConstant.SUPER_ROLE)
    @GetMapping
    public ApiResult<PageResult<SysDictType>> list(SysDictTypeListDTO dto) {
        return ApiPageResult.success(sysDictTypeService.list(dto));
    }

    @Operation(summary = "下拉字典类型查询")
    @GetMapping("selectOptionsType")
    public ApiResult<List<DictTypeVO>> selectOptionType() {
        return ApiResult.success(sysDictTypeService.selectDictTypeOptions());
    }

}

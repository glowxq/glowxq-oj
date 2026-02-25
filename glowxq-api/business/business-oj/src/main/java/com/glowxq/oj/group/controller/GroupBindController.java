package com.glowxq.oj.group.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.glowxq.core.common.annotation.OperationLog;
import com.glowxq.core.common.api.BaseApi;
import com.glowxq.core.common.entity.*;
import com.glowxq.core.common.enums.ModuleEnum;
import com.glowxq.oj.group.pojo.dto.GroupBindCreateDTO;
import com.glowxq.oj.group.pojo.dto.GroupBindListDTO;
import com.glowxq.oj.group.pojo.dto.GroupBindUpdateDTO;
import com.glowxq.oj.group.pojo.vo.GroupBindVO;
import com.glowxq.oj.group.service.GroupBindService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * group/班级绑定数据 Api
 *
 * @author glowxq
 * @since 2025-03-28
 */
@Tag(name =  "班级绑定数据")
@RestController
@RequestMapping("/group-bind")
@RequiredArgsConstructor
public class GroupBindController extends BaseApi  {

    private final GroupBindService groupBindService;

    @Operation(summary = "新增班级绑定")
    @SaCheckPermission(value = "group.bind.create")
    @PostMapping
    @OperationLog(module = ModuleEnum.Course)
    public ApiResult<Void> create(@RequestBody GroupBindCreateDTO dto) {
        groupBindService.create(dto);
        return ApiResult.success();
    }

    @Operation(summary = "修改班级绑定")
    @SaCheckPermission(value = "group.bind.update")
    @PutMapping
    @OperationLog(module = ModuleEnum.Course)
    public ApiResult<Void> update(@RequestBody GroupBindUpdateDTO dto) {
        groupBindService.update(dto);
        return ApiResult.success();
    }

    @Operation(summary = "删除班级绑定")
    @SaCheckPermission(value = "group.bind.remove")
    @DeleteMapping
    @OperationLog(module = ModuleEnum.Course)
    public ApiResult<Void> remove(@RequestBody SelectIdsDTO dto) {
        groupBindService.remove(dto);
        return ApiResult.success();
    }

    @Operation(summary = "列表查询")
    @SaCheckPermission(value = "group.bind.query_table")
    @GetMapping
    public ApiResult<PageResult<GroupBindVO>> list(GroupBindListDTO dto) {
        return ApiPageResult.success(groupBindService.page(dto));
    }

    @Operation(summary = "详情")
    @SaCheckPermission(value = "group.bind.query_table")
    @GetMapping("/{id}")
    public ApiResult<GroupBindVO> detail(@PathVariable Object id) {
        return ApiResult.success(groupBindService.detail(id));
    }

    @Operation(summary = "导入班级绑定")
    @Parameters({
      @Parameter(name = "file", description = "上传文件", schema = @Schema(type = "string", format = "binary"), required = true),
    })
    @SaCheckPermission(value = "group.bind.import")
    @PostMapping("/import")
    @OperationLog(module = ModuleEnum.Course)
    public void importExcel(@ModelAttribute ImportExcelDTO dto) {
        groupBindService.importExcel(dto);
    }

    @Operation(summary = "导出班级绑定")
    @SaCheckPermission(value = "group.bind.export")
    @PostMapping("/export")
    @OperationLog(module = ModuleEnum.Course)
    public void exportExcel(@RequestBody GroupBindListDTO dto, HttpServletResponse response) {
        groupBindService.exportExcel(dto, response);
    }
}
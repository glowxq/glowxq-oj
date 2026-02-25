package com.glowxq.oj.group.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.glowxq.core.common.annotation.OperationLog;
import com.glowxq.core.common.api.BaseApi;
import com.glowxq.core.common.entity.*;
import com.glowxq.core.common.enums.ModuleEnum;
import com.glowxq.oj.group.pojo.dto.GroupCreateDTO;
import com.glowxq.oj.group.pojo.dto.GroupListDTO;
import com.glowxq.oj.group.pojo.dto.GroupUpdateDTO;
import com.glowxq.oj.group.pojo.vo.GroupVO;
import com.glowxq.oj.group.service.GroupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * group/班级表 Api
 *
 * @author glowxq
 * @since 2025-03-27
 */
@Tag(name =  "班级表")
@RestController
@RequestMapping("/group")
@RequiredArgsConstructor
public class GroupController extends BaseApi  {

    private final GroupService groupService;

    @Operation(summary = "新增班级")
    @SaCheckPermission(value = "group.create")
    @PostMapping
    @OperationLog(module = ModuleEnum.Group)
    public ApiResult<Void> create(@RequestBody GroupCreateDTO dto) {
        groupService.create(dto);
        return ApiResult.success();
    }

    @Operation(summary = "修改班级")
    @SaCheckPermission(value = "group.update")
    @PutMapping
    @OperationLog(module = ModuleEnum.Group)
    public ApiResult<Void> update(@RequestBody GroupUpdateDTO dto) {
        groupService.update(dto);
        return ApiResult.success();
    }

    @Operation(summary = "删除班级")
    @SaCheckPermission(value = "group.remove")
    @DeleteMapping
    @OperationLog(module = ModuleEnum.Group)
    public ApiResult<Void> remove(@RequestBody SelectIdsDTO dto) {
        groupService.remove(dto);
        return ApiResult.success();
    }

    @Operation(summary = "列表查询")
    @SaCheckPermission(value = "group.query_table")
    @GetMapping
    public ApiResult<PageResult<GroupVO>> list(GroupListDTO dto) {
        return ApiPageResult.success(groupService.page(dto));
    }

    @Operation(summary = "详情")
    @SaCheckPermission(value = "group.query_table")
    @GetMapping("/{id}")
    public ApiResult<GroupVO> detail(@PathVariable Object id) {
        return ApiResult.success(groupService.detail(id));
    }

    @Operation(summary = "导入班级")
    @Parameters({
      @Parameter(name = "file", description = "上传文件", schema = @Schema(type = "string", format = "binary"), required = true),
    })
    @SaCheckPermission(value = "group.import")
    @PostMapping("/import")
    @OperationLog(module = ModuleEnum.Group)
    public void importExcel(@ModelAttribute ImportExcelDTO dto) {
        groupService.importExcel(dto);
    }

    @Operation(summary = "导出班级")
    @SaCheckPermission(value = "group.export")
    @PostMapping("/export")
    @OperationLog(module = ModuleEnum.Group)
    public void exportExcel(@RequestBody GroupListDTO dto, HttpServletResponse response) {
        groupService.exportExcel(dto, response);
    }
}
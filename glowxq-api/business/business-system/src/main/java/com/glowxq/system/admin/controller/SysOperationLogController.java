package com.glowxq.system.admin.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.glowxq.core.common.api.BaseApi;
import com.glowxq.core.common.entity.*;
import com.glowxq.system.admin.pojo.dto.SysOperationLogCreateDTO;
import com.glowxq.system.admin.pojo.dto.SysOperationLogListDTO;
import com.glowxq.system.admin.pojo.dto.SysOperationLogUpdateDTO;
import com.glowxq.system.admin.pojo.vo.SysOperationLogVO;
import com.glowxq.system.admin.service.SysOperationLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * admin/操作日志 Api
 *
 * @author glowxq
 * @since 2025-06-15
 */
@Tag(name =  "操作日志")
@RestController
@RequestMapping
@RequiredArgsConstructor
public class SysOperationLogController extends BaseApi  {

    private final SysOperationLogService sysOperationLogService;

    @Operation(summary = "新增")
    @SaCheckPermission(value = "sys.operation.log.create")
    @PostMapping("/sys-operation-log/create")
    public ApiResult<Void> create(@RequestBody SysOperationLogCreateDTO dto) {
        sysOperationLogService.create(dto);
        return ApiResult.success();
    }

    @Operation(summary = "修改")
    @SaCheckPermission(value = "sys.operation.log.update")
    @PutMapping("/sys-operation-log/update")
    public ApiResult<Void> update(@RequestBody SysOperationLogUpdateDTO dto) {
        sysOperationLogService.update(dto);
        return ApiResult.success();
    }

    @Operation(summary = "删除")
    @SaCheckPermission(value = "sys.operation.log.remove")
    @DeleteMapping("/sys-operation-log/remove")
    public ApiResult<Void> remove(@RequestBody SelectIdsDTO dto) {
        sysOperationLogService.remove(dto);
        return ApiResult.success();
    }

    @Operation(summary = "列表查询")
    @SaCheckPermission(value = "sys.operation.log.query_table")
    @GetMapping("/sys-operation-log/list")
    public ApiResult<PageResult<SysOperationLogVO>> list(SysOperationLogListDTO dto) {
        return ApiPageResult.success(sysOperationLogService.page(dto));
    }

    @Operation(summary = "详情")
    @SaCheckPermission(value = "sys.operation.log.query_table")
    @GetMapping("/sys-operation-log/detail")
    public ApiResult<SysOperationLogVO> detail(@RequestParam Long id) {
        return ApiResult.success(sysOperationLogService.detail(id));
    }

    @Operation(summary = "导入")
    @Parameters({
      @Parameter(name = "file", description = "上传文件", schema = @Schema(type = "string", format = "binary"), required = true),
    })
    @SaCheckPermission(value = "sys.operation.log.import")
    @PostMapping("/sys-operation-log/import")
    public void importExcel(@ModelAttribute ImportExcelDTO dto) {
        sysOperationLogService.importExcel(dto);
    }

    @Operation(summary = "导出")
    @SaCheckPermission(value = "sys.operation.log.export")
    @PostMapping("/sys-operation-log/export")
    public void exportExcel(@RequestBody SysOperationLogListDTO dto, HttpServletResponse response) {
        sysOperationLogService.exportExcel(dto, response);
    }
}
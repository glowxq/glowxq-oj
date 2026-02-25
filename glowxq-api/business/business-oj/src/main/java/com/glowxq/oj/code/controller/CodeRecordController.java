package com.glowxq.oj.code.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.glowxq.core.common.annotation.OperationLog;
import com.glowxq.core.common.api.BaseApi;
import com.glowxq.core.common.entity.*;
import com.glowxq.core.common.enums.ModuleEnum;
import com.glowxq.oj.code.pojo.dto.CodeRecordCreateDTO;
import com.glowxq.oj.code.pojo.dto.CodeRecordListDTO;
import com.glowxq.oj.code.pojo.dto.CodeRecordUpdateDTO;
import com.glowxq.oj.code.pojo.vo.CodeRecordVO;
import com.glowxq.oj.code.service.CodeRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * code/用户代码 Api
 *
 * @author glowxq
 * @since 2025-04-04
 */
@Tag(name =  "用户代码")
@RestController
@RequestMapping("/code-record")
@RequiredArgsConstructor
public class CodeRecordController extends BaseApi  {

    private final CodeRecordService codeRecordService;

    @Operation(summary = "新增代码")
    @SaCheckPermission(value = "code.record.create")
    @PostMapping
    @OperationLog(module = ModuleEnum.Code)
    public ApiResult<Void> create(@RequestBody CodeRecordCreateDTO dto) {
        codeRecordService.create(dto);
        return ApiResult.success();
    }

    @Operation(summary = "修改代码")
    @SaCheckPermission(value = "code.record.update")
    @PutMapping
    @OperationLog(module = ModuleEnum.Code)
    public ApiResult<Void> update(@RequestBody CodeRecordUpdateDTO dto) {
        codeRecordService.update(dto);
        return ApiResult.success();
    }

    @Operation(summary = "删除代码")
    @SaCheckPermission(value = "code.record.remove")
    @DeleteMapping
    @OperationLog(module = ModuleEnum.Code)
    public ApiResult<Void> remove(@RequestBody SelectIdsDTO dto) {
        codeRecordService.remove(dto);
        return ApiResult.success();
    }

    @Operation(summary = "列表查询")
    @SaCheckPermission(value = "code.record.query_table")
    @GetMapping
    @OperationLog(module = ModuleEnum.Code)
    public ApiResult<PageResult<CodeRecordVO>> list(CodeRecordListDTO dto) {
        return ApiPageResult.success(codeRecordService.page(dto));
    }

    @Operation(summary = "详情")
    @SaCheckPermission(value = "code.record.query_table")
    @GetMapping("/{id}")
    @OperationLog(module = ModuleEnum.Code)
    public ApiResult<CodeRecordVO> detail(@PathVariable Object id) {
        return ApiResult.success(codeRecordService.detail(id));
    }

    @Operation(summary = "导入代码")
    @Parameters({
      @Parameter(name = "file", description = "上传文件", schema = @Schema(type = "string", format = "binary"), required = true),
    })
    @SaCheckPermission(value = "code.record.import")
    @PostMapping("/import")
    @OperationLog(module = ModuleEnum.Code)
    public void importExcel(@ModelAttribute ImportExcelDTO dto) {
        codeRecordService.importExcel(dto);
    }

    @Operation(summary = "导出代码")
    @SaCheckPermission(value = "code.record.export")
    @PostMapping("/export")
    @OperationLog(module = ModuleEnum.Code)
    public void exportExcel(@RequestBody CodeRecordListDTO dto, HttpServletResponse response) {
        codeRecordService.exportExcel(dto, response);
    }
}
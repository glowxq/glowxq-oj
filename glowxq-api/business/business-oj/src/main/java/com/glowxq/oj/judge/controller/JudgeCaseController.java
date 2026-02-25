package com.glowxq.oj.judge.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.glowxq.core.common.annotation.OperationLog;
import com.glowxq.core.common.entity.*;
import com.glowxq.core.common.enums.ModuleEnum;
import com.glowxq.oj.judge.pojo.dto.JudgeCaseCreateDTO;
import com.glowxq.oj.judge.pojo.dto.JudgeCaseListDTO;
import com.glowxq.oj.judge.pojo.dto.JudgeCaseUpdateDTO;
import com.glowxq.oj.judge.pojo.vo.JudgeCaseVO;
import com.glowxq.oj.judge.service.JudgeCaseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 测评用例情况 Controller
 * </p>
 *
 * @author glowxq
 * @since 2025-03-17
 */
@Tag(name =  "测评用例情况")
@RestController
@RequestMapping("judge-case")
@RequiredArgsConstructor
public class JudgeCaseController  {

    private final JudgeCaseService judgeCaseService;

    @Operation(summary = "新增测评用例")
    @SaCheckPermission(value = "judge.case.create")
    @PostMapping
    @OperationLog(module = ModuleEnum.Judge)
    public ApiResult<Void> create(@RequestBody JudgeCaseCreateDTO dto) {
        judgeCaseService.create(dto);
        return ApiResult.success();
    }

    @Operation(summary = "修改测评用例")
    @SaCheckPermission(value = "judge.case.update")
    @PutMapping
    @OperationLog(module = ModuleEnum.Judge)
    public ApiResult<Void> update(@RequestBody JudgeCaseUpdateDTO dto) {
        judgeCaseService.update(dto);
        return ApiResult.success();
    }

    @Operation(summary = "删除测评用例")
    @SaCheckPermission(value = "judge.case.remove")
    @DeleteMapping
    @OperationLog(module = ModuleEnum.Judge)
    public ApiResult<Void> remove(@RequestBody SelectIdsDTO dto) {
        judgeCaseService.remove(dto);
        return ApiResult.success();
    }

    @Operation(summary = "列表查询")
    @SaCheckPermission(value = "judge.case.query_table")
    @GetMapping
    public ApiResult<PageResult<JudgeCaseVO>> list(JudgeCaseListDTO dto) {
        return ApiPageResult.success(judgeCaseService.page(dto));
    }

    @Operation(summary = "详情")
    @SaCheckPermission(value = "judge.case.query_table")
    @GetMapping("/{id}")
    public ApiResult<JudgeCaseVO> detail(@PathVariable Object id) {
        return ApiResult.success(judgeCaseService.detail(id));
    }

    @Operation(summary = "导入测评用例")
    @Parameters({
      @Parameter(name = "file", description = "上传文件", schema = @Schema(type = "string", format = "binary"), required = true),
    })
    @SaCheckPermission(value = "judge.case.import")
    @PostMapping("/import")
    @OperationLog(module = ModuleEnum.Judge)
    public void importExcel(@ModelAttribute ImportExcelDTO dto) {
        judgeCaseService.importExcel(dto);
    }

    @Operation(summary = "导出测评用例")
    @SaCheckPermission(value = "judge.case.export")
    @PostMapping("/export")
    @OperationLog(module = ModuleEnum.Judge)
    public void exportExcel(@RequestBody JudgeCaseListDTO dto, HttpServletResponse response) {
        judgeCaseService.exportExcel(dto, response);
    }
}
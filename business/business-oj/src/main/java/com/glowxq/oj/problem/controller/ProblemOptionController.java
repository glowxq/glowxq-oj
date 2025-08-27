package com.glowxq.oj.problem.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.glowxq.core.common.annotation.OperationLog;
import com.glowxq.core.common.entity.*;
import com.glowxq.core.common.enums.ModuleEnum;
import com.glowxq.oj.problem.pojo.dto.ProblemOptionBO;
import com.glowxq.oj.problem.pojo.dto.ProblemOptionListDTO;
import com.glowxq.oj.problem.pojo.dto.ProblemOptionUpdateDTO;
import com.glowxq.oj.problem.pojo.vo.ProblemOptionVO;
import com.glowxq.oj.problem.service.ProblemOptionService;
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
 * 题目选项 Controller
 * </p>
 *
 * @author glowxq
 * @since 2025-03-17
 */
@Tag(name =  "题目选项")
@RestController
@RequestMapping("problem-option")
@RequiredArgsConstructor
public class ProblemOptionController  {

    private final ProblemOptionService problemOptionService;

    @Operation(summary = "新增题目选项")
    @SaCheckPermission(value = "problem.option.create")
    @PostMapping
    @OperationLog(module = ModuleEnum.Problem)
    public ApiResult<Void> create(@RequestBody ProblemOptionBO dto) {
        problemOptionService.create(dto);
        return ApiResult.success();
    }

    @Operation(summary = "修改题目选项")
    @SaCheckPermission(value = "problem.option.update")
    @PutMapping
    @OperationLog(module = ModuleEnum.Problem)
    public ApiResult<Void> update(@RequestBody ProblemOptionUpdateDTO dto) {
        problemOptionService.update(dto);
        return ApiResult.success();
    }

    @Operation(summary = "删除题目选项")
    @SaCheckPermission(value = "problem.option.remove")
    @DeleteMapping
    @OperationLog(module = ModuleEnum.Problem)
    public ApiResult<Void> remove(@RequestBody SelectIdsDTO dto) {
        problemOptionService.remove(dto);
        return ApiResult.success();
    }

    @Operation(summary = "列表查询")
    @SaCheckPermission(value = "problem.option.query_table")
    @GetMapping
    public ApiResult<PageResult<ProblemOptionVO>> list(ProblemOptionListDTO dto) {
        return ApiPageResult.success(problemOptionService.page(dto));
    }

    @Operation(summary = "详情")
    @SaCheckPermission(value = "problem.option.query_table")
    @GetMapping("/{id}")
    public ApiResult<ProblemOptionVO> detail(@PathVariable Object id) {
        return ApiResult.success(problemOptionService.detail(id));
    }

    @Operation(summary = "导入题目选项")
    @Parameters({
      @Parameter(name = "file", description = "上传文件", schema = @Schema(type = "string", format = "binary"), required = true),
    })
    @SaCheckPermission(value = "problem.option.import")
    @PostMapping("/import")
    @OperationLog(module = ModuleEnum.Problem)
    public void importExcel(@ModelAttribute ImportExcelDTO dto) {
        problemOptionService.importExcel(dto);
    }

    @Operation(summary = "导出题目选项")
    @SaCheckPermission(value = "problem.option.export")
    @PostMapping("/export")
    @OperationLog(module = ModuleEnum.Problem)
    public void exportExcel(@RequestBody ProblemOptionListDTO dto, HttpServletResponse response) {
        problemOptionService.exportExcel(dto, response);
    }
}
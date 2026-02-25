package com.glowxq.oj.problem.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.glowxq.core.common.annotation.OperationLog;
import com.glowxq.core.common.entity.*;
import com.glowxq.core.common.enums.ModuleEnum;
import com.glowxq.oj.problem.pojo.dto.ProblemLanguageCreateDTO;
import com.glowxq.oj.problem.pojo.dto.ProblemLanguageListDTO;
import com.glowxq.oj.problem.pojo.dto.ProblemLanguageUpdateDTO;
import com.glowxq.oj.problem.pojo.vo.ProblemLanguageVO;
import com.glowxq.oj.problem.service.ProblemLanguageService;
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
 * 题目语言 Controller
 * </p>
 *
 * @author glowxq
 * @since 2025-03-17
 */
@Tag(name =  "题目语言")
@RestController
@RequestMapping("problem-language")
@RequiredArgsConstructor
public class ProblemLanguageController  {

    private final ProblemLanguageService problemLanguageService;

    @Operation(summary = "新增题目语言")
    @SaCheckPermission(value = "problem.language.create")
    @PostMapping
    @OperationLog(module = ModuleEnum.Problem)
    public ApiResult<Void> create(@RequestBody ProblemLanguageCreateDTO dto) {
        problemLanguageService.create(dto);
        return ApiResult.success();
    }

    @Operation(summary = "修改题目语言")
    @SaCheckPermission(value = "problem.language.update")
    @PutMapping
    @OperationLog(module = ModuleEnum.Problem)
    public ApiResult<Void> update(@RequestBody ProblemLanguageUpdateDTO dto) {
        problemLanguageService.update(dto);
        return ApiResult.success();
    }

    @Operation(summary = "删除题目语言")
    @SaCheckPermission(value = "problem.language.remove")
    @DeleteMapping
    @OperationLog(module = ModuleEnum.Problem)
    public ApiResult<Void> remove(@RequestBody SelectIdsDTO dto) {
        problemLanguageService.remove(dto);
        return ApiResult.success();
    }

    @Operation(summary = "列表查询")
    @SaCheckPermission(value = "problem.language.query_table")
    @GetMapping
    public ApiResult<PageResult<ProblemLanguageVO>> list(ProblemLanguageListDTO dto) {
        return ApiPageResult.success(problemLanguageService.page(dto));
    }

    @Operation(summary = "详情")
    @SaCheckPermission(value = "problem.language.query_table")
    @GetMapping("/{id}")
    public ApiResult<ProblemLanguageVO> detail(@PathVariable Object id) {
        return ApiResult.success(problemLanguageService.detail(id));
    }

    @Operation(summary = "导入题目语言")
    @Parameters({
      @Parameter(name = "file", description = "上传文件", schema = @Schema(type = "string", format = "binary"), required = true),
    })
    @SaCheckPermission(value = "problem.language.import")
    @PostMapping("/import")
    @OperationLog(module = ModuleEnum.Problem)
    public void importExcel(@ModelAttribute ImportExcelDTO dto) {
        problemLanguageService.importExcel(dto);
    }

    @Operation(summary = "导出题目语言")
    @SaCheckPermission(value = "problem.language.export")
    @PostMapping("/export")
    @OperationLog(module = ModuleEnum.Problem)
    public void exportExcel(@RequestBody ProblemLanguageListDTO dto, HttpServletResponse response) {
        problemLanguageService.exportExcel(dto, response);
    }
}
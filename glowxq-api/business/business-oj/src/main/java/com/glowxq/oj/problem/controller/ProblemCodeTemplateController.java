package com.glowxq.oj.problem.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.glowxq.core.common.annotation.OperationLog;
import com.glowxq.core.common.entity.*;
import com.glowxq.core.common.enums.ModuleEnum;
import com.glowxq.oj.problem.pojo.dto.ProblemCodeTemplateCreateDTO;
import com.glowxq.oj.problem.pojo.dto.ProblemCodeTemplateListDTO;
import com.glowxq.oj.problem.pojo.dto.ProblemCodeTemplateUpdateDTO;
import com.glowxq.oj.problem.pojo.vo.ProblemCodeTemplateVO;
import com.glowxq.oj.problem.service.ProblemCodeTemplateService;
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
 * 题目代码模版 Controller
 * </p>
 *
 * @author glowxq
 * @since 2025-03-18
 */
@Tag(name =  "题目代码模版")
@RestController
@RequestMapping("problem-code-template")
@RequiredArgsConstructor
public class ProblemCodeTemplateController  {

    private final ProblemCodeTemplateService problemCodeTemplateService;

    @Operation(summary = "新增题目代码模版")
    @SaCheckPermission(value = "problem.code.template.create")
    @PostMapping
    @OperationLog(module = ModuleEnum.Problem)
    public ApiResult<Void> create(@RequestBody ProblemCodeTemplateCreateDTO dto) {
        problemCodeTemplateService.create(dto);
        return ApiResult.success();
    }

    @Operation(summary = "修改题目代码模版")
    @SaCheckPermission(value = "problem.code.template.update")
    @PutMapping
    @OperationLog(module = ModuleEnum.Problem)
    public ApiResult<Void> update(@RequestBody ProblemCodeTemplateUpdateDTO dto) {
        problemCodeTemplateService.update(dto);
        return ApiResult.success();
    }

    @Operation(summary = "删除题目代码模版")
    @SaCheckPermission(value = "problem.code.template.remove")
    @DeleteMapping
    @OperationLog(module = ModuleEnum.Problem)
    public ApiResult<Void> remove(@RequestBody SelectIdsDTO dto) {
        problemCodeTemplateService.remove(dto);
        return ApiResult.success();
    }

    @Operation(summary = "列表查询")
    @SaCheckPermission(value = "problem.code.template.query_table")
    @GetMapping
    public ApiResult<PageResult<ProblemCodeTemplateVO>> list(ProblemCodeTemplateListDTO dto) {
        return ApiPageResult.success(problemCodeTemplateService.page(dto));
    }

    @Operation(summary = "详情")
    @SaCheckPermission(value = "problem.code.template.query_table")
    @GetMapping("/{id}")
    public ApiResult<ProblemCodeTemplateVO> detail(@PathVariable Object id) {
        return ApiResult.success(problemCodeTemplateService.detail(id));
    }

    @Operation(summary = "导入题目代码模版")
    @Parameters({
      @Parameter(name = "file", description = "上传文件", schema = @Schema(type = "string", format = "binary"), required = true),
    })
    @SaCheckPermission(value = "problem.code.template.import")
    @PostMapping("/import")
    @OperationLog(module = ModuleEnum.Problem)
    public void importExcel(@ModelAttribute ImportExcelDTO dto) {
        problemCodeTemplateService.importExcel(dto);
    }

    @Operation(summary = "导出题目代码模版")
    @SaCheckPermission(value = "problem.code.template.export")
    @PostMapping("/export")
    @OperationLog(module = ModuleEnum.Problem)
    public void exportExcel(@RequestBody ProblemCodeTemplateListDTO dto, HttpServletResponse response) {
        problemCodeTemplateService.exportExcel(dto, response);
    }
}
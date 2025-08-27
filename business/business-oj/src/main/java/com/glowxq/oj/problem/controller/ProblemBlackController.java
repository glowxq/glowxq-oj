package com.glowxq.oj.problem.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.glowxq.core.common.annotation.OperationLog;
import com.glowxq.core.common.api.BaseApi;
import com.glowxq.core.common.entity.*;
import com.glowxq.core.common.enums.ModuleEnum;
import com.glowxq.oj.problem.pojo.dto.ProblemBlackCreateDTO;
import com.glowxq.oj.problem.pojo.dto.ProblemBlackListDTO;
import com.glowxq.oj.problem.pojo.dto.ProblemBlackUpdateDTO;
import com.glowxq.oj.problem.pojo.vo.ProblemBlackVO;
import com.glowxq.oj.problem.service.ProblemBlackService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * problem/题目黑名单 Api
 *
 * @author glowxq
 * @since 2025-04-11
 */
@Tag(name =  "题目黑名单")
@RestController
@RequestMapping("/problem-black")
@RequiredArgsConstructor
public class ProblemBlackController extends BaseApi  {

    private final ProblemBlackService problemBlackService;

    @Operation(summary = "新增题目黑名单")
    @SaCheckPermission(value = "problem.black.create")
    @PostMapping
    @OperationLog(module = ModuleEnum.Problem)
    public ApiResult<Void> create(@RequestBody ProblemBlackCreateDTO dto) {
        problemBlackService.create(dto);
        return ApiResult.success();
    }

    @Operation(summary = "修改题目黑名单")
    @SaCheckPermission(value = "problem.black.update")
    @PutMapping
    @OperationLog(module = ModuleEnum.Problem)
    public ApiResult<Void> update(@RequestBody ProblemBlackUpdateDTO dto) {
        problemBlackService.update(dto);
        return ApiResult.success();
    }

    @Operation(summary = "删除题目黑名单")
    @SaCheckPermission(value = "problem.black.remove")
    @DeleteMapping
    @OperationLog(module = ModuleEnum.Problem)
    public ApiResult<Void> remove(@RequestBody SelectIdsDTO dto) {
        problemBlackService.remove(dto);
        return ApiResult.success();
    }

    @Operation(summary = "列表查询")
    @SaCheckPermission(value = "problem.black.query_table")
    @GetMapping
    public ApiResult<PageResult<ProblemBlackVO>> list(ProblemBlackListDTO dto) {
        return ApiPageResult.success(problemBlackService.page(dto));
    }

    @Operation(summary = "详情")
    @SaCheckPermission(value = "problem.black.query_table")
    @GetMapping("/{id}")
    public ApiResult<ProblemBlackVO> detail(@PathVariable Object id) {
        return ApiResult.success(problemBlackService.detail(id));
    }

    @Operation(summary = "导入题目黑名单")
    @Parameters({
      @Parameter(name = "file", description = "上传文件", schema = @Schema(type = "string", format = "binary"), required = true),
    })
    @SaCheckPermission(value = "problem.black.import")
    @PostMapping("/import")
    @OperationLog(module = ModuleEnum.Problem)
    public void importExcel(@ModelAttribute ImportExcelDTO dto) {
        problemBlackService.importExcel(dto);
    }

    @Operation(summary = "导出题目黑名单")
    @SaCheckPermission(value = "problem.black.export")
    @PostMapping("/export")
    @OperationLog(module = ModuleEnum.Problem)
    public void exportExcel(@RequestBody ProblemBlackListDTO dto, HttpServletResponse response) {
        problemBlackService.exportExcel(dto, response);
    }
}
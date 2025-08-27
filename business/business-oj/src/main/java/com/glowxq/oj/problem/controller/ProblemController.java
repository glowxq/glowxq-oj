package com.glowxq.oj.problem.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaIgnore;
import com.glowxq.core.common.annotation.OperationLog;
import com.glowxq.core.common.entity.*;
import com.glowxq.core.common.enums.ModuleEnum;
import com.glowxq.core.util.BeanCopyUtils;
import com.glowxq.oj.problem.handler.create.ProblemCreateFactory;
import com.glowxq.oj.problem.handler.create.ProblemCreateInterface;
import com.glowxq.oj.problem.handler.update.ProblemUpdateFactory;
import com.glowxq.oj.problem.handler.update.ProblemUpdateInterface;
import com.glowxq.oj.problem.handler.upload.batch.ProblemBatchUploadFactory;
import com.glowxq.oj.problem.handler.upload.batch.ProblemBatchUploadInterface;
import com.glowxq.oj.problem.handler.upload.oss.ProblemUploadFactory;
import com.glowxq.oj.problem.handler.upload.oss.ProblemUploadInterface;
import com.glowxq.oj.problem.pojo.bo.ProblemBatchImportProgramBO;
import com.glowxq.oj.problem.pojo.bo.ProblemImportProgramBO;
import com.glowxq.oj.problem.pojo.dto.ProblemBatchImportProgramDTO;
import com.glowxq.oj.problem.pojo.dto.ProblemCreateUpdateDTO;
import com.glowxq.oj.problem.pojo.dto.ProblemImportProgramDTO;
import com.glowxq.oj.problem.pojo.dto.ProblemListDTO;
import com.glowxq.oj.problem.pojo.vo.ProblemVO;
import com.glowxq.oj.problem.service.ProblemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 题目 Controller
 *
 * @author glowxq
 * @since 2025-03-17
 */
@Tag(name = "题目")
@RestController
@RequestMapping("/problem")
@RequiredArgsConstructor
@SaIgnore
public class ProblemController {

    private final ProblemService problemService;

    private final ProblemCreateFactory problemCreateFactory;

    private final ProblemUpdateFactory problemUpdateFactory;

    private final ProblemUploadFactory problemUploadFactory;

    private final ProblemBatchUploadFactory problemBatchUploadFactory;

    @Operation(summary = "新增题目")
    @SaCheckPermission(value = "problem.create")
    @PostMapping
    @SaIgnore
    @OperationLog(module = ModuleEnum.Problem)
    public ApiResult<Void> create(@RequestBody @Valid ProblemCreateUpdateDTO dto) {
        ProblemCreateInterface problemHandler = problemCreateFactory.getProblemHandler(dto.problemType());
        problemHandler.create(dto);
        return ApiResult.success();
    }

    @Operation(summary = "修改题目")
    @SaCheckPermission(value = "problem.update")
    @PutMapping
    @OperationLog(module = ModuleEnum.Problem)
    public ApiResult<Void> update(@RequestBody @Valid ProblemCreateUpdateDTO dto) {
        ProblemUpdateInterface problemHandler = problemUpdateFactory.getProblemHandler(dto.problemType());
        problemHandler.update(dto);
        return ApiResult.success();
    }

    @Operation(summary = "删除题目")
    @SaCheckPermission(value = "problem.remove")
    @DeleteMapping
    @OperationLog(module = ModuleEnum.Problem)
    public ApiResult<Void> remove(@RequestBody SelectIdsDTO dto) {
        problemService.remove(dto);
        return ApiResult.success();
    }

    @Operation(summary = "列表查询")
    @PostMapping("/list")
    public ApiResult<PageResult<ProblemVO>> list(@RequestBody ProblemListDTO dto) {
        return ApiPageResult.success(problemService.page(dto));
    }

    @Operation(summary = "详情")
    @SaCheckPermission(value = "problem.query_table")
    @GetMapping("/{id}")
    public ApiResult<ProblemCreateUpdateDTO> detail(@PathVariable Long id) {
        return ApiResult.success(problemService.detail(id));
    }

    @Operation(summary = "从Excel导入题目")
    @Parameters({
            @Parameter(name = "file", description = "上传文件", schema = @Schema(type = "string", format = "binary"), required = true),
    })
    @SaCheckPermission(value = "problem.import")
    @PostMapping("/excel/import")
    @OperationLog(module = ModuleEnum.Problem)
    public void importExcel(@ModelAttribute ImportExcelDTO dto) {
        problemService.importExcel(dto);
    }

    @Operation(summary = "导入编程题目")
    @SaCheckPermission(value = "problem.import")
    @PostMapping("/program/import")
    @OperationLog(module = ModuleEnum.Problem)
    public ApiResult<List<String>> importProgramProblem(@RequestBody ProblemImportProgramDTO dto) {
        ProblemImportProgramBO problemImportProgramBO = BeanCopyUtils.copy(dto, ProblemImportProgramBO.class);
        ProblemUploadInterface problemHandler = problemUploadFactory.getProblemHandler(problemImportProgramBO.getProblemSourceType());

        return ApiResult.success(problemHandler.upload(problemImportProgramBO));
    }

    @Operation(summary = "批量导入编程题")
    @SaCheckPermission(value = "problem.import")
    @PostMapping("/program/batch-import")
    public ApiResult<List<String>> batchImportProgramProblem(@RequestBody ProblemBatchImportProgramDTO dto) {
        ProblemBatchImportProgramBO problemBatchImportProgramBO = BeanCopyUtils.copy(dto, ProblemBatchImportProgramBO.class);
        ProblemBatchUploadInterface problemHandler = problemBatchUploadFactory.getProblemHandler(problemBatchImportProgramBO.getProblemSourceType());

        return ApiResult.success(problemHandler.batchUpload(problemBatchImportProgramBO));
    }

    @Operation(summary = "导出题目")
    @SaCheckPermission(value = "problem.export")
    @PostMapping("/export")
    @OperationLog(desc = "导出题目", module = ModuleEnum.Problem)
    public void exportExcel(@RequestBody ProblemListDTO dto, HttpServletResponse response) {
        problemService.exportExcel(dto, response);
    }
}
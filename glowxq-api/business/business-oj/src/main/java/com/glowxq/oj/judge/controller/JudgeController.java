package com.glowxq.oj.judge.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.glowxq.core.common.annotation.OperationLog;
import com.glowxq.core.common.entity.ApiPageResult;
import com.glowxq.core.common.entity.ApiResult;
import com.glowxq.core.common.entity.PageResult;
import com.glowxq.core.common.entity.SelectIdsDTO;
import com.glowxq.core.common.enums.ModuleEnum;
import com.glowxq.oj.judge.pojo.dto.JudgeListDTO;
import com.glowxq.oj.judge.pojo.dto.ManualEvaluationDTO;
import com.glowxq.oj.judge.pojo.po.Judge;
import com.glowxq.oj.judge.pojo.vo.JudgeVO;
import com.glowxq.oj.judge.service.JudgeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 测评记录 Controller
 * </p>
 *
 * @author glowxq
 * @since 2025-03-17
 */
@Tag(name = "测评记录")
@RestController
@RequestMapping("/judge")
@RequiredArgsConstructor
public class JudgeController {

    private final JudgeService judgeService;

    @Operation(summary = "人工测评")
    @SaCheckPermission(value = "judge.update")
    @PutMapping("/manualEvaluation")
    public ApiResult<Void> manualEvaluation(@RequestBody @Valid ManualEvaluationDTO dto) {
        judgeService.manualEvaluation(dto);
        return ApiResult.success();
    }

    @Operation(summary = "列表查询")
    @SaCheckPermission(value = "judge.query_table")
    @GetMapping
    public ApiResult<PageResult<JudgeVO>> list(JudgeListDTO dto) {
        return ApiPageResult.success(judgeService.page(dto));
    }

    @Operation(summary = "详情")
    @GetMapping("/{id}")
    public ApiResult<JudgeVO> detail(@PathVariable Long id) {
        return ApiResult.success(judgeService.detail(id));
    }

    @Operation(summary = "获取测评结果")
    @GetMapping("/result/{id}")
    public ApiResult<Judge> judgeResult(@PathVariable Long id) {
        return ApiResult.success(judgeService.getById(id));
    }

    @Operation(summary = "删除测评记录")
    @SaCheckPermission(value = "judge.remove")
    @DeleteMapping
    @OperationLog(module = ModuleEnum.Judge)
    public ApiResult<Void> remove(@RequestBody SelectIdsDTO dto) {
        judgeService.remove(dto);
        return ApiResult.success();
    }

    @Operation(summary = "导出测评记录")
    @SaCheckPermission(value = "judge.export")
    @PostMapping("/export")
    @OperationLog(module = ModuleEnum.Judge)
    public void exportExcel(@RequestBody JudgeListDTO dto, HttpServletResponse response) {
        judgeService.exportExcel(dto, response);
    }
}
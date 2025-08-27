package com.glowxq.oj.judge.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.glowxq.core.common.annotation.OperationLog;
import com.glowxq.core.common.entity.*;
import com.glowxq.core.common.enums.ModuleEnum;
import com.glowxq.oj.judge.pojo.dto.JudgeServerCreateDTO;
import com.glowxq.oj.judge.pojo.dto.JudgeServerListDTO;
import com.glowxq.oj.judge.pojo.dto.JudgeServerUpdateDTO;
import com.glowxq.oj.judge.pojo.vo.JudgeServerVO;
import com.glowxq.oj.judge.service.JudgeServerService;
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
 * 测评服务 Controller
 * </p>
 *
 * @author glowxq
 * @since 2025-03-17
 */
@Tag(name =  "测评服务")
@RestController
@RequestMapping("judge-server")
@RequiredArgsConstructor
public class JudgeServerController  {

    private final JudgeServerService judgeServerService;

    @Operation(summary = "新增测评服务")
    @SaCheckPermission(value = "judge.server.create")
    @PostMapping
    @OperationLog(module = ModuleEnum.Judge)
    public ApiResult<Void> create(@RequestBody JudgeServerCreateDTO dto) {
        judgeServerService.create(dto);
        return ApiResult.success();
    }

    @Operation(summary = "修改测评服务")
    @SaCheckPermission(value = "judge.server.update")
    @PutMapping
    @OperationLog(module = ModuleEnum.Judge)
    public ApiResult<Void> update(@RequestBody JudgeServerUpdateDTO dto) {
        judgeServerService.update(dto);
        return ApiResult.success();
    }

    @Operation(summary = "删除测评服务")
    @SaCheckPermission(value = "judge.server.remove")
    @DeleteMapping
    @OperationLog(module = ModuleEnum.Judge)
    public ApiResult<Void> remove(@RequestBody SelectIdsDTO dto) {
        judgeServerService.remove(dto);
        return ApiResult.success();
    }

    @Operation(summary = "列表查询")
    @SaCheckPermission(value = "judge.server.query_table")
    @GetMapping
    public ApiResult<PageResult<JudgeServerVO>> list(JudgeServerListDTO dto) {
        return ApiPageResult.success(judgeServerService.page(dto));
    }

    @Operation(summary = "详情")
    @SaCheckPermission(value = "judge.server.query_table")
    @GetMapping("/{id}")
    public ApiResult<JudgeServerVO> detail(@PathVariable Object id) {
        return ApiResult.success(judgeServerService.detail(id));
    }

    @Operation(summary = "导入测评服务")
    @Parameters({
      @Parameter(name = "file", description = "上传文件", schema = @Schema(type = "string", format = "binary"), required = true),
    })
    @SaCheckPermission(value = "judge.server.import")
    @PostMapping("/import")
    @OperationLog(module = ModuleEnum.Judge)
    public void importExcel(@ModelAttribute ImportExcelDTO dto) {
        judgeServerService.importExcel(dto);
    }

    @Operation(summary = "导出测评服务")
    @SaCheckPermission(value = "judge.server.export")
    @PostMapping("/export")
    @OperationLog(module = ModuleEnum.Judge)
    public void exportExcel(@RequestBody JudgeServerListDTO dto, HttpServletResponse response) {
        judgeServerService.exportExcel(dto, response);
    }
}
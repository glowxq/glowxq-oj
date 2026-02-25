package com.glowxq.oj.code.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.glowxq.core.common.annotation.OperationLog;
import com.glowxq.core.common.api.BaseApi;
import com.glowxq.core.common.entity.*;
import com.glowxq.core.common.enums.BusinessHandlerType;
import com.glowxq.core.common.enums.ModuleEnum;
import com.glowxq.oj.code.pojo.dto.*;
import com.glowxq.oj.code.pojo.po.CodeMonitor;
import com.glowxq.oj.code.pojo.vo.CodeMonitorVO;
import com.glowxq.oj.code.service.CodeMonitorService;
import com.glowxq.oj.user.pojo.po.UserInfo;
import com.glowxq.oj.user.service.UserInfoService;
import com.glowxq.security.core.util.LoginUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * code/代码监控 Api
 *
 * @author glowxq
 * @since 2025-04-04
 */
@Tag(name = "代码监控")
@RestController
@RequestMapping("/code-monitor")
@RequiredArgsConstructor
public class CodeMonitorController extends BaseApi {

    private final CodeMonitorService codeMonitorService;

    private final UserInfoService userInfoService;

    @Operation(summary = "学生监控代码")
    @PostMapping("/monitorCode")
    public ApiResult<Void> monitorCode(@RequestBody CodeMonitorDTO dto) {
        UserInfo userInfo = userInfoService.getById(LoginUtils.getUserId());
        codeMonitorService.monitorCode(dto, userInfo);
        return ApiResult.success();
    }

    @Operation(summary = "老师推送覆盖代码")
    @SaCheckPermission(value = "code.monitor.update")
    @PutMapping("/coveredPush")
    @OperationLog(module = ModuleEnum.Code)
    public ApiResult<Void> coveredPush(@RequestBody CoveredCodeMonitorDTO dto) {
        codeMonitorService.coveredPush(dto);
        return ApiResult.success();
    }

    @Operation(summary = "学生处理老师推送的覆盖")
    @PutMapping("/coveredHandle")
    @OperationLog(module = ModuleEnum.Code)
    public ApiResult<Void> coveredHandle(@RequestBody @Validated CoveredHandleDTO dto) {
        codeMonitorService.coveredHandle(dto);
        return ApiResult.success();
    }

    @Operation(summary = "删除")
    @SaCheckPermission(value = "code.monitor.remove")
    @DeleteMapping
    @OperationLog(module = ModuleEnum.Code)
    public ApiResult<Void> remove(@RequestBody SelectIdsDTO dto) {
        codeMonitorService.remove(dto);
        return ApiResult.success();
    }

    @Operation(summary = "列表查询")
    @SaCheckPermission(value = "code.monitor.query_table")
    @GetMapping
    @OperationLog(module = ModuleEnum.Code, handleType = BusinessHandlerType.Query)
    public ApiResult<PageResult<CodeMonitorVO>> list(CodeMonitorListDTO dto) {
        return ApiPageResult.success(codeMonitorService.page(dto));
    }

    @Operation(summary = "搜索监控的代码")
    @SaCheckPermission(value = "code.monitor.query_table")
    @GetMapping("/searchMonitorCodeList")
    @OperationLog(module = ModuleEnum.Code)
    public ApiResult<List<CodeMonitor>> searchMonitorCodeList(SearchMonitorCodeDTO dto) {
        return ApiResult.success(codeMonitorService.searchMonitorCodeList(dto));
    }

    @Operation(summary = "监控某个班级的代码")
    @SaCheckPermission(value = "code.monitor.query_table")
    @GetMapping("/listGroup")
    @OperationLog(module = ModuleEnum.Code)
    public ApiResult<List<CodeMonitor>> listGroup(@RequestParam Long groupId) {
        return ApiResult.success(codeMonitorService.listByGroupId(groupId));
    }

    @Operation(summary = "详情")
    @SaCheckPermission(value = "code.monitor.query_table")
    @GetMapping("/{id}")
    @OperationLog(module = ModuleEnum.Code)
    public ApiResult<CodeMonitorVO> detail(@PathVariable Object id) {
        return ApiResult.success(codeMonitorService.detail(id));
    }

    @Operation(summary = "导入代码监控")
    @Parameters({
            @Parameter(name = "file", description = "上传文件", schema = @Schema(type = "string", format = "binary"), required = true),
    })
    @SaCheckPermission(value = "code.monitor.import")
    @PostMapping("/import")
    @OperationLog(module = ModuleEnum.Code)
    public void importExcel(@ModelAttribute ImportExcelDTO dto) {
        codeMonitorService.importExcel(dto);
    }

    @Operation(summary = "导出代码监控")
    @SaCheckPermission(value = "code.monitor.export")
    @PostMapping("/export")
    @OperationLog(module = ModuleEnum.Code)
    public void exportExcel(@RequestBody CodeMonitorListDTO dto, HttpServletResponse response) {
        codeMonitorService.exportExcel(dto, response);
    }
}
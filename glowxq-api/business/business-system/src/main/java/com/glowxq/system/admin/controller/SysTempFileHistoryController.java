package com.glowxq.system.admin.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.glowxq.core.common.entity.ApiPageResult;
import com.glowxq.core.common.entity.ApiResult;
import com.glowxq.core.common.entity.PageResult;
import com.glowxq.system.admin.pojo.dto.systempfile.SysTempFileHistoryListDTO;
import com.glowxq.system.admin.pojo.po.SysTempFileHistory;
import com.glowxq.system.admin.service.SysTempFileHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 模版文件历史表 Controller
 * </p>
 *
 * @author glowxq-admin
 * @since 2024-12-14
 */
@Tag(name = "模版文件历史表")
@RestController
@RequestMapping("sys-temp-file-history")
@RequiredArgsConstructor
public class SysTempFileHistoryController {

    private final SysTempFileHistoryService sysTempFileHistoryService;

    @Operation(summary = "模板文件历史查询")
    @SaCheckPermission(value = "sys.temp.file.query_table")
    @GetMapping("history")
    public ApiResult<PageResult<SysTempFileHistory>> list(SysTempFileHistoryListDTO dto) {
        return ApiPageResult.success(sysTempFileHistoryService.historyList(dto));
    }

}
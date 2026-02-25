package com.glowxq.system.applet.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.glowxq.core.common.api.BaseApi;
import com.glowxq.core.common.entity.*;
import com.glowxq.system.applet.pojo.dto.AppletUserCreateDTO;
import com.glowxq.system.applet.pojo.dto.AppletUserListDTO;
import com.glowxq.system.applet.pojo.dto.AppletUserUpdateDTO;
import com.glowxq.system.applet.pojo.vo.AppletUserVO;
import com.glowxq.system.applet.service.AppletUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * applet/小程序用户 Controller
 *
 * @author glowxq
 * @since 2025-04-26
 */
@Tag(name =  "小程序用户")
@RestController
@RequestMapping
@RequiredArgsConstructor
public class AppletUserController extends BaseApi  {

    private final AppletUserService appletUserService;

    @Operation(summary = "新增")
    @SaCheckPermission(value = "applet.user.create")
    @PostMapping("/applet-user/create")
    public ApiResult<Void> create(@RequestBody AppletUserCreateDTO dto) {
        appletUserService.create(dto);
        return ApiResult.success();
    }

    @Operation(summary = "修改")
    @SaCheckPermission(value = "applet.user.update")
    @PutMapping("/applet-user/update")
    public ApiResult<Void> update(@RequestBody AppletUserUpdateDTO dto) {
        appletUserService.update(dto);
        return ApiResult.success();
    }

    @Operation(summary = "删除")
    @SaCheckPermission(value = "applet.user.remove")
    @DeleteMapping("/applet-user/remove")
    public ApiResult<Void> remove(@RequestBody SelectIdsDTO dto) {
        appletUserService.remove(dto);
        return ApiResult.success();
    }

    @Operation(summary = "列表查询")
    @SaCheckPermission(value = "applet.user.query_table")
    @GetMapping("/applet-user/list")
    public ApiResult<PageResult<AppletUserVO>> list(AppletUserListDTO dto) {
        return ApiPageResult.success(appletUserService.page(dto));
    }

    @Operation(summary = "详情")
    @SaCheckPermission(value = "applet.user.query_table")
    @GetMapping("/applet-user/detail")
    public ApiResult<AppletUserVO> detail(@RequestParam Object id) {
        return ApiResult.success(appletUserService.detail(id));
    }

    @Operation(summary = "导入")
    @Parameters({
      @Parameter(name = "file", description = "上传文件", schema = @Schema(type = "string", format = "binary"), required = true),
    })
    @SaCheckPermission(value = "applet.user.import")
    @PostMapping("/applet-user/import")
    public void importExcel(@ModelAttribute ImportExcelDTO dto) {
        appletUserService.importExcel(dto);
    }

    @Operation(summary = "导出")
    @SaCheckPermission(value = "applet.user.export")
    @PostMapping("/applet-user/export")
    public void exportExcel(@RequestBody AppletUserListDTO dto, HttpServletResponse response) {
        appletUserService.exportExcel(dto, response);
    }
}
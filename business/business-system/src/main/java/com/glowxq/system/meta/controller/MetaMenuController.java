package com.glowxq.system.meta.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.glowxq.core.common.api.BaseApi;
import com.glowxq.core.common.entity.*;
import com.glowxq.system.meta.pojo.dto.MetaMenuCreateDTO;
import com.glowxq.system.meta.pojo.dto.MetaMenuListDTO;
import com.glowxq.system.meta.pojo.dto.MetaMenuUpdateDTO;
import com.glowxq.system.meta.pojo.vo.MetaMenuVO;
import com.glowxq.system.meta.service.MetaMenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * meta/菜单 Api
 *
 * @author glowxq
 * @since 2025-04-23
 */
@Tag(name =  "菜单")
@RestController
@RequestMapping
@RequiredArgsConstructor
public class MetaMenuController extends BaseApi  {

    private final MetaMenuService metaMenuService;

    @Operation(summary = "新增")
    @SaCheckPermission(value = "meta.menu.create")
    @PostMapping("/meta-menu/create")
    public ApiResult<Void> create(@RequestBody MetaMenuCreateDTO dto) {
        metaMenuService.create(dto);
        return ApiResult.success();
    }

    @Operation(summary = "修改")
    @SaCheckPermission(value = "meta.menu.update")
    @PutMapping("/meta-menu/update")
    public ApiResult<Void> update(@RequestBody MetaMenuUpdateDTO dto) {
        metaMenuService.update(dto);
        return ApiResult.success();
    }

    @Operation(summary = "删除")
    @SaCheckPermission(value = "meta.menu.remove")
    @DeleteMapping("/meta-menu/remove")
    public ApiResult<Void> remove(@RequestBody SelectIdsDTO dto) {
        metaMenuService.remove(dto);
        return ApiResult.success();
    }

    @Operation(summary = "列表查询")
    // @SaCheckPermission(value = "meta.menu.query_table")
    @GetMapping("/meta-menu/list")
    public ApiResult<PageResult<MetaMenuVO>> list(MetaMenuListDTO dto) {
        return ApiPageResult.success(metaMenuService.page(dto));
    }

    @Operation(summary = "详情")
    @SaCheckPermission(value = "meta.menu.query_table")
    @GetMapping("/meta-menu/detail")
    public ApiResult<MetaMenuVO> detail(@RequestParam Object id) {
        return ApiResult.success(metaMenuService.detail(id));
    }

    @Operation(summary = "导入")
    @Parameters({
      @Parameter(name = "file", description = "上传文件", schema = @Schema(type = "string", format = "binary"), required = true),
    })
    @SaCheckPermission(value = "meta.menu.import")
    @PostMapping("/meta-menu/import")
    public void importExcel(@ModelAttribute ImportExcelDTO dto) {
        metaMenuService.importExcel(dto);
    }

    @Operation(summary = "导出")
    @SaCheckPermission(value = "meta.menu.export")
    @PostMapping("/meta-menu/export")
    public void exportExcel(@RequestBody MetaMenuListDTO dto, HttpServletResponse response) {
        metaMenuService.exportExcel(dto, response);
    }
}
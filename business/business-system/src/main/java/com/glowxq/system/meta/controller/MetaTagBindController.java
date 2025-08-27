package com.glowxq.system.meta.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.glowxq.core.common.api.BaseApi;
import com.glowxq.core.common.entity.*;
import com.glowxq.system.meta.pojo.dto.MetaTagBindCreateDTO;
import com.glowxq.system.meta.pojo.dto.MetaTagBindListDTO;
import com.glowxq.system.meta.pojo.dto.MetaTagBindUpdateDTO;
import com.glowxq.system.meta.pojo.vo.MetaTagBindVO;
import com.glowxq.system.meta.service.MetaTagBindService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * meta/绑定标签权限 Api
 *
 * @author glowxq
 * @since 2025-04-23
 */
@Tag(name =  "绑定标签权限")
@RestController
@RequestMapping
@RequiredArgsConstructor
public class MetaTagBindController extends BaseApi  {

    private final MetaTagBindService metaTagBindService;

    @Operation(summary = "新增")
    @SaCheckPermission(value = "meta.tag.bind.create")
    @PostMapping("/meta-tag-bind/create")
    public ApiResult<Void> create(@RequestBody MetaTagBindCreateDTO dto) {
        metaTagBindService.create(dto);
        return ApiResult.success();
    }

    @Operation(summary = "修改")
    @SaCheckPermission(value = "meta.tag.bind.update")
    @PutMapping("/meta-tag-bind/update")
    public ApiResult<Void> update(@RequestBody MetaTagBindUpdateDTO dto) {
        metaTagBindService.update(dto);
        return ApiResult.success();
    }

    @Operation(summary = "删除")
    @SaCheckPermission(value = "meta.tag.bind.remove")
    @DeleteMapping("/meta-tag-bind/remove")
    public ApiResult<Void> remove(@RequestBody SelectIdsDTO dto) {
        metaTagBindService.remove(dto);
        return ApiResult.success();
    }

    @Operation(summary = "列表查询")
    // @SaCheckPermission(value = "meta.tag.bind.query_table")
    @GetMapping("/meta-tag-bind/list")
    public ApiResult<PageResult<MetaTagBindVO>> list(MetaTagBindListDTO dto) {
        return ApiPageResult.success(metaTagBindService.page(dto));
    }

    @Operation(summary = "详情")
    @SaCheckPermission(value = "meta.tag.bind.query_table")
    @GetMapping("/meta-tag-bind/detail")
    public ApiResult<MetaTagBindVO> detail(@RequestParam Object id) {
        return ApiResult.success(metaTagBindService.detail(id));
    }

    @Operation(summary = "导入")
    @Parameters({
      @Parameter(name = "file", description = "上传文件", schema = @Schema(type = "string", format = "binary"), required = true),
    })
    @SaCheckPermission(value = "meta.tag.bind.import")
    @PostMapping("/meta-tag-bind/import")
    public void importExcel(@ModelAttribute ImportExcelDTO dto) {
        metaTagBindService.importExcel(dto);
    }

    @Operation(summary = "导出")
    @SaCheckPermission(value = "meta.tag.bind.export")
    @PostMapping("/meta-tag-bind/export")
    public void exportExcel(@RequestBody MetaTagBindListDTO dto, HttpServletResponse response) {
        metaTagBindService.exportExcel(dto, response);
    }
}
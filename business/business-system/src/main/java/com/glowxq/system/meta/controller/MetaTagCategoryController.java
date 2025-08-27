package com.glowxq.system.meta.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.glowxq.core.common.api.BaseApi;
import com.glowxq.core.common.entity.*;
import com.glowxq.system.meta.pojo.dto.MetaTagCategoryCreateDTO;
import com.glowxq.system.meta.pojo.dto.MetaTagCategoryListDTO;
import com.glowxq.system.meta.pojo.dto.MetaTagCategoryUpdateDTO;
import com.glowxq.system.meta.pojo.vo.MetaTagCategoryVO;
import com.glowxq.system.meta.service.MetaTagCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * meta/标签分类 Api
 *
 * @author glowxq
 * @since 2025-06-10
 */
@Tag(name = "标签分类")
@RestController
@RequestMapping
@RequiredArgsConstructor
public class MetaTagCategoryController extends BaseApi {

    private final MetaTagCategoryService metaTagCategoryService;

    @Operation(summary = "新增")
    @SaCheckPermission(value = "meta.tag.category.create")
    @PostMapping("/meta-tag-category/create")
    public ApiResult<Void> create(@RequestBody MetaTagCategoryCreateDTO dto) {
        metaTagCategoryService.create(dto);
        return ApiResult.success();
    }

    @Operation(summary = "修改")
    @SaCheckPermission(value = "meta.tag.category.update")
    @PutMapping("/meta-tag-category/update")
    public ApiResult<Void> update(@RequestBody MetaTagCategoryUpdateDTO dto) {
        metaTagCategoryService.update(dto);
        return ApiResult.success();
    }

    @Operation(summary = "删除")
    @SaCheckPermission(value = "meta.tag.category.remove")
    @DeleteMapping("/meta-tag-category/remove")
    public ApiResult<Void> remove(@RequestBody SelectIdsDTO dto) {
        metaTagCategoryService.remove(dto);
        return ApiResult.success();
    }

    @Operation(summary = "列表查询")
    // @SaCheckPermission(value = "meta.tag.query_table")
    @GetMapping("/meta-tag-category/list")
    public ApiResult<PageResult<MetaTagCategoryVO>> list(MetaTagCategoryListDTO dto) {
        return ApiPageResult.success(metaTagCategoryService.page(dto));
    }

    @Operation(summary = "详情")
    @SaCheckPermission(value = "meta.tag.category.query_table")
    @GetMapping("/meta-tag-category/detail")
    public ApiResult<MetaTagCategoryVO> detail(@RequestParam Long id) {
        return ApiResult.success(metaTagCategoryService.detail(id));
    }

    @Operation(summary = "导入")
    @Parameters({
            @Parameter(name = "file", description = "上传文件", schema = @Schema(type = "string", format = "binary"), required = true),
    })
    @SaCheckPermission(value = "meta.tag.category.import")
    @PostMapping("/meta-tag-category/import")
    public void importExcel(@ModelAttribute ImportExcelDTO dto) {
        metaTagCategoryService.importExcel(dto);
    }

    @Operation(summary = "导出")
    @SaCheckPermission(value = "meta.tag.category.export")
    @PostMapping("/meta-tag-category/export")
    public void exportExcel(@RequestBody MetaTagCategoryListDTO dto, HttpServletResponse response) {
        metaTagCategoryService.exportExcel(dto, response);
    }
}
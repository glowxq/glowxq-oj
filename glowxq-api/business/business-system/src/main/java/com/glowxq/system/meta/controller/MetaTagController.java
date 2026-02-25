package com.glowxq.system.meta.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.glowxq.core.common.api.BaseApi;
import com.glowxq.core.common.entity.*;
import com.glowxq.system.meta.pojo.dto.MetaTagBatchUpdateCategoryDTO;
import com.glowxq.system.meta.pojo.dto.MetaTagCreateDTO;
import com.glowxq.system.meta.pojo.dto.MetaTagListDTO;
import com.glowxq.system.meta.pojo.dto.MetaTagUpdateDTO;
import com.glowxq.system.meta.pojo.vo.MetaTagCountVO;
import com.glowxq.system.meta.pojo.vo.MetaTagVO;
import com.glowxq.system.meta.service.MetaTagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * meta/标签 Api
 *
 * @author glowxq
 * @since 2025-04-23
 */
@Tag(name =  "标签")
@RestController
@RequestMapping
@RequiredArgsConstructor
public class MetaTagController extends BaseApi  {

    private final MetaTagService metaTagService;

    @Operation(summary = "新增")
    @SaCheckPermission(value = "meta.tag.create")
    @PostMapping("/meta-tag/create")
    public ApiResult<Void> create(@RequestBody MetaTagCreateDTO dto) {
        metaTagService.create(dto);
        return ApiResult.success();
    }

    @Operation(summary = "修改")
    @SaCheckPermission(value = "meta.tag.update")
    @PutMapping("/meta-tag/update")
    public ApiResult<Void> update(@RequestBody MetaTagUpdateDTO dto) {
        metaTagService.update(dto);
        return ApiResult.success();
    }

    @Operation(summary = "修改")
    @SaCheckPermission(value = "meta.tag.update")
    @PutMapping("/meta-tag/batchUpdateCategory")
    public ApiResult<Void> batchUpdateCategory(@RequestBody MetaTagBatchUpdateCategoryDTO dto) {
        metaTagService.batchUpdateCategory(dto);
        return ApiResult.success();
    }

    @Operation(summary = "删除")
    @SaCheckPermission(value = "meta.tag.remove")
    @DeleteMapping("/meta-tag/remove")
    public ApiResult<Void> remove(@RequestBody SelectIdsDTO dto) {
        metaTagService.remove(dto);
        return ApiResult.success();
    }

    @Operation(summary = "列表查询")
    // @SaCheckPermission(value = "meta.tag.query_table")
    @GetMapping("/meta-tag/list")
    public ApiResult<PageResult<MetaTagVO>> list(MetaTagListDTO dto) {
        return ApiPageResult.success(metaTagService.page(dto));
    }

    @Operation(summary = "列表查询")
    // @SaCheckPermission(value = "meta.tag.query_table")
    @GetMapping("/meta-tag/categoryCount")
    public ApiResult<List<MetaTagCountVO>> categoryCount() {
        return ApiResult.success(metaTagService.categoryCount());
    }

    @Operation(summary = "详情")
    @SaCheckPermission(value = "meta.tag.query_table")
    @GetMapping("/meta-tag/detail")
    public ApiResult<MetaTagVO> detail(@RequestParam Object id) {
        return ApiResult.success(metaTagService.detail(id));
    }

    @Operation(summary = "导入")
    @Parameters({
      @Parameter(name = "file", description = "上传文件", schema = @Schema(type = "string", format = "binary"), required = true),
    })
    @SaCheckPermission(value = "meta.tag.import")
    @PostMapping("/meta-tag/import")
    public void importExcel(@ModelAttribute ImportExcelDTO dto) {
        metaTagService.importExcel(dto);
    }

    @Operation(summary = "导出")
    @SaCheckPermission(value = "meta.tag.export")
    @PostMapping("/meta-tag/export")
    public void exportExcel(@RequestBody MetaTagListDTO dto, HttpServletResponse response) {
        metaTagService.exportExcel(dto, response);
    }
}
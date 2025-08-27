package com.glowxq.system.meta.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.glowxq.core.common.api.BaseApi;
import com.glowxq.core.common.entity.*;
import com.glowxq.system.meta.pojo.dto.MetaCategoryCreateDTO;
import com.glowxq.system.meta.pojo.dto.MetaCategoryListDTO;
import com.glowxq.system.meta.pojo.dto.MetaCategoryUpdateDTO;
import com.glowxq.system.meta.pojo.vo.MetaCategoryTreeVO;
import com.glowxq.system.meta.pojo.vo.MetaCategoryVO;
import com.glowxq.system.meta.service.MetaCategoryService;
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
 * meta/分类 Api
 *
 * @author glowxq
 * @since 2025-04-23
 */
@Tag(name =  "分类")
@RestController
@RequestMapping
@RequiredArgsConstructor
public class MetaCategoryController extends BaseApi  {

    private final MetaCategoryService metaCategoryService;

    @Operation(summary = "新增")
    @SaCheckPermission(value = "meta.category.create")
    @PostMapping("/meta-category/create")
    public ApiResult<Void> create(@RequestBody MetaCategoryCreateDTO dto) {
        metaCategoryService.create(dto);
        return ApiResult.success();
    }

    @Operation(summary = "修改")
    @SaCheckPermission(value = "meta.category.update")
    @PutMapping("/meta-category/update")
    public ApiResult<Void> update(@RequestBody MetaCategoryUpdateDTO dto) {
        metaCategoryService.update(dto);
        return ApiResult.success();
    }

    @Operation(summary = "删除")
    @SaCheckPermission(value = "meta.category.remove")
    @DeleteMapping("/meta-category/remove")
    public ApiResult<Void> remove(@RequestBody SelectIdsDTO dto) {
        metaCategoryService.remove(dto);
        return ApiResult.success();
    }

    @Operation(summary = "列表查询")
    // @SaCheckPermission(value = "meta.category.query_table")
    @GetMapping("/meta-category/list")
    public ApiResult<PageResult<MetaCategoryVO>> list(MetaCategoryListDTO dto) {
        return ApiPageResult.success(metaCategoryService.page(dto));
    }

    @Operation(summary = "树形列表")
    @GetMapping("/meta-category/tree")
    public ApiResult<List<MetaCategoryTreeVO>> tree(@Parameter(description = "需要排除的节点ID") @RequestParam(required = false) Integer excludeNodeId,
                                                    @Parameter(description = "是否添加根节点") @RequestParam(required = false) Boolean appendRoot) {
        return ApiResult.success(metaCategoryService.getTree(excludeNodeId, appendRoot, false));
    }
    @Operation(summary = "详情")
    @SaCheckPermission(value = "meta.category.query_table")
    @GetMapping("/meta-category/detail")
    public ApiResult<MetaCategoryVO> detail(@RequestParam Object id) {
        return ApiResult.success(metaCategoryService.detail(id));
    }

    @Operation(summary = "导入")
    @Parameters({
      @Parameter(name = "file", description = "上传文件", schema = @Schema(type = "string", format = "binary"), required = true),
    })
    @SaCheckPermission(value = "meta.category.import")
    @PostMapping("/meta-category/import")
    public void importExcel(@ModelAttribute ImportExcelDTO dto) {
        metaCategoryService.importExcel(dto);
    }

    @Operation(summary = "导出")
    @SaCheckPermission(value = "meta.category.export")
    @PostMapping("/meta-category/export")
    public void exportExcel(@RequestBody MetaCategoryListDTO dto, HttpServletResponse response) {
        metaCategoryService.exportExcel(dto, response);
    }
}
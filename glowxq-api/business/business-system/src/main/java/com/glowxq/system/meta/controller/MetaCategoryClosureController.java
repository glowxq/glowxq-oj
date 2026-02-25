package com.glowxq.system.meta.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.glowxq.core.common.api.BaseApi;
import com.glowxq.core.common.entity.*;
import com.glowxq.system.meta.pojo.dto.MetaCategoryClosureCreateDTO;
import com.glowxq.system.meta.pojo.dto.MetaCategoryClosureListDTO;
import com.glowxq.system.meta.pojo.dto.MetaCategoryClosureUpdateDTO;
import com.glowxq.system.meta.pojo.vo.MetaCategoryClosureVO;
import com.glowxq.system.meta.service.MetaCategoryClosureService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * meta/分类祖籍关系表 Api
 *
 * @author glowxq
 * @since 2025-04-24
 */
@Tag(name =  "分类祖籍关系表")
@RestController
@RequestMapping
@RequiredArgsConstructor
public class MetaCategoryClosureController extends BaseApi  {

    private final MetaCategoryClosureService metaCategoryClosureService;

    @Operation(summary = "新增")
    @SaCheckPermission(value = "meta.category.closure.create")
    @PostMapping("/meta-category-closure/create")
    public ApiResult<Void> create(@RequestBody MetaCategoryClosureCreateDTO dto) {
        metaCategoryClosureService.create(dto);
        return ApiResult.success();
    }

    @Operation(summary = "修改")
    @SaCheckPermission(value = "meta.category.closure.update")
    @PutMapping("/meta-category-closure/update")
    public ApiResult<Void> update(@RequestBody MetaCategoryClosureUpdateDTO dto) {
        metaCategoryClosureService.update(dto);
        return ApiResult.success();
    }

    @Operation(summary = "删除")
    @SaCheckPermission(value = "meta.category.closure.remove")
    @DeleteMapping("/meta-category-closure/remove")
    public ApiResult<Void> remove(@RequestBody SelectIdsDTO dto) {
        metaCategoryClosureService.remove(dto);
        return ApiResult.success();
    }

    @Operation(summary = "列表查询")
    @SaCheckPermission(value = "meta.category.closure.query_table")
    // @GetMapping("/meta-category-closure/list")
    public ApiResult<PageResult<MetaCategoryClosureVO>> list(MetaCategoryClosureListDTO dto) {
        return ApiPageResult.success(metaCategoryClosureService.page(dto));
    }

    @Operation(summary = "详情")
    @SaCheckPermission(value = "meta.category.closure.query_table")
    @GetMapping("/meta-category-closure/detail")
    public ApiResult<MetaCategoryClosureVO> detail(@RequestParam Object id) {
        return ApiResult.success(metaCategoryClosureService.detail(id));
    }

    @Operation(summary = "导入")
    @Parameters({
      @Parameter(name = "file", description = "上传文件", schema = @Schema(type = "string", format = "binary"), required = true),
    })
    @SaCheckPermission(value = "meta.category.closure.import")
    @PostMapping("/meta-category-closure/import")
    public void importExcel(@ModelAttribute ImportExcelDTO dto) {
        metaCategoryClosureService.importExcel(dto);
    }

    @Operation(summary = "导出")
    @SaCheckPermission(value = "meta.category.closure.export")
    @PostMapping("/meta-category-closure/export")
    public void exportExcel(@RequestBody MetaCategoryClosureListDTO dto, HttpServletResponse response) {
        metaCategoryClosureService.exportExcel(dto, response);
    }
}
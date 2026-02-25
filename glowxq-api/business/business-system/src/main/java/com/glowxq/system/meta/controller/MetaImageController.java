package com.glowxq.system.meta.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.glowxq.core.common.api.BaseApi;
import com.glowxq.core.common.entity.*;
import com.glowxq.system.meta.pojo.dto.MetaImageCreateDTO;
import com.glowxq.system.meta.pojo.dto.MetaImageListDTO;
import com.glowxq.system.meta.pojo.dto.MetaImageUpdateDTO;
import com.glowxq.system.meta.pojo.vo.MetaImageVO;
import com.glowxq.system.meta.service.MetaImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * meta/图片 Api
 *
 * @author glowxq
 * @since 2025-04-25
 */
@Tag(name =  "图片")
@RestController
@RequestMapping
@RequiredArgsConstructor
public class MetaImageController extends BaseApi  {

    private final MetaImageService metaImageService;

    @Operation(summary = "新增")
    @SaCheckPermission(value = "meta.image.create")
    @PostMapping("/meta-image/create")
    public ApiResult<Void> create(@RequestBody MetaImageCreateDTO dto) {
        metaImageService.create(dto);
        return ApiResult.success();
    }

    @Operation(summary = "修改")
    @SaCheckPermission(value = "meta.image.update")
    @PutMapping("/meta-image/update")
    public ApiResult<Void> update(@RequestBody MetaImageUpdateDTO dto) {
        metaImageService.update(dto);
        return ApiResult.success();
    }

    @Operation(summary = "删除")
    @SaCheckPermission(value = "meta.image.remove")
    @DeleteMapping("/meta-image/remove")
    public ApiResult<Void> remove(@RequestBody SelectIdsDTO dto) {
        metaImageService.remove(dto);
        return ApiResult.success();
    }

    @Operation(summary = "列表查询")
    // @SaCheckPermission(value = "meta.image.query_table")
    @GetMapping("/meta-image/list")
    public ApiResult<PageResult<MetaImageVO>> list(MetaImageListDTO dto) {
        return ApiPageResult.success(metaImageService.page(dto));
    }

    @Operation(summary = "详情")
    @SaCheckPermission(value = "meta.image.query_table")
    @GetMapping("/meta-image/detail")
    public ApiResult<MetaImageVO> detail(@RequestParam Object id) {
        return ApiResult.success(metaImageService.detail(id));
    }

    @Operation(summary = "导入")
    @Parameters({
      @Parameter(name = "file", description = "上传文件", schema = @Schema(type = "string", format = "binary"), required = true),
    })
    @SaCheckPermission(value = "meta.image.import")
    @PostMapping("/meta-image/import")
    public void importExcel(@ModelAttribute ImportExcelDTO dto) {
        metaImageService.importExcel(dto);
    }

    @Operation(summary = "导出")
    @SaCheckPermission(value = "meta.image.export")
    @PostMapping("/meta-image/export")
    public void exportExcel(@RequestBody MetaImageListDTO dto, HttpServletResponse response) {
        metaImageService.exportExcel(dto, response);
    }
}
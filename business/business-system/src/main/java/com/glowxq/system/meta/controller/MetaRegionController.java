package com.glowxq.system.meta.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.glowxq.core.common.api.BaseApi;
import com.glowxq.core.common.entity.*;
import com.glowxq.system.meta.pojo.dto.MetaRegionCreateDTO;
import com.glowxq.system.meta.pojo.dto.MetaRegionListDTO;
import com.glowxq.system.meta.pojo.dto.MetaRegionUpdateDTO;
import com.glowxq.system.meta.pojo.po.MetaRegion;
import com.glowxq.system.meta.pojo.vo.MetaRegionVO;
import com.glowxq.system.meta.service.MetaRegionService;
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
 * meta/区域地址 Api
 *
 * @author glowxq
 * @since 2025-06-20
 */
@Tag(name = "区域地址")
@RestController
@RequestMapping
@RequiredArgsConstructor
public class MetaRegionController extends BaseApi {

    private final MetaRegionService metaRegionService;

    @Operation(summary = "新增")
    @SaCheckPermission(value = "meta.region.create")
    @PostMapping("/meta-region/create")
    public ApiResult<Void> create(@RequestBody MetaRegionCreateDTO dto) {
        metaRegionService.create(dto);
        return ApiResult.success();
    }

    @Operation(summary = "修改")
    @SaCheckPermission(value = "meta.region.update")
    @PutMapping("/meta-region/update")
    public ApiResult<Void> update(@RequestBody MetaRegionUpdateDTO dto) {
        metaRegionService.update(dto);
        return ApiResult.success();
    }

    @Operation(summary = "删除")
    @SaCheckPermission(value = "meta.region.remove")
    @DeleteMapping("/meta-region/remove")
    public ApiResult<Void> remove(@RequestBody SelectIdsDTO dto) {
        metaRegionService.remove(dto);
        return ApiResult.success();
    }

    @Operation(summary = "列表查询")
    @SaCheckPermission(value = "meta.region.query_table")
    @GetMapping("/meta-region/list")
    public ApiResult<PageResult<MetaRegionVO>> list(MetaRegionListDTO dto) {
        return ApiPageResult.success(metaRegionService.page(dto));
    }

    @Operation(summary = "列表查询")
    @GetMapping("/meta-region/listAll")
    public ApiResult<List<MetaRegion>> listAll() {
        return ApiResult.success(metaRegionService.listByLevel(2));
    }

    @Operation(summary = "详情")
    @SaCheckPermission(value = "meta.region.query_table")
    @GetMapping("/meta-region/detail")
    public ApiResult<MetaRegionVO> detail(@RequestParam Long id) {
        return ApiResult.success(metaRegionService.detail(id));
    }

    @Operation(summary = "导入")
    @Parameters({
            @Parameter(name = "file", description = "上传文件", schema = @Schema(type = "string", format = "binary"), required = true),
    })
    @SaCheckPermission(value = "meta.region.import")
    @PostMapping("/meta-region/import")
    public void importExcel(@ModelAttribute ImportExcelDTO dto) {
        metaRegionService.importExcel(dto);
    }

    @Operation(summary = "导出")
    @SaCheckPermission(value = "meta.region.export")
    @PostMapping("/meta-region/export")
    public void exportExcel(@RequestBody MetaRegionListDTO dto, HttpServletResponse response) {
        metaRegionService.exportExcel(dto, response);
    }
}
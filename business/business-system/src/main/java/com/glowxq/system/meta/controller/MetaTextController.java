package com.glowxq.system.meta.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.glowxq.core.common.api.BaseApi;
import com.glowxq.core.common.entity.*;
import com.glowxq.system.meta.pojo.dto.MetaTextCreateDTO;
import com.glowxq.system.meta.pojo.dto.MetaTextListDTO;
import com.glowxq.system.meta.pojo.dto.MetaTextUpdateDTO;
import com.glowxq.system.meta.pojo.po.MetaText;
import com.glowxq.system.meta.pojo.vo.MetaTextVO;
import com.glowxq.system.meta.service.MetaTextService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * meta/文本 Api
 *
 * @author glowxq
 * @since 2025-04-23
 */
@Tag(name =  "文本")
@RestController
@RequestMapping
@RequiredArgsConstructor
public class MetaTextController extends BaseApi  {

    private final MetaTextService metaTextService;

    @Operation(summary = "根据key获取")
    @GetMapping("/meta-text/getByKey")
    public ApiResult<MetaText> getByKey(@RequestParam String key) {
        return ApiResult.success(metaTextService.getByKey(key));
    }

    @Operation(summary = "新增")
    @SaCheckPermission(value = "meta.text.create")
    @PostMapping("/meta-text/create")
    public ApiResult<Void> create(@RequestBody MetaTextCreateDTO dto) {
        metaTextService.create(dto);
        return ApiResult.success();
    }

    @Operation(summary = "修改")
    @SaCheckPermission(value = "meta.text.update")
    @PutMapping("/meta-text/update")
    public ApiResult<Void> update(@RequestBody MetaTextUpdateDTO dto) {
        metaTextService.update(dto);
        return ApiResult.success();
    }

    @Operation(summary = "删除")
    @SaCheckPermission(value = "meta.text.remove")
    @DeleteMapping("/meta-text/remove")
    public ApiResult<Void> remove(@RequestBody SelectIdsDTO dto) {
        metaTextService.remove(dto);
        return ApiResult.success();
    }

    @Operation(summary = "列表查询")
    // @SaCheckPermission(value = "meta.text.query_table")
    @GetMapping("/meta-text/list")
    public ApiResult<PageResult<MetaTextVO>> list(MetaTextListDTO dto) {
        return ApiPageResult.success(metaTextService.page(dto));
    }

    @Operation(summary = "详情")
    @SaCheckPermission(value = "meta.text.query_table")
    @GetMapping("/meta-text/detail")
    public ApiResult<MetaTextVO> detail(@RequestParam Object id) {
        return ApiResult.success(metaTextService.detail(id));
    }

    @Operation(summary = "导入")
    @Parameters({
      @Parameter(name = "file", description = "上传文件", schema = @Schema(type = "string", format = "binary"), required = true),
    })
    @SaCheckPermission(value = "meta.text.import")
    @PostMapping("/meta-text/import")
    public void importExcel(@ModelAttribute ImportExcelDTO dto) {
        metaTextService.importExcel(dto);
    }

    @Operation(summary = "导出")
    @SaCheckPermission(value = "meta.text.export")
    @PostMapping("/meta-text/export")
    public void exportExcel(@RequestBody MetaTextListDTO dto, HttpServletResponse response) {
        metaTextService.exportExcel(dto, response);
    }
}
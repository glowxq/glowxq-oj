package com.glowxq.generator.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.glowxq.core.common.constant.GlobalConstant;
import com.glowxq.core.common.entity.ApiPageResult;
import com.glowxq.core.common.entity.ApiResult;
import com.glowxq.core.common.entity.PageResult;
import com.glowxq.core.util.FileUtils;
import com.glowxq.generator.pojo.dto.DbTableQueryDTO;
import com.glowxq.generator.pojo.dto.ImportTableDTO;
import com.glowxq.generator.pojo.dto.SelectTablesDTO;
import com.glowxq.generator.pojo.po.GeneratorTable;
import com.glowxq.generator.pojo.vo.GenCheckedInfoVO;
import com.glowxq.generator.pojo.vo.GeneratorDetailVO;
import com.glowxq.generator.pojo.vo.GeneratorPreviewVO;
import com.glowxq.generator.service.GeneratorTableService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/**
 * <p>
 * 代码生成业务表 前端控制器
 * </p>
 *
 * @author glowxq
 * @since 2023-11-27
 */
@Tag(name = "代码生成")
@RestController
@RequestMapping("/generator")
@RequiredArgsConstructor
public class GeneratorTableController {

    private final GeneratorTableService generatorTableService;

    @SaCheckPermission(value = "generator.import", orRole = GlobalConstant.SUPER_ROLE)
    @Operation(summary = "导入指定表")
    @PostMapping("import")
    public ApiResult<Void> importTables(@RequestBody ImportTableDTO dto) {
        generatorTableService.importTable(dto);
        return ApiResult.success();
    }

    @SaCheckPermission(value = "generator.import", orRole = GlobalConstant.SUPER_ROLE)
    @Operation(summary = "查询要导入的表列表（排除已经导入的表）")
    @GetMapping("schema/list")
    public ApiResult<PageResult<GeneratorTable>> schemaList(DbTableQueryDTO dto) {
        return ApiPageResult.success(generatorTableService.selectDbTableNotInImport(dto));
    }

    @SaCheckPermission(value = "generator.import", orRole = GlobalConstant.SUPER_ROLE)
    @Operation(summary = "查询已经导入的表列表")
    @GetMapping("/list")
    public ApiResult<PageResult<GeneratorTable>> list(DbTableQueryDTO dto) {
        return ApiPageResult.success(generatorTableService.selectDbTableByImport(dto));
    }

    @Operation(summary = "代码生成参数详情")
    @GetMapping("/{tableName}")
    public ApiResult<GeneratorDetailVO> detail(@PathVariable String tableName) {
        return ApiResult.success(generatorTableService.detail(tableName));
    }

    @SaCheckPermission(value = "generator.update", orRole = GlobalConstant.SUPER_ROLE)
    @Operation(summary = "更新代码生成配置")
    @PutMapping()
    public ApiResult<Void> updateGenerator(@RequestBody GeneratorDetailVO dto) {
        generatorTableService.updateGeneratorSetting(dto);
        return ApiResult.success();
    }

    @SaCheckPermission(value = "generator.generator", orRole = GlobalConstant.SUPER_ROLE)
    @Operation(summary = "代码生成")
    @PostMapping("/generator/{tableName}")
    public ApiResult<List<String>> generator(@PathVariable String tableName) throws IOException {
        return ApiResult.success(generatorTableService.generator(tableName));
    }

    @SaCheckPermission(value = "generator.remove", orRole = GlobalConstant.SUPER_ROLE)
    @Operation(summary = "删除导入的表")
    @DeleteMapping
    public ApiResult<Void> remove(@RequestBody SelectTablesDTO dto) {
        generatorTableService.remove(dto);
        return ApiResult.success();
    }

    @SaCheckPermission(value = "generator.zip", orRole = GlobalConstant.SUPER_ROLE)
    @Operation(summary = "zip下载")
    @PostMapping("zip")
    public void downloadZip(@RequestBody SelectTablesDTO dto, HttpServletResponse response) throws IOException {
        byte[] data = generatorTableService.downloadZip(dto);
        OutputStream outputStream = FileUtils.getOutputStream(response, "glowxq-admin-" + dto.getTableNames() + ".zip", data.length);
        // 将字节数组写入输出流
        IOUtils.write(data, outputStream);
        response.flushBuffer();
    }

    @SaCheckPermission(value = "generator.preview", orRole = GlobalConstant.SUPER_ROLE)
    @Operation(summary = "预览")
    @GetMapping("preview/{tableName}")
    public ApiResult<List<GeneratorPreviewVO>> preview(@PathVariable String tableName) throws IOException {
        return ApiResult.success(generatorTableService.preview(tableName));
    }

    @SaCheckPermission(value = "generator.generator", orRole = GlobalConstant.SUPER_ROLE)
    @Operation(summary = "验证磁盘")
    @GetMapping("check/{tableName}")
    public ApiResult<GenCheckedInfoVO> check(@PathVariable String tableName) {
        return ApiResult.success(generatorTableService.checkDist(tableName));
    }

}

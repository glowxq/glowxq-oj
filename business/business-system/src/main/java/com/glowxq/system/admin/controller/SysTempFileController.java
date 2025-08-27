package com.glowxq.system.admin.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.glowxq.core.common.annotation.DebounceIgnore;
import com.glowxq.core.common.annotation.OperationLog;
import com.glowxq.core.common.entity.ApiPageResult;
import com.glowxq.core.common.entity.ApiResult;
import com.glowxq.core.common.entity.PageResult;
import com.glowxq.core.common.entity.SelectIdsDTO;
import com.glowxq.core.common.enums.ModuleEnum;
import com.glowxq.oss.UploadResult;
import com.glowxq.system.admin.pojo.dto.systempfile.SysTempFileCreateDTO;
import com.glowxq.system.admin.pojo.dto.systempfile.SysTempFileListDTO;
import com.glowxq.system.admin.pojo.dto.systempfile.SysTempFileUpdateDTO;
import com.glowxq.system.admin.pojo.vo.systempfile.SysTempFileVO;
import com.glowxq.system.admin.service.SysTempFileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * <p>
 * 模版文件表 Controller
 * </p>
 *
 * @author glowxq
 * @since 2024-12-05
 */
@Tag(name = "模版文件表")
@RestController
@RequestMapping("sys-temp-file")
@RequiredArgsConstructor
public class SysTempFileController {

    private final SysTempFileService sysTempFileService;

    @Operation(summary = "新增模版文件")
    @SaCheckPermission(value = "sys.temp.file.create")
    @PostMapping
    @OperationLog(module = ModuleEnum.System)
    public ApiResult<Void> create(@RequestBody SysTempFileCreateDTO dto) {
        sysTempFileService.create(dto);
        return ApiResult.success();
    }

    @Operation(summary = "修改模版文件")
    @SaCheckPermission(value = "sys.temp.file.update")
    @PutMapping
    @OperationLog(module = ModuleEnum.System)
    public ApiResult<Void> update(@RequestBody SysTempFileUpdateDTO dto) {
        sysTempFileService.update(dto);
        return ApiResult.success();
    }

    @Operation(summary = "删除模版文件")
    @SaCheckPermission(value = "sys.temp.file.remove")
    @DeleteMapping
    @OperationLog(module = ModuleEnum.System)
    public ApiResult<Void> remove(@RequestBody SelectIdsDTO dto) {
        sysTempFileService.remove(dto);
        return ApiResult.success();
    }

    @Operation(summary = "列表查询")
    @SaCheckPermission(value = "sys.temp.file.query_table")
    @GetMapping
    public ApiResult<PageResult<SysTempFileVO>> list(SysTempFileListDTO dto) {
        return ApiPageResult.success(sysTempFileService.page(dto));
    }

    @Operation(summary = "详情")
    @SaCheckPermission(value = "sys.temp.file.query_table")
    @GetMapping("/{id}")
    public ApiResult<SysTempFileVO> detail(@PathVariable Object id) {
        return ApiResult.success(sysTempFileService.detail(id));
    }

    @DebounceIgnore
    @Operation(summary = "上传模板文件")
    @PostMapping("/upload")
    @OperationLog(module = ModuleEnum.System)
    public ApiResult<UploadResult> upload(@RequestParam MultipartFile file) {
        return ApiResult.success(sysTempFileService.uploadFile(file));
    }

}
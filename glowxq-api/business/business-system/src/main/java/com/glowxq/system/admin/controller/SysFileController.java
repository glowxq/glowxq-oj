package com.glowxq.system.admin.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import com.glowxq.core.common.annotation.DebounceIgnore;
import com.glowxq.core.common.annotation.OperationLog;
import com.glowxq.core.common.entity.ApiPageResult;
import com.glowxq.core.common.entity.ApiResult;
import com.glowxq.core.common.entity.PageResult;
import com.glowxq.core.common.enums.ModuleEnum;
import com.glowxq.oss.UploadResult;
import com.glowxq.system.admin.pojo.dto.sysfile.GeneratePreUrlDTO;
import com.glowxq.system.admin.pojo.dto.sysfile.SysFileListDTO;
import com.glowxq.system.admin.pojo.po.SysFile;
import com.glowxq.system.admin.service.SysFileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件管理
 *
 * @author glowxq
 * @since 2023-08-31
 */
@Tag(name = "系统公共文件管理")
@RequiredArgsConstructor
@RestController
@SaIgnore
@RequestMapping("/sys-file")
public class SysFileController {

    private final SysFileService sysFileService;

    @Operation(summary = "列表查询")
    @GetMapping
    public ApiPageResult<PageResult<SysFile>> list(SysFileListDTO dto) {
        return ApiPageResult.success(sysFileService.fileList(dto));
    }

    @DebounceIgnore
    @Operation(summary = "上传文件")
    @PostMapping("/upload")
    @OperationLog(module = ModuleEnum.System)
    public ApiResult<UploadResult> upload(@RequestParam MultipartFile file, @RequestParam(value = "dirTag") String dirTag) {
        return ApiResult.success(sysFileService.uploadFile(file, dirTag));
    }


    @DebounceIgnore
    @Operation(summary = "获取上传的预签名")
    @PostMapping("/generatePreUrl")
    public ApiResult<String> generatePreUrl(@RequestBody GeneratePreUrlDTO generatePreUrlDTO) {
        return ApiResult.success(sysFileService.generatePreUrl(generatePreUrlDTO.getName()));
    }

    @DebounceIgnore
    @Operation(summary = "初始化bucket")
    @PostMapping("/initBucket")
    public ApiResult<String> initBucket() {
        sysFileService.initBucket();
        return ApiResult.success();
    }


}

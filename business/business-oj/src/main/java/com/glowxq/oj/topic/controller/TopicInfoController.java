package com.glowxq.oj.topic.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.glowxq.core.common.annotation.OperationLog;
import com.glowxq.core.common.api.BaseApi;
import com.glowxq.core.common.entity.*;
import com.glowxq.core.common.enums.ModuleEnum;
import com.glowxq.oj.topic.pojo.dto.TopicInfoCreateDTO;
import com.glowxq.oj.topic.pojo.dto.TopicInfoListDTO;
import com.glowxq.oj.topic.pojo.dto.TopicInfoUpdateDTO;
import com.glowxq.oj.topic.pojo.vo.TopicInfoVO;
import com.glowxq.oj.topic.service.TopicInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * topic/主题数据 Api
 *
 * @author glowxq
 * @since 2025-04-12
 */
@Tag(name = "主题数据")
@RestController
@RequestMapping("/topic-info")
@RequiredArgsConstructor
public class TopicInfoController extends BaseApi {

    private final TopicInfoService topicInfoService;

    @Operation(summary = "开始进入主题")
    @SaCheckPermission(value = "topic.info.create")
    @PostMapping
    @OperationLog(module = ModuleEnum.Topic)
    public ApiResult<Void> create(@RequestBody TopicInfoCreateDTO dto) {
        topicInfoService.create(dto);
        return ApiResult.success();
    }

    @Operation(summary = "修改主题数据")
    @SaCheckPermission(value = "topic.info.update")
    @PutMapping
    @OperationLog(module = ModuleEnum.Topic)
    public ApiResult<Void> update(@RequestBody TopicInfoUpdateDTO dto) {
        topicInfoService.update(dto);
        return ApiResult.success();
    }

    @Operation(summary = "删除主题数据")
    @SaCheckPermission(value = "topic.info.remove")
    @DeleteMapping
    @OperationLog(module = ModuleEnum.Topic)
    public ApiResult<Void> remove(@RequestBody SelectIdsDTO dto) {
        topicInfoService.remove(dto);
        return ApiResult.success();
    }

    @Operation(summary = "列表查询")
    @SaCheckPermission(value = "topic.info.query_table")
    @GetMapping
    public ApiResult<PageResult<TopicInfoVO>> list(TopicInfoListDTO dto) {
        return ApiPageResult.success(topicInfoService.page(dto));
    }

    @Operation(summary = "详情")
    @SaCheckPermission(value = "topic.info.query_table")
    @GetMapping("/{id}")
    public ApiResult<TopicInfoVO> detail(@PathVariable Object id) {
        return ApiResult.success(topicInfoService.detail(id));
    }

    @Operation(summary = "导入主题数据")
    @Parameters({
            @Parameter(name = "file", description = "上传文件", schema = @Schema(type = "string", format = "binary"), required = true),
    })
    @SaCheckPermission(value = "topic.info.import")
    @PostMapping("/import")
    @OperationLog(module = ModuleEnum.Topic)
    public void importExcel(@ModelAttribute ImportExcelDTO dto) {
        topicInfoService.importExcel(dto);
    }

    @Operation(summary = "导出主题数据")
    @SaCheckPermission(value = "topic.info.export")
    @PostMapping("/export")
    @OperationLog(module = ModuleEnum.Topic)
    public void exportExcel(@RequestBody TopicInfoListDTO dto, HttpServletResponse response) {
        topicInfoService.exportExcel(dto, response);
    }
}
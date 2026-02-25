package com.glowxq.oj.topic.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.glowxq.core.common.annotation.OperationLog;
import com.glowxq.core.common.api.BaseApi;
import com.glowxq.core.common.entity.*;
import com.glowxq.core.common.enums.ModuleEnum;
import com.glowxq.oj.topic.pojo.dto.*;
import com.glowxq.oj.topic.pojo.po.TopicInfo;
import com.glowxq.oj.topic.pojo.po.TopicSubmit;
import com.glowxq.oj.topic.pojo.vo.TopicVO;
import com.glowxq.oj.topic.service.TopicService;
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
 * topic/主题 Api
 *
 * @author glowxq
 * @since 2025-04-12
 */
@Tag(name = "主题")
@RestController
@RequestMapping
@RequiredArgsConstructor
public class TopicController extends BaseApi {

    private final TopicService topicService;

    @Operation(summary = "新增主题")
    @SaCheckPermission(value = "topic.create")
    @PostMapping("/topic")
    @OperationLog(module = ModuleEnum.Topic)
    public ApiResult<Void> create(@RequestBody TopicCreateDTO dto) {
        topicService.create(dto);
        return ApiResult.success();
    }

    @Operation(summary = "修改主题")
    @SaCheckPermission(value = "topic.update")
    @PutMapping("/topic")
    @OperationLog(module = ModuleEnum.Topic)
    public ApiResult<Void> update(@RequestBody TopicUpdateDTO dto) {
        topicService.update(dto);
        return ApiResult.success();
    }

    @Operation(summary = "删除主题")
    @SaCheckPermission(value = "topic.remove")
    @DeleteMapping("/topic")
    @OperationLog(module = ModuleEnum.Topic)
    public ApiResult<Void> remove(@RequestBody SelectIdsDTO dto) {
        topicService.remove(dto);
        return ApiResult.success();
    }

    @Operation(summary = "列表查询")
    @SaCheckPermission(value = "topic.query_table")
    @PostMapping("/topic/topicPage")
    public ApiResult<PageResult<TopicVO>> list(@RequestBody TopicListDTO dto) {
        return ApiPageResult.success(topicService.page(dto));
    }

    @Operation(summary = "获取主题排行榜")
    @SaCheckPermission(value = "topic.query_table")
    @PostMapping("/topic/topicRank")
    public ApiResult<List<TopicInfo>> topicRank(@RequestBody TopicRankDTO dto) {
        return ApiResult.success(topicService.topicRank(dto));
    }

    @Operation(summary = "获取主题做题情况")
    @SaCheckPermission(value = "topic.query_table")
    @PostMapping("/topic/topicSubmitInfo")
    public ApiResult<List<TopicSubmit>> topicSubmitInfo(@RequestBody TopicSubmitInfoDTO topicSubmitInfoDTO) {
        List<TopicSubmit> topicSubmits = topicService.submitTopicInfo(topicSubmitInfoDTO.getTopicId());
        return ApiResult.success(topicSubmits);
    }

    @Operation(summary = "详情")
    @SaCheckPermission(value = "topic.query_table")
    @GetMapping("/topic/{id}")
    public ApiResult<TopicVO> detail(@PathVariable Long id) {
        return ApiResult.success(topicService.detail(id));
    }

    @Operation(summary = "导入主题")
    @Parameters({
            @Parameter(name = "file", description = "上传文件", schema = @Schema(type = "string", format = "binary"), required = true),
    })
    @SaCheckPermission(value = "topic.import")
    @PostMapping("/topic/import")
    @OperationLog(module = ModuleEnum.Topic)
    public void importExcel(@ModelAttribute ImportExcelDTO dto) {
        topicService.importExcel(dto);
    }

    @Operation(summary = "导出主题")
    @SaCheckPermission(value = "topic.export")
    @PostMapping("/topic/export")
    @OperationLog(module = ModuleEnum.Topic)
    public void exportExcel(@RequestBody TopicListDTO dto, HttpServletResponse response) {
        topicService.exportExcel(dto, response);
    }
}
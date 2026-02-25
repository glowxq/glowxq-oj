package com.glowxq.oj.topic.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.glowxq.core.common.annotation.OperationLog;
import com.glowxq.core.common.api.BaseApi;
import com.glowxq.core.common.entity.*;
import com.glowxq.core.common.enums.ModuleEnum;
import com.glowxq.oj.topic.pojo.dto.TopicSubmitCreateDTO;
import com.glowxq.oj.topic.pojo.dto.TopicSubmitListDTO;
import com.glowxq.oj.topic.pojo.dto.TopicSubmitUpdateDTO;
import com.glowxq.oj.topic.pojo.vo.TopicSubmitVO;
import com.glowxq.oj.topic.service.TopicSubmitService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * topic/主题测评记录 Api
 *
 * @author glowxq
 * @since 2025-04-12
 */
@Tag(name =  "主题测评记录")
@RestController
@RequestMapping("/topic-submit")
@RequiredArgsConstructor
public class TopicSubmitController extends BaseApi  {

    private final TopicSubmitService topicSubmitService;

    @Operation(summary = "新增主题测评记录")
    @SaCheckPermission(value = "topic.submit.create")
    @PostMapping
    @OperationLog(module = ModuleEnum.Topic)
    public ApiResult<Void> create(@RequestBody TopicSubmitCreateDTO dto) {
        topicSubmitService.create(dto);
        return ApiResult.success();
    }

    @Operation(summary = "修改主题测评记录")
    @SaCheckPermission(value = "topic.submit.update")
    @PutMapping
    @OperationLog(module = ModuleEnum.Topic)
    public ApiResult<Void> update(@RequestBody TopicSubmitUpdateDTO dto) {
        topicSubmitService.update(dto);
        return ApiResult.success();
    }

    @Operation(summary = "删除主题测评记录")
    @SaCheckPermission(value = "topic.submit.remove")
    @DeleteMapping
    @OperationLog(module = ModuleEnum.Topic)
    public ApiResult<Void> remove(@RequestBody SelectIdsDTO dto) {
        topicSubmitService.remove(dto);
        return ApiResult.success();
    }

    @Operation(summary = "列表查询")
    @SaCheckPermission(value = "topic.submit.query_table")
    @GetMapping
    public ApiResult<PageResult<TopicSubmitVO>> list(TopicSubmitListDTO dto) {
        return ApiPageResult.success(topicSubmitService.page(dto));
    }

    @Operation(summary = "详情")
    @SaCheckPermission(value = "topic.submit.query_table")
    @GetMapping("/{id}")
    public ApiResult<TopicSubmitVO> detail(@PathVariable Object id) {
        return ApiResult.success(topicSubmitService.detail(id));
    }

    @Operation(summary = "导入主题测评记录")
    @Parameters({
      @Parameter(name = "file", description = "上传文件", schema = @Schema(type = "string", format = "binary"), required = true),
    })
    @SaCheckPermission(value = "topic.submit.import")
    @PostMapping("/import")
    @OperationLog(module = ModuleEnum.Topic)
    public void importExcel(@ModelAttribute ImportExcelDTO dto) {
        topicSubmitService.importExcel(dto);
    }

    @Operation(summary = "导出主题测评记录")
    @SaCheckPermission(value = "topic.submit.export")
    @PostMapping("/export")
    @OperationLog(module = ModuleEnum.Topic)
    public void exportExcel(@RequestBody TopicSubmitListDTO dto, HttpServletResponse response) {
        topicSubmitService.exportExcel(dto, response);
    }
}
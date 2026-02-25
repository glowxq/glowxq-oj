package com.glowxq.oj.topic.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.glowxq.core.common.annotation.OperationLog;
import com.glowxq.core.common.api.BaseApi;
import com.glowxq.core.common.entity.*;
import com.glowxq.core.common.enums.ModuleEnum;
import com.glowxq.oj.topic.pojo.dto.TopicProblemCreateDTO;
import com.glowxq.oj.topic.pojo.dto.TopicProblemListDTO;
import com.glowxq.oj.topic.pojo.dto.TopicProblemUpdateDTO;
import com.glowxq.oj.topic.pojo.vo.TopicProblemVO;
import com.glowxq.oj.topic.service.TopicProblemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * topic/主题题目 Api
 *
 * @author glowxq
 * @since 2025-04-12
 */
@Tag(name =  "主题题目")
@RestController
@RequestMapping("/topic-problem")
@RequiredArgsConstructor
public class TopicProblemController extends BaseApi  {

    private final TopicProblemService topicProblemService;

    @Operation(summary = "新增主题题目")
    @SaCheckPermission(value = "topic.problem.create")
    @PostMapping
    @OperationLog(module = ModuleEnum.Topic)
    public ApiResult<Void> create(@RequestBody TopicProblemCreateDTO dto) {
        topicProblemService.create(dto);
        return ApiResult.success();
    }

    @Operation(summary = "修改主题题目")
    @SaCheckPermission(value = "topic.problem.update")
    @PutMapping
    @OperationLog(module = ModuleEnum.Topic)
    public ApiResult<Void> update(@RequestBody TopicProblemUpdateDTO dto) {
        topicProblemService.update(dto);
        return ApiResult.success();
    }

    @Operation(summary = "删除主题题目")
    @SaCheckPermission(value = "topic.problem.remove")
    @DeleteMapping
    @OperationLog(module = ModuleEnum.Topic)
    public ApiResult<Void> remove(@RequestBody SelectIdsDTO dto) {
        topicProblemService.remove(dto);
        return ApiResult.success();
    }

    @Operation(summary = "列表查询")
    @SaCheckPermission(value = "topic.problem.query_table")
    @GetMapping
    public ApiResult<PageResult<TopicProblemVO>> list(TopicProblemListDTO dto) {
        return ApiPageResult.success(topicProblemService.page(dto));
    }

    @Operation(summary = "详情")
    @SaCheckPermission(value = "topic.problem.query_table")
    @GetMapping("/{id}")
    public ApiResult<TopicProblemVO> detail(@PathVariable Object id) {
        return ApiResult.success(topicProblemService.detail(id));
    }

    @Operation(summary = "导入主题题目")
    @Parameters({
      @Parameter(name = "file", description = "上传文件", schema = @Schema(type = "string", format = "binary"), required = true),
    })
    @SaCheckPermission(value = "topic.problem.import")
    @PostMapping("/import")
    @OperationLog(module = ModuleEnum.Topic)
    public void importExcel(@ModelAttribute ImportExcelDTO dto) {
        topicProblemService.importExcel(dto);
    }

    @Operation(summary = "导出主题题目")
    @SaCheckPermission(value = "topic.problem.export")
    @PostMapping("/export")
    @OperationLog(module = ModuleEnum.Topic)
    public void exportExcel(@RequestBody TopicProblemListDTO dto, HttpServletResponse response) {
        topicProblemService.exportExcel(dto, response);
    }
}
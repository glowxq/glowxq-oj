package com.glowxq.oj.course.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.glowxq.core.common.annotation.OperationLog;
import com.glowxq.core.common.api.BaseApi;
import com.glowxq.core.common.entity.*;
import com.glowxq.core.common.enums.ModuleEnum;
import com.glowxq.oj.course.pojo.dto.CourseCreateDTO;
import com.glowxq.oj.course.pojo.dto.CourseListDTO;
import com.glowxq.oj.course.pojo.dto.CourseUpdateDTO;
import com.glowxq.oj.course.pojo.vo.CourseVO;
import com.glowxq.oj.course.service.CourseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * operation/课程 Api
 *
 * @author glowxq
 * @since 2025-03-30
 */
@Tag(name = "课程")
@RestController
@RequestMapping("/course")
@RequiredArgsConstructor
public class CourseController extends BaseApi {

    private final CourseService courseService;

    @Operation(summary = "新增课程")
    @SaCheckPermission(value = "course.create")
    @PostMapping
    @OperationLog(module = ModuleEnum.Course)
    public ApiResult<Void> create(@RequestBody CourseCreateDTO dto) {
        courseService.create(dto);
        return ApiResult.success();
    }

    @Operation(summary = "修改课程")
    @SaCheckPermission(value = "course.update")
    @PutMapping
    @OperationLog(module = ModuleEnum.Course)
    public ApiResult<Void> update(@RequestBody CourseUpdateDTO dto) {
        courseService.update(dto);
        return ApiResult.success();
    }

    @Operation(summary = "删除课程")
    @SaCheckPermission(value = "course.remove")
    @DeleteMapping
    @OperationLog(module = ModuleEnum.Course)
    public ApiResult<Void> remove(@RequestBody SelectIdsDTO dto) {
        courseService.remove(dto);
        return ApiResult.success();
    }

    @Operation(summary = "列表查询")
    @SaCheckPermission(value = "course.query_table")
    @PostMapping("/list")
    public ApiResult<PageResult<CourseVO>> list(@RequestBody @Validated CourseListDTO dto) {
        return ApiPageResult.success(courseService.page(dto));
    }

    @Operation(summary = "详情")
    @SaCheckPermission(value = "course.query_table")
    @GetMapping("/{id}")
    public ApiResult<CourseVO> detail(@PathVariable Object id) {
        return ApiResult.success(courseService.detail(id));
    }

    @Operation(summary = "导入课程")
    @Parameters({
            @Parameter(name = "file", description = "上传文件", schema = @Schema(type = "string", format = "binary"), required = true),
    })
    @SaCheckPermission(value = "course.import")
    @PostMapping("/import")
    @OperationLog(module = ModuleEnum.Course)
    public void importExcel(@ModelAttribute ImportExcelDTO dto) {
        courseService.importExcel(dto);
    }

    @Operation(summary = "导出课程")
    @SaCheckPermission(value = "course.export")
    @PostMapping("/export")
    @OperationLog(module = ModuleEnum.Course)
    public void exportExcel(@RequestBody CourseListDTO dto, HttpServletResponse response) {
        courseService.exportExcel(dto, response);
    }
}
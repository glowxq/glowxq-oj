package com.glowxq.oj.user.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.glowxq.core.common.api.BaseApi;
import com.glowxq.core.common.entity.*;
import com.glowxq.oj.user.pojo.dto.UserProblemCreateDTO;
import com.glowxq.oj.user.pojo.dto.UserProblemListDTO;
import com.glowxq.oj.user.pojo.dto.UserProblemUpdateDTO;
import com.glowxq.oj.user.pojo.vo.UserProblemVO;
import com.glowxq.oj.user.service.UserProblemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * user/用户题目数据 Api
 *
 * @author glowxq
 * @since 2025-04-03
 */
@Tag(name = "用户题目数据")
@RestController
@RequestMapping("/user-problem")
@RequiredArgsConstructor
public class UserProblemController extends BaseApi {

    private final UserProblemService userProblemService;

    @Operation(summary = "新增")
    @SaCheckPermission(value = "user.problem.create")
    @PostMapping
    public ApiResult<Void> create(@RequestBody UserProblemCreateDTO dto) {
        userProblemService.create(dto);
        return ApiResult.success();
    }

    @Operation(summary = "修改")
    @SaCheckPermission(value = "user.problem.update")
    @PutMapping
    public ApiResult<Void> update(@RequestBody UserProblemUpdateDTO dto) {
        userProblemService.update(dto);
        return ApiResult.success();
    }

    @Operation(summary = "删除")
    @SaCheckPermission(value = "user.problem.remove")
    @DeleteMapping
    public ApiResult<Void> remove(@RequestBody SelectIdsDTO dto) {
        userProblemService.remove(dto);
        return ApiResult.success();
    }

    @Operation(summary = "列表查询")
    @GetMapping
    public ApiResult<PageResult<UserProblemVO>> list(UserProblemListDTO dto) {
        return ApiPageResult.success(userProblemService.page(dto));
    }

    // @Operation(summary = "列表查询")
    // @SaCheckPermission(value = "user.problem.query_table")
    // @GetMapping("/ranking")
    // public ApiResult<PageResult<UserProblemVO>> ranking(RankingDTO dto) {
    //     return ApiPageResult.success(userProblemService.ranking(dto));
    // }

    @Operation(summary = "详情")
    @SaCheckPermission(value = "user.problem.query_table")
    @GetMapping("/{id}")
    public ApiResult<UserProblemVO> detail(@PathVariable Object id) {
        return ApiResult.success(userProblemService.detail(id));
    }

    @Operation(summary = "导入")
    @Parameters({
            @Parameter(name = "file", description = "上传文件", schema = @Schema(type = "string", format = "binary"), required = true),
    })
    @SaCheckPermission(value = "user.problem.import")
    @PostMapping("/import")
    public void importExcel(@ModelAttribute ImportExcelDTO dto) {
        userProblemService.importExcel(dto);
    }

    @Operation(summary = "导出")
    @SaCheckPermission(value = "user.problem.export")
    @PostMapping("/export")
    public void exportExcel(@RequestBody UserProblemListDTO dto, HttpServletResponse response) {
        userProblemService.exportExcel(dto, response);
    }
}
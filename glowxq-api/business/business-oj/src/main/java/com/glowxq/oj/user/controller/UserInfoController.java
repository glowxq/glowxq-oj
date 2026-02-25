package com.glowxq.oj.user.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaIgnore;
import com.glowxq.core.common.api.BaseApi;
import com.glowxq.core.common.entity.*;
import com.glowxq.oj.user.pojo.dto.*;
import com.glowxq.oj.user.pojo.po.UserInfo;
import com.glowxq.oj.user.pojo.vo.UserInfoVO;
import com.glowxq.oj.user.service.UserInfoService;
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
 * user/用户信息 Api
 *
 * @author glowxq
 * @since 2025-06-23
 */
@Tag(name = "用户信息")
@RestController
@RequestMapping
@RequiredArgsConstructor
public class UserInfoController extends BaseApi {

    private final UserInfoService userInfoService;

    @SaIgnore
    @Operation(summary = "注册")
    @PostMapping("/user-info/register")
    public ApiResult<Void> register(@RequestBody UserInfoRegisterDTO dto) {
        userInfoService.register(dto);
        return ApiResult.success();
    }

    @SaIgnore
    @PostMapping("/user-info/rank")
    public ApiResult<List<UserInfo>> group(@RequestBody RankingDTO rankingDTO) {
        List<UserInfo> userInfos = userInfoService.acRankingList(rankingDTO);
        return ApiResult.success(userInfos);
    }

    @Operation(summary = "新增")
    @SaCheckPermission(value = "user.info.create")
    @PostMapping("/user-info/create")
    public ApiResult<Void> create(@RequestBody UserInfoCreateDTO dto) {
        userInfoService.create(dto);
        return ApiResult.success();
    }

    @Operation(summary = "修改")
    @SaCheckPermission(value = "user.info.update")
    @PutMapping("/user-info/update")
    public ApiResult<Void> update(@RequestBody UserInfoUpdateDTO dto) {
        userInfoService.update(dto);
        return ApiResult.success();
    }

    @Operation(summary = "删除")
    @SaCheckPermission(value = "user.info.remove")
    @DeleteMapping("/user-info/remove")
    public ApiResult<Void> remove(@RequestBody SelectIdsDTO dto) {
        userInfoService.remove(dto);
        return ApiResult.success();
    }

    @Operation(summary = "列表查询")
    @SaCheckPermission(value = "user.info.query_table")
    @PostMapping("/user-info/list")
    public ApiResult<PageResult<UserInfoVO>> list(@RequestBody UserInfoListDTO dto) {
        return ApiPageResult.success(userInfoService.page(dto));
    }

    @Operation(summary = "详情")
    @SaCheckPermission(value = "user.info.query_table")
    @GetMapping("/user-info/detail")
    public ApiResult<UserInfoVO> detail(@RequestParam Long id) {
        return ApiResult.success(userInfoService.detail(id));
    }

    @Operation(summary = "导入")
    @Parameters({
            @Parameter(name = "file", description = "上传文件", schema = @Schema(type = "string", format = "binary"), required = true),
    })
    @SaCheckPermission(value = "user.info.import")
    @PostMapping("/user-info/import")
    public void importExcel(@ModelAttribute ImportExcelDTO dto) {
        userInfoService.importExcel(dto);
    }

    @Operation(summary = "导出")
    @SaCheckPermission(value = "user.info.export")
    @PostMapping("/user-info/export")
    public void exportExcel(@RequestBody UserInfoListDTO dto, HttpServletResponse response) {
        userInfoService.exportExcel(dto, response);
    }

    @Operation(summary = "更新用户绑定")
    @SaCheckPermission(value = "user.info.update")
    @PostMapping("/user-info/bind")
    public void bind(@RequestBody UserInfoBindDTO dto) {
        userInfoService.bind(dto);
    }
}
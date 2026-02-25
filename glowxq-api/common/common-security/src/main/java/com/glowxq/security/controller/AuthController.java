package com.glowxq.security.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.stp.StpUtil;
import com.glowxq.core.common.annotation.DebounceIgnore;
import com.glowxq.core.common.entity.ApiResult;
import com.glowxq.security.pojo.LoginInfo;
import com.glowxq.security.pojo.LoginVO;
import com.glowxq.security.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 认证Controller。
 *
 * @author glowxq
 * @since 2023/12/25
 */

@Tag(name = "登录认证")
@RestController
@RequestMapping
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

    @SaIgnore
    @DebounceIgnore
    @Operation(summary = "登录")
    @PostMapping("/auth/login")
    public ApiResult<LoginVO> login(@RequestBody LoginInfo loginInfo) {
        return ApiResult.success(authService.loginClient(loginInfo));
    }

    @SaIgnore
    @DebounceIgnore
    @Operation(summary = "登录测试")
    @PostMapping("/auth/loginTest")
    public ApiResult<LoginVO> loginTest(@RequestParam String username) {
        return ApiResult.success(authService.loginTest(username));
    }

    @Operation(summary = "登出")
    @PostMapping("/auth/logout")
    public ApiResult<Void> logout() {
        // 注意执行顺序，最后再执行logout
        StpUtil.getTokenSession().logout(); // 清除缓存session
        StpUtil.logout(); // 退出登录
        return ApiResult.success();
    }
}

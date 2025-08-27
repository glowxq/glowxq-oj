package com.glowxq.system.admin.api;

import cn.dev33.satoken.annotation.SaIgnore;
import com.glowxq.core.common.annotation.DebounceIgnore;
import com.glowxq.core.common.api.BaseApi;
import com.glowxq.core.common.entity.ApiResult;
import com.glowxq.system.admin.pojo.dto.RegisterDTO;
import com.glowxq.system.admin.service.SysUserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author glowxq
 * @version 1.0
 * @date 2025/3/28
 */
@Tag(name = "用户管理")
@RestController
@RequestMapping("/client/sys-user/auth")
@RequiredArgsConstructor
public class UserApi extends BaseApi {

    private final SysUserService sysUserService;

    @SaIgnore
    @DebounceIgnore
    @PostMapping("/register")
    public ApiResult<Void> register(@RequestBody @Validated RegisterDTO registerParam) {
        sysUserService.register(registerParam);
        return ApiResult.success();
    }
}

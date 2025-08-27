package com.glowxq.system.admin.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import com.glowxq.core.common.annotation.Debounce;
import com.glowxq.core.common.entity.ApiResult;
import com.glowxq.core.common.entity.CheckPuzzle;
import com.glowxq.core.common.entity.SliderPuzzle;
import com.glowxq.core.util.SysConfigUtils;
import com.glowxq.system.admin.service.CaptchaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * 验证码controller
 *
 * @author glowxq
 * @version 1.0
 * @since 2025/01/07 19:07
 */
@Tag(name = "验证码")
@RestController
@RequestMapping("/captcha")
@RequiredArgsConstructor
@Slf4j
public class CaptchaController {

    private final CaptchaService captchaService;

    @SaIgnore
    @Debounce()
    @GetMapping("status")
    @Operation(summary = "验证码是否启用")
    public ApiResult<Boolean> captchaStatus() {
        String value = SysConfigUtils.getConfValue("sys.captcha.state");
        return ApiResult.success(Boolean.parseBoolean(value));
    }

    @SaIgnore
    @Debounce()
    @Operation(summary = "获取验证码")
    @PostMapping(value = "/get")
    public ApiResult<SliderPuzzle> getImageCode(HttpServletRequest request) {
        return ApiResult.success(captchaService.getImageCode(request));
    }

    @SaIgnore
    @Debounce()
    @Operation(summary = "校验滑块拼图验证码")
    @PostMapping(value = "/check")
    public ApiResult<Void> captchaCheck(@RequestBody CheckPuzzle checkPuzzle) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
            InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
        captchaService.checkImageCode(checkPuzzle);
        return ApiResult.success();
    }
}

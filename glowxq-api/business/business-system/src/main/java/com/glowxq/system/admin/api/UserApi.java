package com.glowxq.system.admin.api;

import cn.dev33.satoken.annotation.SaIgnore;
import com.glowxq.core.common.annotation.DebounceIgnore;
import com.glowxq.core.common.api.BaseApi;
import com.glowxq.core.common.entity.ApiResult;
import com.glowxq.security.pojo.LoginVO;
import com.glowxq.system.admin.pojo.dto.RegisterDTO;
import com.glowxq.system.admin.service.SysUserService;
import com.glowxq.system.applet.pojo.dto.WechatPhoneInfoDTO;
import com.glowxq.wechat.applet.WechatService;
import com.glowxq.wechat.applet.pojo.WechatPhoneInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author glowxq
 * @version 1.0
 * @date 2025/3/28
 */
@Tag(name = "注册")
@RestController
@RequestMapping("/client/sys-user/auth")
@RequiredArgsConstructor
public class UserApi extends BaseApi {

    private final SysUserService sysUserService;

    private final WechatService wechatService;


    @SaIgnore
    @DebounceIgnore
    @PostMapping("/register")
    public ApiResult<Void> register(@RequestBody @Validated RegisterDTO registerParam) {
        registerParam.buildBaseInfo();
        sysUserService.register(registerParam);
        return ApiResult.success();
    }

    // /**
    //  * 微信一键注册登录
    //  * @param wechatCode
    //  * @return
    //  */
    // @SaIgnore
    // @DebounceIgnore
    // @Operation(summary = "微信一键注册登录")
    // @PostMapping("/auth/applet/autoWechatLogin")
    // public ApiResult<LoginVO> autoWechatLogin(@RequestParam String wechatCode) {
    //     // return ApiResult.success(authService.autoWechatLogin(wechatCode));
    // }

    // /**
    //  * 微信小程序登录
    //  */
    // @PostMapping("/loginWechatApplet")
    // public ApiResult<Void> loginWechatApplet(@RequestBody String wechatLoginCode) {
    //
    //     return ApiResult.success();
    // }

    /**
     * 获取微信绑定手机号信息
     */
    @SaIgnore
    @PostMapping("/getWechatPhoneInfo")
    public ApiResult<WechatPhoneInfo> getWechatPhoneInfo(@RequestBody WechatPhoneInfoDTO wechatPhoneInfoDTO) {
        WechatPhoneInfo phoneInfo = wechatService.getPhoneInfo(wechatPhoneInfoDTO.getPhoneCode());
        return ApiResult.success(phoneInfo);
    }



    @DebounceIgnore
    @PostMapping("/bindWechat")
    public ApiResult<Void> bindWechat(@RequestBody String wechatLoginCode) {
        sysUserService.bindWechat(wechatLoginCode);
        return ApiResult.success();
    }
}

package com.glowxq.system.applet.api;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaIgnore;
import com.glowxq.core.common.annotation.DebounceIgnore;
import com.glowxq.core.common.api.BaseApi;
import com.glowxq.core.common.entity.ApiResult;
import com.glowxq.oss.UploadResult;
import com.glowxq.security.core.util.LoginUtils;
import com.glowxq.security.pojo.LoginInfo;
import com.glowxq.security.pojo.LoginVO;
import com.glowxq.security.service.AuthService;
import com.glowxq.system.applet.pojo.dto.AppletUserUpdateDTO;
import com.glowxq.system.applet.pojo.dto.BuildWechatCodeDTO;
import com.glowxq.system.applet.pojo.dto.RegisterDTO;
import com.glowxq.system.applet.pojo.dto.WechatPhoneInfoDTO;
import com.glowxq.system.applet.pojo.vo.AppletUserVO;
import com.glowxq.system.applet.service.AppletUserService;
import com.glowxq.wechat.applet.pojo.WechatPhoneInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * applet/小程序用户 Api
 *
 * @author glowxq
 * @version 1.0
 * @date 2025/4/25
 */
@Tag(name = "用户接口")
@RestController
@RequestMapping("/client/applet")
@RequiredArgsConstructor
public class AppletUserApi extends BaseApi {

    private final AppletUserService appletUserService;

    private final AuthService authService;

    /**
     * 注册接口
     */
    @SaIgnore
    @PostMapping("/register")
    public ApiResult<Void> register(@RequestBody RegisterDTO dto) {
        appletUserService.register(dto);
        return ApiResult.success();
    }

    /**
     * 微信登陆接口
     */
    @SaIgnore
    @PostMapping("/wechatLogin")
    public ApiResult<LoginVO> wechatLogin(@RequestBody LoginInfo info) {
        return ApiResult.success(authService.loginClient(info));
    }

    /**
     * 生成微信二维码信息
     */
    @PostMapping("/buildWechatCodeDTO")
    public ApiResult<UploadResult> buildWechatCodeDTO(@RequestBody BuildWechatCodeDTO dto) {
        UploadResult uploadResult = appletUserService.buildWechatCodeDTO(dto);
        return ApiResult.success(uploadResult);
    }

    /**
     * 获取微信绑定手机号信息
     */
    @SaIgnore
    @PostMapping("/getWechatPhoneInfo")
    public ApiResult<WechatPhoneInfo> getWechatPhoneInfo(@RequestBody WechatPhoneInfoDTO wechatPhoneInfoDTO) {
        WechatPhoneInfo phoneInfo = appletUserService.getPhoneInfo(wechatPhoneInfoDTO.getPhoneCode());
        return ApiResult.success(phoneInfo);
    }

    /**
     * 修改用户信息
     */
    @PostMapping("/updateUserInfo")
    public ApiResult<AppletUserVO> updateUserInfo(@RequestBody AppletUserUpdateDTO appletUserUpdateDTO) {
        appletUserUpdateDTO.setId(LoginUtils.getUserId());
        appletUserService.update(appletUserUpdateDTO);
        AppletUserVO detail = appletUserService.detail(LoginUtils.getUserId());
        return ApiResult.success(detail);
    }

    /**
     * 获取用户详情
     *
     * @return
     */
    @PostMapping("/getUserDetail")
    @SaCheckLogin
    public ApiResult<AppletUserVO> getUserDetail() {
        AppletUserVO detail = appletUserService.detail(LoginUtils.getUserId());
        return ApiResult.success(detail);
    }
}

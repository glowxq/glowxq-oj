package com.glowxq.wechat.applet;

import cn.binarywang.wx.miniapp.api.WxMaQrcodeService;
import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.api.WxMaUserService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.binarywang.wx.miniapp.bean.WxMaPhoneNumberInfo;
import com.glowxq.core.common.exception.common.AlertsException;
import com.glowxq.core.util.JsonUtils;
import com.glowxq.redis.RedisUtils;
import com.glowxq.wechat.applet.config.WechatAppletConfiguration;
import com.glowxq.wechat.applet.pojo.AccessTokenResult;
import com.glowxq.wechat.applet.pojo.ErrorMessage;
import com.glowxq.wechat.applet.pojo.LoginInfoResult;
import com.glowxq.wechat.applet.pojo.WechatPhoneInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.io.File;
import java.util.concurrent.TimeUnit;

import static com.glowxq.wechat.applet.WechatApiConstant.WECHAT_MINI_LOGIN_URL;
import static com.glowxq.wechat.applet.WechatApiConstant.WECHAT_TOKEN_URL;

/**
 * @author glowxq
 * @version 1.0
 * @since 2024/4/26 10:04
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class WechatService {

    private static final String WECHAT_MINI_TOKEN = "wechat:mini:token";

    private final WechatAppletConfiguration wechatAppletConfiguration;

    private final WxMaService wxMaService;

    /**
     * 获取accessToken【小程序】
     *
     * @return accessToken
     */
    public String getAccessToken() {
        if (RedisUtils.hasKey(WECHAT_MINI_TOKEN)) {
            return (String) RedisUtils.getValue(WECHAT_MINI_TOKEN);
        }
        else {
            ResponseEntity<AccessTokenResult> entity = RestClient.create().get()
                                                                 .uri(WECHAT_TOKEN_URL,
                                                                         wechatAppletConfiguration.getAppId(),
                                                                         wechatAppletConfiguration.getSecret())
                                                                 .retrieve()
                                                                 .toEntity(AccessTokenResult.class);
            AccessTokenResult result = entity.getBody();
            assert result != null;
            if (validSuccess(result)) {
                int expireTime = result.getExpiresIn() - 1200;
                RedisUtils.getRestTemplate().opsForValue().set(WECHAT_MINI_TOKEN, result.getAccessToken(), expireTime, TimeUnit.SECONDS);
                return result.getAccessToken();
            }
            else {
                log.error("【微信小程序】 获取accessToken失败，错误码：{}，错误信息：{}", result.getErrcode(), result.getErrmsg());
                return "";
            }
        }
    }

    /**
     * 微信小程序登录
     *
     * @param code        code
     * @param accessToken accessToken
     * @return 登录信息
     */
    public LoginInfoResult login(String code, String accessToken) {
        // 微信小程序登录接口返回content-type是text/plain，因此无法直接映射对象。使用String接收，后续再做转换
        ResponseEntity<String> entity = RestClient.create().get()
                                                  .uri(WECHAT_MINI_LOGIN_URL,
                                                          wechatAppletConfiguration.getAppId(),
                                                          wechatAppletConfiguration.getSecret(),
                                                          code,
                                                          accessToken)
                                                  .retrieve()
                                                  .toEntity(String.class);
        return JsonUtils.parseObject(entity.getBody(), LoginInfoResult.class);
    }

    /**
     * 微信小程序登录
     *
     * @param code code
     * @return 登录信息
     */
    public LoginInfoResult login(String code) {
        // 微信小程序登录接口返回content-type是text/plain，因此无法直接映射对象。使用String接收，后续再做转换
        WxMaUserService userService = wxMaService.getUserService();
        try {
            WxMaJscode2SessionResult sessionInfo = userService.getSessionInfo(code);
            LoginInfoResult loginInfoResult = new LoginInfoResult();
            loginInfoResult.setOpenid(sessionInfo.getOpenid());
            loginInfoResult.setSessionKey(sessionInfo.getSessionKey());
            loginInfoResult.setUnionId(sessionInfo.getUnionid());
            return loginInfoResult;
        } catch (WxErrorException e) {
            log.error("微信登陆失败 message:{}", e.getMessage(), e);
            throw new AlertsException("微信登陆失败");
        }
    }

    /**
     * 校验微信返回结果是否成功
     *
     * @param errorMessage 微信返回结果
     * @return 是否成功
     */
    public boolean validSuccess(ErrorMessage errorMessage) {
        return errorMessage.getErrcode() == null || errorMessage.getErrcode() == 0;
    }

    /**
     * 生成小程序二维码
     *
     * @param scene
     * @param page
     * @param envVersion
     * @return
     */
    public File createQRcode(String scene, String page, String envVersion) {
        WxMaQrcodeService qrcodeService = wxMaService.getQrcodeService();
        try {
            return qrcodeService.createWxaCodeUnlimit( scene, page, false, envVersion, 430, true, null, false);
        } catch (WxErrorException e) {
            log.error("微信小程序二维码生成失败 message:{}", e.getMessage(), e);
            throw new AlertsException("微信小程序二维码生成失败" + e.getMessage());
        }
    }

    /**
     * 获取微信手机号
     *
     * @param phoneCode
     * @return
     */
    public WechatPhoneInfo getPhoneInfo(String phoneCode) {
        WxMaUserService userService = wxMaService.getUserService();
        try {
            WxMaPhoneNumberInfo phoneNumberInfo = userService.getPhoneNumber(phoneCode);
            WechatPhoneInfo wechatPhoneInfo = new WechatPhoneInfo();
            wechatPhoneInfo.setPhoneNumber(phoneNumberInfo.getPhoneNumber());
            wechatPhoneInfo.setPurePhoneNumber(phoneNumberInfo.getPurePhoneNumber());
            wechatPhoneInfo.setCountryCode(phoneNumberInfo.getCountryCode());
            return wechatPhoneInfo;
        } catch (WxErrorException e) {
            log.error("获取微信手机号失败 message:{}", e.getMessage(), e);
            throw new AlertsException("获取微信手机号失败" + e.getMessage());
        }
    }
}

package com.glowxq.platform.strategy;

import cn.dev33.satoken.stp.SaLoginModel;
import com.glowxq.core.common.entity.LoginUser;
import com.glowxq.core.common.enums.CommonResponseEnum;
import com.glowxq.core.util.JsonUtils;
import com.glowxq.core.util.Utils;
import com.glowxq.security.core.util.LoginUtils;
import com.glowxq.security.pojo.ClientVO;
import com.glowxq.security.pojo.LoginInfo;
import com.glowxq.security.pojo.LoginVO;
import com.glowxq.security.service.IAuthStrategy;
import com.glowxq.system.admin.service.SysUserService;
import com.glowxq.system.applet.service.AppletUserService;
import com.glowxq.system.base.BaseAuthStrategy;
import com.glowxq.wechat.applet.WechatService;
import com.glowxq.wechat.applet.pojo.LoginInfoResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 小程序认证策略
 * <p>
 * AppletStrategy
 *
 * @author glowxq
 * @version 1.0
 * @since 2024/4/26 16:08
 */

@Slf4j
@Service("applet" + IAuthStrategy.BASE_NAME)
@RequiredArgsConstructor
public class AppletStrategy extends BaseAuthStrategy {

    protected final WechatService wechatService;

    protected final AppletUserService appletUserService;

    protected final SysUserService sysUserService;

    @Override
    protected LoginVO doLogin(LoginInfo info, ClientVO client) {
        String clientId = client.getClientId();
        String code = info.getCode();
        CommonResponseEnum.INVALID.message("无效的小程序code").assertFalse(Utils.isNotNull(code));

        String accessToken = wechatService.getAccessToken();
        LoginInfoResult result = wechatService.login(code, accessToken);
        log.info("小程序登录返回信息：{}", JsonUtils.toJsonString(result));
        String openid = result.getOpenid();
        String unionid = result.getUnionId();
        String sessionKey = result.getSessionKey(); // 小程序登录凭证
        log.info("小程序登录返回信息：openid={}, unionid={}, sessionKey={}", openid, unionid, sessionKey);
        LoginUser loginUser = sysUserService.buildLoginUser(openid);

        // 设置登录模型
        SaLoginModel model = createLoginModel(client);

        // 设置jwt额外数据
        Map<String, Object> extraData = createExtraData(clientId, loginUser.getUserInfo().getId());

        // 执行登录
        LoginUtils.performLogin(loginUser, model, extraData);

        // 构造返回对象
        return createLoginVO(loginUser);
    }
}

package com.glowxq.system.base;

import cn.dev33.satoken.stp.SaLoginModel;
import cn.dev33.satoken.stp.StpUtil;
import com.glowxq.core.common.entity.BaseUserInfo;
import com.glowxq.core.common.entity.LoginUser;
import com.glowxq.core.common.enums.MenuType;
import com.glowxq.security.pojo.ClientVO;
import com.glowxq.security.pojo.LoginInfo;
import com.glowxq.security.pojo.LoginVO;
import com.glowxq.security.service.IAuthStrategy;
import com.glowxq.tenant.business.service.TenantService;
import com.glowxq.tenant.utils.TenantUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author glowxq
 * @version 1.0
 * @date 2025/6/10
 */
@Slf4j
@Component
@RequiredArgsConstructor
public abstract class BaseAuthStrategy implements IAuthStrategy {

    @Autowired
    private TenantService tenantService;

    @Autowired
    private AuthBusinessHandler authBusinessHandler;

    @Override
    public LoginVO login(LoginInfo info, ClientVO client) {
        this.beforeCheck();
        LoginVO loginVo = doLogin(info, client);
        authBusinessHandler.afterCheck(loginVo.getUserId());
        loginAfter(loginVo.getUserId());
        return loginVo;
    }

    protected void loginAfter(Long userId) {
        log.info("登录成功，用户ID：{}", userId);
        authBusinessHandler.loginAfter(userId);
    }

    protected void beforeCheck() {
        tenantService.checkTenantThrow(TenantUtils.getTenantId());
    }

    protected abstract LoginVO doLogin(LoginInfo info, ClientVO client);

    protected SaLoginModel createLoginModel(ClientVO client) {
        SaLoginModel model = new SaLoginModel();
        model.setDevice(client.getDeviceTypeCd());
        model.setTimeout(client.getTimeout());
        model.setActiveTimeout(client.getActiveTimeout());
        return model;
    }

    protected Map<String, Object> createExtraData(String clientId, Long userId) {
        Map<String, Object> extraData = new HashMap<>();
        extraData.put("clientId", clientId);
        extraData.put("userId", userId);
        return extraData;
    }

    protected LoginVO createLoginVO(LoginUser loginUser) {
        BaseUserInfo userInfo = loginUser.getUserInfo();
        LoginVO loginVo = new LoginVO();
        loginVo.setUserId(loginUser.getUserId());
        loginVo.setTenantId(loginVo.getTenantId());
        loginVo.setAccessToken(StpUtil.getTokenValue());
        loginVo.setExpireIn(StpUtil.getTokenTimeout());
        loginVo.setUserInfo(userInfo);
        loginVo.setMenuType(MenuType.Admin);
        return loginVo;
    }
}

package com.glowxq.platform.strategy;

import cn.dev33.satoken.stp.SaLoginModel;
import com.glowxq.core.common.entity.LoginUser;
import com.glowxq.core.common.enums.MenuType;
import com.glowxq.security.core.constants.RoleConstant;
import com.glowxq.security.core.util.LoginUtils;
import com.glowxq.security.pojo.ClientVO;
import com.glowxq.security.pojo.LoginInfo;
import com.glowxq.security.pojo.LoginVO;
import com.glowxq.security.service.IAuthStrategy;
import com.glowxq.system.admin.service.SysUserService;
import com.glowxq.system.base.AuthBusinessHandler;
import com.glowxq.system.base.BaseAuthStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 密码认证策略
 * <p>
 * PasswordStrategy
 *
 * @author glowxq
 * @version 1.0
 * @since 2024/1/23 10:29
 */
@Slf4j
@Service("password" + IAuthStrategy.BASE_NAME)
@RequiredArgsConstructor
public class PasswordStrategy extends BaseAuthStrategy {

    @Autowired
    protected AuthBusinessHandler authBusinessHandler;

    @Autowired
    private SysUserService sysUserService;

    @Override
    protected LoginVO doLogin(LoginInfo info, ClientVO client) {
        String clientId = client.getClientId();
        String password = info.getPassword();
        String username = info.getUsername();
        // 设置登录模型
        SaLoginModel model = createLoginModel(client);
        // 复制用户信息
        LoginUser loginUser = sysUserService.buildLoginUser(username, password);
        authBusinessHandler.buildLoginUser(loginUser);

        Long userId = loginUser.getUserInfo().getId();
        // 设置jwt额外数据
        Map<String, Object> extraData = createExtraData(clientId, userId);
        // 执行登录
        LoginUtils.performLogin(loginUser, model, extraData);
        // 构造返回对象
        return createLoginVO(loginUser);
    }

    @Override
    protected LoginVO createLoginVO(LoginUser loginUser) {
        LoginVO loginVo = super.createLoginVO(loginUser);
        loginVo.setMenuType(loginUser.containsRole(RoleConstant.STUDENT) ? MenuType.Client : MenuType.Admin);
        return loginVo;
    }
}

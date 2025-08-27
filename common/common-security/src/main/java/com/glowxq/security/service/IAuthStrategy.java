package com.glowxq.security.service;

import com.glowxq.core.common.enums.CommonResponseEnum;
import com.glowxq.core.util.SpringApplicationContextUtils;
import com.glowxq.security.pojo.ClientVO;
import com.glowxq.security.pojo.LoginInfo;
import com.glowxq.security.pojo.LoginVO;

/**
 * 策略接口
 *
 * @author glowxq
 * @since 2024/1/23 9:38
 * @version 1.0
 */
public interface IAuthStrategy {

    String BASE_NAME = "AuthStrategy";
    /**
     * 登录
     *
     * @param info     登录信息
     * @param client   客户端信息
     * @param grantType 授权类型
     * @return 登录信息
     */
    static LoginVO login(LoginInfo info, ClientVO client, String grantType) {
        // 授权类型和客户端id
        String beanName = grantType + BASE_NAME;
        CommonResponseEnum.INVALID.message("无效的授权类型").assertFalse(SpringApplicationContextUtils.getInstance().containsBean(beanName));
        IAuthStrategy instance = SpringApplicationContextUtils.getInstance().getBean(beanName);
        return instance.login(info, client);
    }

    LoginVO login(LoginInfo info, ClientVO client);

}

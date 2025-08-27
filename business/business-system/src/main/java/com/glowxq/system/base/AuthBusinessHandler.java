package com.glowxq.system.base;

import com.glowxq.core.common.entity.LoginUser;

/**
 * @author glowxq
 * @version 1.0
 * @date 2025/6/23
 */
public interface AuthBusinessHandler {

    void buildLoginUser(LoginUser loginUser);

    void afterCheck(Long userId);

    void loginAfter(Long userId);
}

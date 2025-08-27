package com.glowxq.security.service;

import com.glowxq.security.pojo.LoginInfo;
import com.glowxq.security.pojo.LoginVO;

/**
 * @author glowxq
 * @since 2024/1/22 17:24
 * @version 1.0
 */
public interface AuthService {

    LoginVO loginClient(LoginInfo info);

    void kickOut(Long userId);
}

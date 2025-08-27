package com.glowxq.platform.strategy;

import com.glowxq.security.pojo.ClientVO;
import com.glowxq.security.pojo.LoginInfo;
import com.glowxq.security.pojo.LoginVO;
import com.glowxq.security.service.IAuthStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * EmailAuthStrategy
 * 
 * @author glowxq
 * @since 2024/1/23 10:29
 * @version 1.0
 */
@Slf4j
@Service("email" + IAuthStrategy.BASE_NAME)
public class EmailAuthStrategy implements IAuthStrategy {

    @Override
    public LoginVO login(LoginInfo body, ClientVO client) {
        return null;
    }
}

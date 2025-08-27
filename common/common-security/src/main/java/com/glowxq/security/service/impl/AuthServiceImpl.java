package com.glowxq.security.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.glowxq.core.common.entity.SocketMessage;
import com.glowxq.core.common.entity.TransferMessage;
import com.glowxq.core.common.enums.CommonResponseEnum;
import com.glowxq.core.common.enums.SocketChannelEnum;
import com.glowxq.redis.WebsocketRedisService;
import com.glowxq.security.pojo.ClientVO;
import com.glowxq.security.pojo.LoginInfo;
import com.glowxq.security.pojo.LoginVO;
import com.glowxq.security.service.AuthService;
import com.glowxq.security.service.ClientService;
import com.glowxq.security.service.IAuthStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * @author glowxq
 * @since 2022-10-01
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final WebsocketRedisService websocketRedisService;

    private final ClientService clientService;

    @Override
    public LoginVO loginClient(LoginInfo info) {
        String clientId = info.getClientId();
        ClientVO client = clientService.getClientByClientId(clientId);
        // 验证clientId有效性
        CommonResponseEnum.CLIENT_INVALID.assertNull(client);
        // 验证client status 有效性
        CommonResponseEnum.CLIENT_BLOCKED.assertTrue(!("1003001").equals(client.getClientStatusCd()));
        return IAuthStrategy.login(info, client, info.getGrantType());
    }

    /**
     * 强制注销指定用户
     *
     * @param id 用户id
     */
    @Override
    public void kickOut(Long id) {
        TransferMessage<Void> tm = new TransferMessage<>();
        tm.setToUsers(Collections.singletonList(id + ""));
        SocketMessage<Void> sb = new SocketMessage<>();
        sb.setChannel(SocketChannelEnum.KICK_OFF);
        tm.setMessage(sb);
        websocketRedisService.sendServiceToWs(tm);
        StpUtil.logout(id);
    }
}

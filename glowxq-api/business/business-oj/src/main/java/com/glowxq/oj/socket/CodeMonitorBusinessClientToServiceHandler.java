package com.glowxq.oj.socket;

import com.glowxq.core.common.entity.SocketMessage;
import com.glowxq.core.common.entity.WsSession;
import com.glowxq.core.common.exception.common.AlertsException;
import com.glowxq.core.util.JsonUtils;
import com.glowxq.core.util.ValidatorUtils;
import com.glowxq.oj.code.pojo.dto.CodeMonitorDTO;
import com.glowxq.oj.code.service.CodeMonitorService;
import com.glowxq.oj.user.pojo.po.UserInfo;
import com.glowxq.oj.user.service.UserInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CodeMonitorBusinessClientToServiceHandler extends BaseBusinessClientToServiceHandler {

    private final UserInfoService userInfoService;

    private final CodeMonitorService codeMonitorService;

    @Override
    protected void doBusiness(WsSession session, SocketMessage socketMessage) {
        String loginId = session.getLoginId();
        UserInfo userInfo = userInfoService.getById(Long.parseLong(loginId));
        if (userInfo == null) {
            throw new AlertsException("【websocket】 unknown loginId: {%s}, send to service ... ".formatted(loginId));
        }
        log.info("【websocket】 loginId: {%s}, send to service ... ".formatted(loginId));
        String jsonString = JsonUtils.toJsonString(socketMessage.getData());
        CodeMonitorDTO monitorCreateDTO = JsonUtils.parseObject(jsonString, CodeMonitorDTO.class);
        if (!ValidatorUtils.validate(monitorCreateDTO)) {
            log.error("【websocket】 code validate not pass: {%s} ... ".formatted(monitorCreateDTO));;
        }

        codeMonitorService.monitorCode(monitorCreateDTO, userInfo);
    }
}

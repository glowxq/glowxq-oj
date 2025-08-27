package com.glowxq.platform.handler;

import com.glowxq.core.common.entity.UserPermissionChangeMessage;
import com.glowxq.core.util.JsonUtils;
import com.glowxq.redis.handler.UserPermissionChangeMsgHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PermissionChangeHandler implements UserPermissionChangeMsgHandler {

    @Override
    public void handlerMsg(UserPermissionChangeMessage message) {
        log.info(" [signal] message  = {}", JsonUtils.toJsonString(message));
        // [do something ...] 同步更新变更用户的权限信息
    }
}

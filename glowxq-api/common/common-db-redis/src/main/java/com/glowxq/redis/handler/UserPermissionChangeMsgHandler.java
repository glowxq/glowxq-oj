package com.glowxq.redis.handler;

import com.glowxq.core.common.entity.UserPermissionChangeMessage;

public interface UserPermissionChangeMsgHandler {

    void handlerMsg(UserPermissionChangeMessage message);
}
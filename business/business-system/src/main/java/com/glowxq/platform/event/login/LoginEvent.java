package com.glowxq.platform.event.login;

import com.glowxq.core.common.event.BaseEvent;

/**
 * 权限变更事件（用户角色变更、角色权限变更）
 * 
 * @author glowxq
 * @since 2024/2/29 15:44
 * @version 1.0
 */
public class LoginEvent extends BaseEvent<LoginMeta> {

    public LoginEvent(Object source, LoginMeta payload) {
        super(source, payload);
    }
}

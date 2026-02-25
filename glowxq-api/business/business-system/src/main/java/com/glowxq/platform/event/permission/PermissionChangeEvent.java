package com.glowxq.platform.event.permission;

import com.glowxq.core.common.event.BaseEvent;

/**
 * 权限变更事件（用户角色变更、角色权限变更）
 *
 * @author glowxq
 * @version 1.0
 * @since 2024/2/29 15:44
 */
public class PermissionChangeEvent extends BaseEvent<PermissionMeta> {

    public PermissionChangeEvent(Object source, PermissionMeta payload) {
        super(source, payload);
    }
}

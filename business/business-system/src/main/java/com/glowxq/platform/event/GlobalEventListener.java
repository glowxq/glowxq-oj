package com.glowxq.platform.event;

import com.glowxq.core.util.JsonUtils;
import com.glowxq.platform.event.permission.PermissionChangeEvent;
import com.glowxq.platform.event.permission.PermissionMeta;
import com.glowxq.system.admin.service.SysUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * EventListener 事件监听器
 * 
 * @author glowxq
 * @since 2024/2/29 16:27
 * @version 1.0
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class GlobalEventListener {

    private final SysUserService sysUserService;

    @EventListener
    public void handlePermissionChangeEvent(PermissionChangeEvent event) {
        PermissionMeta permissionMeta = event.getPayload();
        log.warn("[事件监听]-权限变更, data: {}", JsonUtils.toJsonString(permissionMeta));
        List<?> userIds = permissionMeta.getUserIds();
        for (Object userId : userIds) {
            sysUserService.syncUserInfo(userId);
        }
    }
}

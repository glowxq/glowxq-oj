package com.glowxq.platform.event.permission;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * PermissionChangeMeta
 * 
 * @author glowxq
 * @since 2024/2/29 15:44
 * @version 1.0
 */
@Data
public class PermissionMeta {

    private List<?> userIds = new ArrayList<>();

    public PermissionMeta() {
    }

    public PermissionMeta(List<?> userIds) {
        this.userIds = userIds;
    }


}

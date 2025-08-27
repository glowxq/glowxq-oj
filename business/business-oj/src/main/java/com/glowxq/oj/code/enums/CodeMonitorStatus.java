package com.glowxq.oj.code.enums;

import com.glowxq.core.common.enums.base.BaseType;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author glowxq
 * @version 1.0
 * @date 2025/1/20
 */
@AllArgsConstructor
@Getter
public enum CodeMonitorStatus implements BaseType {
    MonitorPush("MonitorPush", "监控端（学生）推送代码"),
    CoveredPush("CoveredPush", "覆盖端（老师）推送代码"),
    MonitorPull("MonitorPull", "监控端（学生）拉取代码"),
    MonitorReject("MonitorReject", "监控端（学生）拒绝代码"),;

    /**
     * code
     */
    private final String code;

    /**
     * 最近类型
     */
    private final String name;

    /**
     * 根据code获取枚举
     *
     * @param code
     * @return
     */
    public static CodeMonitorStatus matchCode(String code) {
        for (CodeMonitorStatus pushStatus : CodeMonitorStatus.values()) {
            if (pushStatus.getCode().equals(code)) {
                return pushStatus;
            }
        }
        return null;
    }
}

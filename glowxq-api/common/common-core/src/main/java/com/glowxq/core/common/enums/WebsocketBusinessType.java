package com.glowxq.core.common.enums;

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
public enum WebsocketBusinessType implements BaseType {

    // 未知
    Unknown("Unknown","未知",0),
    CodeMonitor("CodeMonitor", "代码监控推送", -1),
    PushCoveredCode("PushCoveredCode", "教师推送覆盖代码", 1),
    JudgeNotify("JudgeNotify", "测评结果", 1),
    BeginCourse("BeginCourse", "上课通知", 1);

    /**
     * code
     */
    private final String code;

    /**
     * 最近类型
     */
    private final String name;

    /**
     * 方向 0:全双工 1:S->C -1:C->S
     */
    private final Integer direction;

    /**
     * 根据code获取枚举
     *
     * @param code
     * @return
     */
    public static WebsocketBusinessType matchCode(String code) {
        for (WebsocketBusinessType pushStatus : WebsocketBusinessType.values()) {
            if (pushStatus.getCode().equals(code)) {
                return pushStatus;
            }
        }
        return Unknown;
    }
}

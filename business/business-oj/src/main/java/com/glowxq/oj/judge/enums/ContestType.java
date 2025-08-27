package com.glowxq.oj.judge.enums;

import com.glowxq.core.common.enums.base.BaseType;
import com.glowxq.core.common.exception.common.AlertsException;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ContestType implements BaseType {
    TYPE_ACM(0, "ACM"),
    TYPE_OI(1, "OI"),

    STATUS_SCHEDULED(-1, "Scheduled"),
    STATUS_RUNNING(0, "Running"),
    STATUS_ENDED(1, "Ended"),

    AUTH_PUBLIC(0, "Public"),
    AUTH_PRIVATE(1, "Private"),
    AUTH_PROTECT(2, "Protect"),

    RECORD_NOT_AC_PENALTY(-1, "未AC通过算罚时"),
    RECORD_NOT_AC_NOT_PENALTY(0, "未AC通过不罚时"),
    RECORD_AC(1, "AC通过");

    private final Integer code;
    private final String name;


    /**
     * 匹配代码
     *
     * @param code
     * @return {@link ContestType}
     */
    public static ContestType matchCode(Integer code) {
        for (ContestType type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        throw new AlertsException(String.format("%s 未匹配到对应 EnvType枚举类型", code));
    }
}

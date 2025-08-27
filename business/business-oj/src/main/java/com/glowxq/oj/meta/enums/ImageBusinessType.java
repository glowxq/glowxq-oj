package com.glowxq.oj.meta.enums;

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
public enum ImageBusinessType implements BaseType {

    Carousel("Carousel", "轮播图"),
    Temp("Temp", "临时图片"),

    ;

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
    public static ImageBusinessType matchCode(String code) {
        for (ImageBusinessType pushStatus : ImageBusinessType.values()) {
            if (pushStatus.getCode().equals(code)) {
                return pushStatus;
            }
        }
        return null;
    }
}

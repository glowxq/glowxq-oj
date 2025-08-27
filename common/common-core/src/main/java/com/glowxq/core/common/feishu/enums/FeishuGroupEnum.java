package com.glowxq.core.common.feishu.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author glowxq
 * @date 2023/09/26
 */
@Getter
@AllArgsConstructor
public enum FeishuGroupEnum {

    /**
     * 微音开发
     */
    TestGroup("微音", "", "开发群");

    /**
     * 飞书名
     */
    private final String feishuName;

    /**
     * 飞书机器人URL
     */
    private final String webhook;

    /**
     * 群名
     */
    private final String groupName;
}

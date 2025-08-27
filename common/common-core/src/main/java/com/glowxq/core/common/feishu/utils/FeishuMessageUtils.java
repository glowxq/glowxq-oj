package com.glowxq.core.common.feishu.utils;

import com.glowxq.core.common.feishu.enums.FeiShuAtEnum;
import com.glowxq.core.common.feishu.enums.FeishuGroupEnum;
import com.glowxq.core.util.AppUtils;
import com.glowxq.core.util.JsonUtils;
import com.glowxq.core.util.SpringUtils;
import com.glowxq.core.util.http.HttpUtils;
import com.glowxq.core.common.feishu.bo.TextMessageBO;
import com.glowxq.core.common.configuration.FeiShuConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;

/**
 * Fei Shu 消息 utils
 *
 * @author glowxq
 * @date 2024/07/29
 */
@Slf4j
public class FeishuMessageUtils {

    /**
     * 飞书at功能文本
     */
    private static final String AT_TEXT = "<at user_id =\"%s\"></at>";

    /**
     * 换行符
     */
    private static final String NEWLINE = "\n";

    /**
     * 标题
     */
    private static final String TITLE = "【%s】-【%s】";

    /**
     * 飞书机器人属性
     */
    private static final FeiShuConfig PROPERTIES = SpringUtils.getBean(FeiShuConfig.class);

    /**
     * @param message         消息内容
     * @param feishuGroupEnum 飞书群枚举
     * @param feiShuAtEnums   枚举信息
     */
    public static void sendMessage(String message, FeishuGroupEnum feishuGroupEnum, FeiShuAtEnum... feiShuAtEnums) {
        sendMessage(message, feishuGroupEnum.getWebhook(), feiShuAtEnums);
    }

    /**
     * 发送短消息
     *
     * @param message       消息内容
     * @param url           网址
     * @param feiShuAtEnums Fei Shu 在 ENUMS
     */
    private static void sendMessage(String message, String url, FeiShuAtEnum... feiShuAtEnums) {
        String appName = AppUtils.getAppName();
        String title = String.format(TITLE, appName, PROPERTIES.env().getName());
        String traceId = ""; // SkyWalking.getTraceId();
        message = title + NEWLINE + traceId + NEWLINE + message + NEWLINE + getAtText(feiShuAtEnums);
        TextMessageBO textMessageBo = new TextMessageBO(message);
        HttpUtils.postAsync(url, JsonUtils.toJsonString(textMessageBo));
    }

    /**
     * 获取AT文本
     *
     * @param feiShuAtEnums 飞书AT枚举
     * @return {@link String}
     */
    private static String getAtText(FeiShuAtEnum... feiShuAtEnums) {
        if (ArrayUtils.isEmpty(feiShuAtEnums)) {
            return "";
        }
        if (PROPERTIES.isNotProd()) {
            return "";
        }
        StringBuilder allAtText = new StringBuilder();
        for (FeiShuAtEnum feiShuAtEnum : feiShuAtEnums) {
            allAtText.append(String.format(AT_TEXT, feiShuAtEnum.getUserOpenId()));
        }
        return allAtText.toString();
    }

    /**
     * 发送内部消息
     *
     * @param message       消息内容
     * @param feiShuAtEnums Fei Shu 在 ENUMS
     */
    public static void sendInternalMessage(String message, FeiShuAtEnum... feiShuAtEnums) {
        sendMessage(message, PROPERTIES.getInternalUrl(), feiShuAtEnums);
    }

    /**
     * 发送业务消息
     *
     * @param message       消息内容
     * @param feiShuAtEnums Fei Shu 在 ENUMS
     */
    public static void sendBusinessMessage(String message, FeiShuAtEnum... feiShuAtEnums) {
        sendMessage(message, PROPERTIES.getBusinessUrl(), feiShuAtEnums);
    }
}

package com.glowxq.oj.judge.processor.utils;

import org.apache.commons.lang3.StringUtils;

/**
 * @author glowxq
 * @version 1.0
 * @date 2025/3/20
 */
public class MessageUtils {

    public static String mergeNonEmptyStrings(String... strings) {
        StringBuilder sb = new StringBuilder();
        for (String str : strings) {
            if (StringUtils.isNotEmpty(str)) {
                sb.append(str.substring(0, Math.min(1024 * 1024, str.length()))).append("\n");
            }
        }
        return sb.toString();
    }
}

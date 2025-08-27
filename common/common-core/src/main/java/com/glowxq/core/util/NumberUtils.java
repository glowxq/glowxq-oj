package com.glowxq.core.util;

import cn.hutool.core.util.RandomUtil;
import com.glowxq.core.common.enums.base.NumberGen;

/**
 * @author glowxq
 * @version 1.0
 * @date 2025/6/16
 */
public class NumberUtils {

    public static String generateNumber(NumberGen prefix) {
        return prefix.numberPrefix() + RandomUtil.randomStringUpper(6);
    }
}

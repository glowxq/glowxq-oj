package com.glowxq.core.util;

import com.glowxq.core.common.filter.trace.TraceLogContext;

/**
 * @author glowxq
 * @version 1.0
 * @date 2025/8/20
 */
public class TraceUtils {

    public static String getSpanId() {
        return TraceLogContext.getSpanId();
    }
}

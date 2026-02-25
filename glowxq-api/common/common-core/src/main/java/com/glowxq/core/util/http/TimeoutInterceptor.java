package com.glowxq.core.util.http;

/**
 * @author glowxq
 * @version 1.0
 * @date 2024/4/28
 */

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 超时拦截器
 *
 * @author glowxq
 * @date 2024/04/28
 */
public class TimeoutInterceptor implements Interceptor {

    @NotNull
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        TimeoutConfigure timeoutConfigure = request.tag(TimeoutConfigure.class);
        if (Objects.nonNull(timeoutConfigure)) {
            chain.withConnectTimeout(timeoutConfigure.getConnectTimeout(), TimeUnit.SECONDS)
                    .withReadTimeout(timeoutConfigure.getReadTimeout(), TimeUnit.SECONDS)
                    .withWriteTimeout(timeoutConfigure.getWriteTimeout(), TimeUnit.SECONDS);

        }
        return chain.proceed(request);

    }
}
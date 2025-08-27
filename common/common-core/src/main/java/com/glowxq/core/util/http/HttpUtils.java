package com.glowxq.core.util.http;

import com.glowxq.core.common.exception.common.BusinessException;
import com.glowxq.core.util.bo.DownloadResult;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.http.HttpMethod;
import org.springframework.util.CollectionUtils;

import java.io.InputStream;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

/**
 * HTTP utils
 *
 * @author glowxq
 * @date 2024/07/29
 */
@Slf4j
public class HttpUtils {

    /**
     * http客户端
     */
    private static final OkHttpClient HTTP_CLIENT = new OkHttpClient.Builder().addInterceptor(new TimeoutInterceptor()).build();

    /**
     * ContentType 类型
     */
    private static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");

    /**
     * ContentType 类型
     */
    private static final MediaType MEDIA_TYPE_FORM = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");

    /**
     * 构建请求默认方法
     *
     * @param url    url
     * @param method 方法
     * @param body   请求体
     * @param params URL参数
     * @return {@link String}
     */
    public static String request(String url, HttpMethod method, Map<String, String> params, String body) {
        return request(url, method, MEDIA_TYPE_JSON, null, params, body, null);
    }

    /**
     * 构建请求核心方法，负责发送HTTP请求。
     * 这里会记录请求的异常但不会进行处理，仅作为日志记录。
     *
     * @param url              请求的URL地址。
     * @param method           请求的方法类型，例如GET、POST等。
     * @param mediaType        请求的媒体类型，用于指定请求体的数据格式。
     * @param headers          请求的头部信息，可以为空。
     * @param params           URL参数，会附加在URL上，可以为空。
     * @param body             请求的主体内容，可以为空。
     * @param timeoutConfigure 请求超时配置，用于设置请求的超时时间。
     * @return 返回请求的结果，为{@link String}类型。
     *
     * @throws Exception 如果请求过程中发生异常，则抛出。
     */
    public static String request(String url, HttpMethod method, MediaType mediaType, Map<String, String> headers, Map<String, String> params, String body,
                                 TimeoutConfigure timeoutConfigure) {
        try {
            // 构建请求对象
            Request request = buildRequest(url, method, mediaType, headers, params, body, timeoutConfigure);
            // 执行请求并返回结果
            return synchronizedCall(request);
        } catch (Exception e) {
            // 记录请求异常日志
            log.error(String.format("请求[%s]发生异常 params:[%s] method:[%s] body:[%s]", url, params, method, body), e);
            throw e;
        }
    }

    /**
     * 构建一个OkHttp请求。
     *
     * @param url              请求的URL地址。
     * @param method           请求的方法类型，例如GET、POST等。
     * @param mediaType        请求的媒体类型，用于指定请求体的数据类型。
     * @param headers          请求的头部信息，可以包含授权信息、内容类型等。
     * @param params           请求的参数，通常以键值对的形式传递，会拼接在URL上。
     * @param body             请求的正文体，对于某些HTTP方法（如POST）是必需的。
     * @param timeoutConfigure 请求的超时配置，用于设置连接、读取和写入的超时时间。
     * @return 返回构建好的OkHttp Request对象。
     */
    private static Request buildRequest(String url, HttpMethod method, MediaType mediaType, Map<String, String> headers, Map<String, String> params, String body,
                                        TimeoutConfigure timeoutConfigure) {
        Request.Builder requestBuild = new Request.Builder();
        requestBuild.method(method.name(), buildRequestBody(method, mediaType, body));
        requestBuild.setUrl$okhttp(buildUrl(url, params));
        requestBuild.tag(TimeoutConfigure.class, timeoutConfigure);
        requestBuild.headers(buildHeaders(headers));
        return requestBuild.build();
    }

    /**
     * 同步调用
     *
     * @param request 请求
     * @return {@link String}
     */
    private static String synchronizedCall(Request request) {
        try (Response response = HTTP_CLIENT.newCall(request).execute()) {
            ResponseBody body = response.body();
            if (!response.isSuccessful() || Objects.isNull(body)) {
                return "";
            }
            return body.string();
        } catch (IOException e) {
            log.error(String.format("请求[%s]发生 IOException 异常", request.url()), e);
            throw new BusinessException(e.getMessage());
        }
    }

    /**
     * 生成请求体
     *
     * @param mediaType 媒体类型
     * @param body      请求头
     * @param method    方法
     * @return {@link RequestBody}
     */
    private static RequestBody buildRequestBody(HttpMethod method, MediaType mediaType, String body) {
        // GET、HEAD 请求不应含有请求体，如果含有请求体，直接创建body由okHttp自身内层抛出异常
        if (!okhttp3.internal.http.HttpMethod.permitsRequestBody(method.name())) {
            return body == null ? null : RequestBody.create(body, mediaType);
        }

        // OKHttp 不允许提交空请求体,如果确实请求无请求体参数则应使用空字符串，这里做了兜底
        if (Objects.isNull(body)) {
            body = "";
        }
        return RequestBody.create(body, mediaType);
    }

    /**
     * 构建url
     *
     * @param params 参数
     * @param url    url
     */
    private static HttpUrl buildUrl(String url, Map<String, String> params) {
        HttpUrl.Builder builder = HttpUrl.get(url).newBuilder();
        if (CollectionUtils.isEmpty(params)) {
            return builder.build();
        }
        // 添加参数
        for (Map.Entry<String, String> param : params.entrySet()) {
            builder.addQueryParameter(param.getKey(), param.getValue());
        }
        return builder.build();
    }

    /**
     * 构建请求头
     *
     * @param headers 请求头
     * @return {@link Headers}
     */
    private static Headers buildHeaders(Map<String, String> headers) {
        Headers.Builder builder = new Headers.Builder();
        if (CollectionUtils.isEmpty(headers)) {
            return builder.build();
        }
        for (Map.Entry<String, String> header : headers.entrySet()) {
            builder.add(header.getKey(), header.getValue());
        }
        return builder.build();
    }

    /**
     * 异步调用
     *
     * @param url        网址
     * @param jsonString JSON 字符串
     */
    public static void postAsync(String url, String jsonString) {
        Runnable runnable = () -> {
            post(url, jsonString);
        };
        Thread.ofVirtual()
              .name("postAsync")
              .start(runnable);
    }

    /**
     * 构建请求默认方法
     *
     * @param url  url
     * @param body 请求体
     * @return {@link String}
     */
    public static String post(String url, String body) {
        return request(url, HttpMethod.POST, MEDIA_TYPE_JSON, null, null, body, null);
    }

    /**
     * 构建请求默认方法
     *
     * @param url    url
     * @param method 方法
     * @param body   请求体
     * @param params URL参数
     * @return {@link String}
     */
    public static String request(String url, HttpMethod method, Map<String, String> params, String body, TimeoutConfigure timeoutConfigure) {
        return request(url, method, MEDIA_TYPE_JSON, null, params, body, timeoutConfigure);
    }

    /**
     * 构建 GET 请求默认方法
     *
     * @param url url
     * @return {@link String
     */
    public static String get(String url) {
        return request(url, HttpMethod.GET, MEDIA_TYPE_JSON, null, null, null, null);
    }

    /**
     * 构建 GET 请求默认方法
     *
     * @param url    url
     * @param params URL参数
     * @return {@link String}
     */
    public static String get(String url, Map<String, String> params) {
        return request(url, HttpMethod.GET, MEDIA_TYPE_JSON, null, params, null, null);
    }

    /**
     * 文件下载专用方法
     *
     * @param url 文件下载地址
     * @return DownloadResult 包含文件数据和元信息
     */
    public static DownloadResult download(String url) {
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        try (Response response = HTTP_CLIENT.newCall(request).execute()) {
            if (!response.isSuccessful() || response.body() == null) {
                throw new IOException("下载失败，HTTP状态码: " + response.code());
            }

            // 获取Content-Type（默认octet-stream）
            String contentType = response.header("Content-Type", "application/octet-stream");
            return new DownloadResult(response.body().bytes(), contentType);
        } catch (IOException e) {
            log.error("文件下载异常 - URL: {}", url, e);
            throw new RuntimeException("文件下载失败", e);
        }
    }

    /**
     * 使用 NIO 方式下载文件并返回 InputStream
     * 更高效的内存使用，支持大文件下载
     *
     * @param url 文件下载地址
     * @return InputStream 文件数据流
     * @throws RuntimeException 下载失败时抛出
     */
    public static InputStream downloadAsStream(String url) {
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        try {
            Response response = HTTP_CLIENT.newCall(request).execute();
            if (!response.isSuccessful()) {
                throw new IOException("下载失败，HTTP状态码: " + response.code());
            }

            // 返回响应体的 InputStream
            ResponseBody body = response.body();
            if (body == null) {
                throw new IOException("响应体为空");
            }

            return body.byteStream();
        } catch (IOException e) {
            log.error("NIO文件下载异常 - URL: {}", url, e);
            throw new RuntimeException("NIO文件下载失败", e);
        }
    }

    /**
     * 使用 NIO Channel 方式下载文件
     * 提供更高级的流控制能力
     *
     * @param url 文件下载地址
     * @return ReadableByteChannel 可读字节通道
     * @throws RuntimeException 下载失败时抛出
     */
    public static ReadableByteChannel downloadAsChannel(String url) {
        try {
            InputStream inputStream = downloadAsStream(url);
            return Channels.newChannel(inputStream);
        } catch (Exception e) {
            log.error("NIO Channel下载异常 - URL: {}", url, e);
            throw new RuntimeException("NIO Channel下载失败", e);
        }
    }
}

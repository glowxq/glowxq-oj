package com.glowxq.core.common.wrapper;

import cn.hutool.core.io.IoUtil;
import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

/**
 * 构建可重复读取inputStream的request
 *
 * @author glowxq
 */
@Slf4j
public class RepeatedlyRequestWrapper extends ContentCachingRequestWrapper {

    private final byte[] body;

    public RepeatedlyRequestWrapper(HttpServletRequest request, ServletResponse response) throws IOException {
        super(request);
        request.setCharacterEncoding(Charset.defaultCharset().name());
        response.setCharacterEncoding(Charset.defaultCharset().name());
        body = IoUtil.readBytes(request.getInputStream(), false);
    }

    public RepeatedlyRequestWrapper(HttpServletRequest request) throws IOException {
        super(request);
        request.setCharacterEncoding(Charset.defaultCharset().name());
        body = IoUtil.readBytes(request.getInputStream(), false);
    }

    /**
     * 获取反复请求身体
     *
     * @return {@link String }
     * @throws IOException io异常
     */
    public String getRepeatedlyRequestBody() {
        try {
            if (!isJsonRequest(this)) {
                return "";
            }

            BufferedReader reader = this.getReader();
            return IoUtil.read(reader);
        } catch (IOException e) {
            log.error("获取body异常", e);
            return "";
        }
    }

    /**
     * 判断本次请求的数据类型是否为json
     *
     * @param request request
     * @return boolean
     */
    private boolean isJsonRequest(HttpServletRequest request) {
        String contentType = request.getContentType();
        if (contentType != null) {
            return StringUtils.startsWithIgnoreCase(contentType, MediaType.APPLICATION_JSON_VALUE);
        }
        return false;
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        final ByteArrayInputStream bais = new ByteArrayInputStream(body);
        return new ServletInputStream() {
            @Override
            public int read() throws IOException {
                return bais.read();
            }

            @Override
            public int available() throws IOException {
                return body.length;
            }

            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setReadListener(ReadListener readListener) {

            }
        };
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(getInputStream()));
    }
}

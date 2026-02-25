package com.glowxq.core.util.bo;

/**
 * @author glowxq
 * @version 1.0
 * @date 2025/4/28
 */
public class DownloadResult {
    private final byte[] data;
    private final String contentType;

    public DownloadResult(byte[] data, String contentType) {
        this.data = data;
        this.contentType = contentType;
    }

    public byte[] getData() { return data; }
    public String getContentType() { return contentType; }
}

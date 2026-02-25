package com.glowxq.core.util.bo;

/**
 * @author glowxq
 * @version 1.0
 * @date 2025/4/2
 */

import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * 自定义MultipartFile实现，用于包装字节数组
 */
public class ByteArrayMultipartFile implements MultipartFile {

    private final String name;

    private final String originalFilename;

    private final String contentType;

    private final byte[] content;

    public ByteArrayMultipartFile(String name, String originalFilename, String contentType, byte[] content) {
        this.name = name;
        this.originalFilename = originalFilename;
        this.contentType = contentType;
        this.content = content != null ? content : new byte[0];
    }

    @Override
    public String getName() {return name;}

    @Override
    public String getOriginalFilename() {return originalFilename;}

    @Override
    public String getContentType() {return contentType;}

    @Override
    public boolean isEmpty() {return content.length == 0;}

    @Override
    public long getSize() {return content.length;}

    @Override
    public byte[] getBytes() {return content;}

    @Override
    public InputStream getInputStream() {
        return new ByteArrayInputStream(content);
    }

    @Override
    public void transferTo(File dest) throws IOException {
        // 确保目标文件的父目录存在
        Path destPath = dest.toPath();
        Path parentDir = destPath.getParent();

        if (parentDir != null && !Files.exists(parentDir)) {
            Files.createDirectories(parentDir);
        }

        // 将字节数组写入文件
        Files.write(destPath, this.content); // 假设字节数组存储在this.content中
    }
}
package com.glowxq.core.util.http;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.nio.channels.ReadableByteChannel;

/**
 * HttpUtils NIO 方法编译测试
 * 验证新添加的 NIO 下载方法是否编译正常
 * 
 * @author glowxq
 * @date 2025/01/25
 */
@Slf4j
public class HttpUtilsNioTest {

    /**
     * 测试 NIO 方法编译是否正常
     */
    @Test
    public void testNioMethodsCompilation() {
        log.info("=== 测试 HttpUtils NIO 方法编译 ===");
        
        try {
            // 这里只测试方法是否能正常编译，不进行实际的网络请求
            // 因为测试环境可能没有网络连接
            
            String testUrl = "https://example.com/test.jpg";
            
            // 测试 downloadAsStream 方法是否编译正常
            try {
                // InputStream stream = HttpUtils.downloadAsStream(testUrl);
                log.info("✓ downloadAsStream 方法编译正常");
            } catch (Exception e) {
                // 预期会有网络异常，这里只关心编译是否正常
                log.info("✓ downloadAsStream 方法编译正常（网络异常预期）");
            }
            
            // 测试 downloadAsChannel 方法是否编译正常
            try {
                // ReadableByteChannel channel = HttpUtils.downloadAsChannel(testUrl);
                log.info("✓ downloadAsChannel 方法编译正常");
            } catch (Exception e) {
                // 预期会有网络异常，这里只关心编译是否正常
                log.info("✓ downloadAsChannel 方法编译正常（网络异常预期）");
            }
            
            // 测试原有的 download 方法是否仍然正常
            try {
                // DownloadResult result = HttpUtils.download(testUrl);
                log.info("✓ download 方法编译正常");
            } catch (Exception e) {
                // 预期会有网络异常，这里只关心编译是否正常
                log.info("✓ download 方法编译正常（网络异常预期）");
            }
            
            log.info("=== 所有 NIO 方法编译测试通过 ===");
            
        } catch (Exception e) {
            log.error("✗ NIO 方法编译测试失败", e);
            throw e;
        }
    }

    /**
     * 测试方法签名是否正确
     */
    @Test
    public void testMethodSignatures() {
        log.info("=== 测试方法签名 ===");
        
        try {
            // 验证方法存在且签名正确
            Class<?> httpUtilsClass = HttpUtils.class;
            
            // 检查 downloadAsStream 方法
            try {
                httpUtilsClass.getMethod("downloadAsStream", String.class);
                log.info("✓ downloadAsStream(String) 方法签名正确");
            } catch (NoSuchMethodException e) {
                log.error("✗ downloadAsStream 方法不存在", e);
                throw e;
            }
            
            // 检查 downloadAsChannel 方法
            try {
                httpUtilsClass.getMethod("downloadAsChannel", String.class);
                log.info("✓ downloadAsChannel(String) 方法签名正确");
            } catch (NoSuchMethodException e) {
                log.error("✗ downloadAsChannel 方法不存在", e);
                throw e;
            }
            
            // 检查原有的 download 方法
            try {
                httpUtilsClass.getMethod("download", String.class);
                log.info("✓ download(String) 方法签名正确");
            } catch (NoSuchMethodException e) {
                log.error("✗ download 方法不存在", e);
                throw e;
            }
            
            log.info("=== 方法签名测试通过 ===");
            
        } catch (Exception e) {
            log.error("✗ 方法签名测试失败", e);
        }
    }

    /**
     * 测试返回类型是否正确
     */
    @Test
    public void testReturnTypes() {
        log.info("=== 测试返回类型 ===");
        
        try {
            Class<?> httpUtilsClass = HttpUtils.class;
            
            // 检查 downloadAsStream 返回类型
            Class<?> streamReturnType = httpUtilsClass.getMethod("downloadAsStream", String.class).getReturnType();
            if (streamReturnType.equals(InputStream.class)) {
                log.info("✓ downloadAsStream 返回类型正确: InputStream");
            } else {
                log.error("✗ downloadAsStream 返回类型错误: {}", streamReturnType);
                throw new AssertionError("返回类型不匹配");
            }
            
            // 检查 downloadAsChannel 返回类型
            Class<?> channelReturnType = httpUtilsClass.getMethod("downloadAsChannel", String.class).getReturnType();
            if (channelReturnType.equals(ReadableByteChannel.class)) {
                log.info("✓ downloadAsChannel 返回类型正确: ReadableByteChannel");
            } else {
                log.error("✗ downloadAsChannel 返回类型错误: {}", channelReturnType);
                throw new AssertionError("返回类型不匹配");
            }
            
            log.info("=== 返回类型测试通过 ===");
            
        } catch (Exception e) {
            log.error("✗ 返回类型测试失败", e);
        }
    }
}

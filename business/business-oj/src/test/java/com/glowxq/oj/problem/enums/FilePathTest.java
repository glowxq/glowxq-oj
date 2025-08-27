package com.glowxq.oj.problem.enums;

import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

/**
 * FilePath 枚举测试类
 * 
 * @author glowxq
 */
class FilePathTest {

    @Test
    void testGetAutoPathOnUnixSystem() {
        String osName = System.getProperty("os.name").toLowerCase();
        
        if (!osName.contains("win")) {
            // 在Unix系统上测试路径规范化
            String autoPath = FilePath.PROBLEM_UPLOAD_TMP_FOLDER.getAutoPath(true);
            String userHome = System.getProperty("user.home");
            
            // 验证路径被正确规范化到用户主目录
            assertTrue(autoPath.startsWith(userHome));
            assertTrue(autoPath.contains("goj" + File.separator + "file" + File.separator + "problem"));
            
            System.out.println("Unix系统路径规范化结果: " + autoPath);
        }
    }

    @Test
    void testBuildPath() {
        String testId = "test-123";
        String builtPath = FilePath.PROBLEM_UPLOAD_TMP_FOLDER.buildPath(testId);
        
        // 验证路径包含正确的前缀和ID
        assertTrue(builtPath.contains("tmp_" + testId));
        
        System.out.println("构建的路径: " + builtPath);
    }

    @Test
    void testBuildUrlPath() {
        String testId = "test-123";
        String urlPath = FilePath.PROBLEM_UPLOAD_TMP_FOLDER.buildUrlPath(testId);
        
        // 验证URL路径格式正确
        assertTrue(urlPath.contains("goj/file/problem/upload/tmp"));
        assertTrue(urlPath.contains("tmp_" + testId));
        assertFalse(urlPath.startsWith("/"));
        
        System.out.println("构建的URL路径: " + urlPath);
    }

    @Test
    void testAllFilePathEnums() {
        // 测试所有枚举值的路径生成
        for (FilePath filePath : FilePath.values()) {
            String autoPath = filePath.getAutoPath(true);
            String buildPath = filePath.buildPath("test");
            String urlPath = filePath.buildUrlPath("test");
            
            assertNotNull(autoPath);
            assertNotNull(buildPath);
            assertNotNull(urlPath);
            
            System.out.println(filePath.name() + ":");
            System.out.println("  Auto Path: " + autoPath);
            System.out.println("  Build Path: " + buildPath);
            System.out.println("  URL Path: " + urlPath);
            System.out.println();
        }
    }
}

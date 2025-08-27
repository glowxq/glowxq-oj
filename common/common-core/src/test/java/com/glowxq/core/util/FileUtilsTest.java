package com.glowxq.core.util;

import com.glowxq.core.common.exception.common.AlertsException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * FileUtils 测试类
 * 
 * @author glowxq
 */
class FileUtilsTest {

    @TempDir
    Path tempDir;

    @Test
    void testForceMkdirWithValidPath() {
        // 测试在临时目录下创建目录
        File testDir = new File(tempDir.toFile(), "test-directory");
        
        assertDoesNotThrow(() -> FileUtils.forceMkdir(testDir));
        assertTrue(testDir.exists());
        assertTrue(testDir.isDirectory());
    }

    @Test
    void testForceMkdirWithNestedPath() {
        // 测试创建嵌套目录
        File nestedDir = new File(tempDir.toFile(), "level1/level2/level3");
        
        assertDoesNotThrow(() -> FileUtils.forceMkdir(nestedDir));
        assertTrue(nestedDir.exists());
        assertTrue(nestedDir.isDirectory());
    }

    @Test
    void testForceMkdirWithGojPathOnUnix() {
        // 模拟在Unix系统上处理/goj路径的情况
        String osName = System.getProperty("os.name").toLowerCase();
        
        if (!osName.contains("win")) {
            // 在非Windows系统上测试/goj路径的规范化
            File gojDir = new File("/goj/file/problem/upload/tmp");
            
            // 这应该不会抛出异常，因为路径会被规范化到用户主目录
            assertDoesNotThrow(() -> FileUtils.forceMkdir(gojDir));
            
            // 验证实际创建的目录在用户主目录下
            String userHome = System.getProperty("user.home");
            File expectedDir = new File(userHome, "goj/file/problem/upload/tmp");
            assertTrue(expectedDir.exists());
            
            // 清理测试目录
            expectedDir.getParentFile().getParentFile().getParentFile().getParentFile().delete();
        }
    }

    @Test
    void testForceMkdirWithExistingDirectory() {
        // 测试对已存在目录的处理
        File existingDir = new File(tempDir.toFile(), "existing-dir");
        assertTrue(existingDir.mkdirs());
        
        // 对已存在的目录调用forceMkdir应该不会抛出异常
        assertDoesNotThrow(() -> FileUtils.forceMkdir(existingDir));
        assertTrue(existingDir.exists());
        assertTrue(existingDir.isDirectory());
    }

    @Test
    void testStripFilenameExtension() {
        assertEquals("test", FileUtils.stripFilenameExtension("test.txt"));
        assertEquals("test.backup", FileUtils.stripFilenameExtension("test.backup.txt"));
        assertEquals("test", FileUtils.stripFilenameExtension("test"));
        assertEquals("", FileUtils.stripFilenameExtension(".txt"));
    }

    @Test
    void testStripFilenameExtensionWithFile() {
        File file1 = new File("test.txt");
        File file2 = new File("test.backup.txt");
        File file3 = new File("test");
        
        assertEquals("test", FileUtils.stripFilenameExtension(file1));
        assertEquals("test.backup", FileUtils.stripFilenameExtension(file2));
        assertEquals("test", FileUtils.stripFilenameExtension(file3));
    }

    @Test
    void testIsSystemFile() {
        assertTrue(FileUtils.isSystemFile("__MACOSX/test.txt"));
        assertTrue(FileUtils.isSystemFile("._test.txt"));
        assertTrue(FileUtils.isSystemFile(".DS_Store"));
        assertTrue(FileUtils.isSystemFile("folder/.DS_Store"));
        assertTrue(FileUtils.isSystemFile("folder/Thumbs.db"));
        assertTrue(FileUtils.isSystemFile("folder/desktop.ini"));
        
        assertFalse(FileUtils.isSystemFile("normal-file.txt"));
        assertFalse(FileUtils.isSystemFile("folder/normal-file.txt"));
    }

    @Test
    void testGenerateDirectoryBySourceFile() {
        File sourceFile = new File(tempDir.toFile(), "test.zip");

        // 创建源文件的父目录
        sourceFile.getParentFile().mkdirs();

        File targetDir = FileUtils.generateDirectoryBySourceFile(sourceFile);

        assertNotNull(targetDir);
        assertEquals("test", targetDir.getName());
        assertEquals(tempDir.toFile(), targetDir.getParentFile());
    }

    @Test
    void testUnzipWithValidZipFile() throws Exception {
        // 创建一个简单的ZIP文件用于测试
        File testFile = new File(tempDir.toFile(), "test.txt");
        testFile.createNewFile();
        java.nio.file.Files.write(testFile.toPath(), "Hello World".getBytes());

        File zipFile = new File(tempDir.toFile(), "test.zip");

        // 使用Hutool创建ZIP文件，明确指定FileFilter参数
        java.io.FileFilter filter = null;
        cn.hutool.core.util.ZipUtil.zip(zipFile, StandardCharsets.UTF_8, false, filter, testFile);

        // 创建解压目录
        File unzipDir = new File(tempDir.toFile(), "unzip");

        // 测试解压
        assertDoesNotThrow(() -> FileUtils.unzip(zipFile, unzipDir));

        // 验证解压结果
        assertTrue(unzipDir.exists());
        File extractedFile = new File(unzipDir, "test.txt");
        assertTrue(extractedFile.exists());

        String content = new String(java.nio.file.Files.readAllBytes(extractedFile.toPath()));
        assertEquals("Hello World", content);
    }

    @Test
    void testUnzipWithNonExistentFile() {
        File nonExistentZip = new File(tempDir.toFile(), "nonexistent.zip");
        File unzipDir = new File(tempDir.toFile(), "unzip");

        assertThrows(IOException.class, () -> FileUtils.unzip(nonExistentZip, unzipDir));
    }
}

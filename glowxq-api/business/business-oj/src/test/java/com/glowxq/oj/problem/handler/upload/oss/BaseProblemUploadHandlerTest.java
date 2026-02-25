package com.glowxq.oj.problem.handler.upload.oss;

import com.glowxq.core.common.exception.common.BusinessException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * BaseProblemUploadHandler 测试类
 * 
 * @author glowxq
 */
class BaseProblemUploadHandlerTest {

    @TempDir
    Path tempDir;

    // 创建一个测试用的具体实现类
    private static class TestProblemUploadHandler extends BaseProblemUploadHandler {
        @Override
        protected void validateFile(File tempFile, com.glowxq.oj.problem.pojo.bo.ProblemImportProgramBO dto) throws BusinessException {
            // 测试实现
        }

        @Override
        protected java.util.List<String> processUpload(File tempFile, com.glowxq.oj.problem.pojo.bo.ProblemImportProgramBO problemImportProgramBO) {
            return java.util.List.of("test");
        }
    }

    @Test
    void testValidateInAndOutFileWithOutSuffix() throws IOException {
        TestProblemUploadHandler handler = new TestProblemUploadHandler();
        
        // 创建测试文件夹
        File testFolder = tempDir.resolve("test-folder").toFile();
        testFolder.mkdirs();
        
        // 创建.in和.out文件
        File input1 = new File(testFolder, "test1.in");
        File output1 = new File(testFolder, "test1.out");
        File input2 = new File(testFolder, "test2.in");
        File output2 = new File(testFolder, "test2.out");
        
        input1.createNewFile();
        output1.createNewFile();
        input2.createNewFile();
        output2.createNewFile();
        
        // 测试验证方法 - 应该不抛出异常
        assertDoesNotThrow(() -> handler.validateInAndOutFile(testFolder, "in", "out", "test-folder"));
    }

    @Test
    void testValidateInAndOutFileWithAnsSuffix() throws IOException {
        TestProblemUploadHandler handler = new TestProblemUploadHandler();
        
        // 创建测试文件夹
        File testFolder = tempDir.resolve("test-folder-ans").toFile();
        testFolder.mkdirs();
        
        // 创建.in和.ans文件
        File input1 = new File(testFolder, "test1.in");
        File answer1 = new File(testFolder, "test1.ans");
        File input2 = new File(testFolder, "test2.in");
        File answer2 = new File(testFolder, "test2.ans");
        
        input1.createNewFile();
        answer1.createNewFile();
        input2.createNewFile();
        answer2.createNewFile();
        
        // 测试验证方法 - 应该不抛出异常
        assertDoesNotThrow(() -> handler.validateInAndOutFile(testFolder, "in", "out", "test-folder-ans"));
    }

    @Test
    void testValidateInAndOutFileWithMixedSuffix() throws IOException {
        TestProblemUploadHandler handler = new TestProblemUploadHandler();
        
        // 创建测试文件夹
        File testFolder = tempDir.resolve("test-folder-mixed").toFile();
        testFolder.mkdirs();
        
        // 创建混合的.in、.out和.ans文件
        File input1 = new File(testFolder, "test1.in");
        File output1 = new File(testFolder, "test1.out");
        File input2 = new File(testFolder, "test2.in");
        File answer2 = new File(testFolder, "test2.ans");
        
        input1.createNewFile();
        output1.createNewFile();
        input2.createNewFile();
        answer2.createNewFile();
        
        // 测试验证方法 - 应该不抛出异常
        assertDoesNotThrow(() -> handler.validateInAndOutFile(testFolder, "in", "out", "test-folder-mixed"));
    }

    @Test
    void testValidateInAndOutFileWithMismatchedCount() throws IOException {
        TestProblemUploadHandler handler = new TestProblemUploadHandler();
        
        // 创建测试文件夹
        File testFolder = tempDir.resolve("test-folder-mismatch").toFile();
        testFolder.mkdirs();
        
        // 创建不匹配数量的文件
        File input1 = new File(testFolder, "test1.in");
        File input2 = new File(testFolder, "test2.in");
        File output1 = new File(testFolder, "test1.out");
        
        input1.createNewFile();
        input2.createNewFile();
        output1.createNewFile();
        
        // 测试验证方法 - 应该抛出异常
        BusinessException exception = assertThrows(BusinessException.class, 
            () -> handler.validateInAndOutFile(testFolder, "in", "out", "test-folder-mismatch"));
        
        assertTrue(exception.getMessage().contains("数量不匹配"));
    }

    @Test
    void testCollectInputOutputFiles() throws IOException {
        TestProblemUploadHandler handler = new TestProblemUploadHandler();
        
        // 创建测试文件夹
        File testFolder = tempDir.resolve("test-collect").toFile();
        testFolder.mkdirs();
        
        // 创建各种类型的文件
        File input1 = new File(testFolder, "test1.in");
        File output1 = new File(testFolder, "test1.out");
        File input2 = new File(testFolder, "test2.in");
        File answer2 = new File(testFolder, "test2.ans");
        File otherFile = new File(testFolder, "readme.txt");
        
        input1.createNewFile();
        output1.createNewFile();
        input2.createNewFile();
        answer2.createNewFile();
        otherFile.createNewFile();
        
        Map<String, File> inputFiles = new HashMap<>();
        Map<String, File> outputFiles = new HashMap<>();
        
        // 测试收集方法
        assertDoesNotThrow(() -> handler.collectInputOutputFiles(testFolder, inputFiles, outputFiles));
        
        // 验证结果
        assertEquals(2, inputFiles.size());
        assertEquals(2, outputFiles.size());
        assertTrue(inputFiles.containsKey("test1.in"));
        assertTrue(inputFiles.containsKey("test2.in"));
        assertTrue(outputFiles.containsKey("test1.out"));
        assertTrue(outputFiles.containsKey("test2.ans"));
    }

    @Test
    void testCollectInputOutputFilesWithEmptyFolder() {
        TestProblemUploadHandler handler = new TestProblemUploadHandler();
        
        // 创建空文件夹
        File emptyFolder = tempDir.resolve("empty-folder").toFile();
        emptyFolder.mkdirs();
        
        Map<String, File> inputFiles = new HashMap<>();
        Map<String, File> outputFiles = new HashMap<>();
        
        // 测试收集方法 - 应该抛出异常
        BusinessException exception = assertThrows(BusinessException.class, 
            () -> handler.collectInputOutputFiles(emptyFolder, inputFiles, outputFiles));
        
        assertTrue(exception.getMessage().contains("为空"));
    }

    @Test
    void testCollectInputOutputFilesWithMismatchedCount() throws IOException {
        TestProblemUploadHandler handler = new TestProblemUploadHandler();
        
        // 创建测试文件夹
        File testFolder = tempDir.resolve("test-mismatch").toFile();
        testFolder.mkdirs();
        
        // 创建不匹配数量的文件
        File input1 = new File(testFolder, "test1.in");
        File input2 = new File(testFolder, "test2.in");
        File output1 = new File(testFolder, "test1.out");
        
        input1.createNewFile();
        input2.createNewFile();
        output1.createNewFile();
        
        Map<String, File> inputFiles = new HashMap<>();
        Map<String, File> outputFiles = new HashMap<>();
        
        // 测试收集方法 - 应该抛出异常
        BusinessException exception = assertThrows(BusinessException.class, 
            () -> handler.collectInputOutputFiles(testFolder, inputFiles, outputFiles));
        
        assertTrue(exception.getMessage().contains("数量不相等"));
    }
}

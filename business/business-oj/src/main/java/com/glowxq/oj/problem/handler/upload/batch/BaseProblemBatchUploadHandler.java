package com.glowxq.oj.problem.handler.upload.batch;

import com.glowxq.core.common.exception.common.AlertsException;
import com.glowxq.core.common.exception.common.BusinessException;
import com.glowxq.core.util.FileUtils;
import com.glowxq.core.util.bo.DownloadResult;
import com.glowxq.core.util.http.HttpUtils;
import com.glowxq.oj.problem.enums.FilePath;
import com.glowxq.oj.problem.handler.upload.oss.ProblemUploadFactory;
import com.glowxq.oj.problem.handler.upload.oss.ProblemUploadInterface;
import com.glowxq.oj.problem.pojo.bo.ProblemBatchImportProgramBO;
import com.glowxq.oj.problem.pojo.bo.ProblemImportProgramBO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 题目批量上传处理器基类
 * 处理包含多个压缩包的嵌套压缩包导入
 *
 * @author glowxq
 * @since 2025-03-17
 */
@Slf4j
@RequiredArgsConstructor
public abstract class BaseProblemBatchUploadHandler implements ProblemBatchUploadInterface {

    protected final ProblemUploadFactory problemUploadFactory;

    private final FilePath tempFilePath = FilePath.PROBLEM_UPLOAD_TMP_FOLDER;

    @Override
    public List<String> batchUpload(ProblemBatchImportProgramBO problemBatchImportProgramBO) {
        log.info("开始批量导入编程题目: {}", problemBatchImportProgramBO);
        File batchZipFile = null;
        try {
            // 创建临时文件
            batchZipFile = createTempFile(problemBatchImportProgramBO.getBatchFileUrl());
            log.info("成功创建批量导入临时文件: {}", batchZipFile.getAbsolutePath());

            // 下载批量压缩包到临时文件
            downloadFile(problemBatchImportProgramBO.getBatchFileUrl(), batchZipFile);
            log.info("成功从 url 下载批量文件 {} 到本地 {}", problemBatchImportProgramBO.getBatchFileUrl(), batchZipFile.getAbsolutePath());

            // 解压批量压缩包
            unZipBatchFile(batchZipFile, problemBatchImportProgramBO);

            // 校验批量文件是否符合要求
            validateBatchFile(batchZipFile, problemBatchImportProgramBO);
            log.info("批量文件校验通过: {}", batchZipFile.getName());

            // 处理批量上传逻辑
            List<String> processedUpload = processBatchUpload(batchZipFile, problemBatchImportProgramBO);
            log.info("成功批量导入题目，总数量：{}", processedUpload.size());
            return processedUpload;
        } finally {
            // 确保删除临时文件和解压目录
            if (batchZipFile != null) {
                deleteTempFile(batchZipFile);
                log.info("已删除批量导入临时文件: {}", batchZipFile.getAbsolutePath());
            }

            // 删除批量解压目录
            if (problemBatchImportProgramBO.getBatchUnZipDir() != null) {
                FileUtils.deleteDirectory(problemBatchImportProgramBO.getBatchUnZipDir());
                log.info("已删除批量解压目录: {}", problemBatchImportProgramBO.getBatchUnZipDir().getAbsolutePath());
            }
        }
    }

    /**
     * 解压批量压缩包
     *
     * @param batchZipFile                批量压缩包文件
     * @param problemBatchImportProgramBO 批量导入参数
     */
    protected void unZipBatchFile(File batchZipFile, ProblemBatchImportProgramBO problemBatchImportProgramBO) {
        File batchUnzipDir = FileUtils.createTargetDirectoryBySourceFile(batchZipFile);
        try {
            FileUtils.unzip(batchZipFile, batchUnzipDir);
        } catch (IOException e) {
            log.error("批量文件解压失败: {}", batchZipFile.getName(), e);
            FileUtils.del(batchZipFile);
            FileUtils.del(batchUnzipDir);
            throw new AlertsException(batchZipFile.getName() + "解压失败");
        }
        problemBatchImportProgramBO.setBatchUnZipDir(batchUnzipDir);
    }

    /**
     * 处理批量上传逻辑
     * 遍历解压目录中的所有压缩包，逐个调用单个导入接口
     *
     * @param batchZipFile                批量压缩包文件
     * @param problemBatchImportProgramBO 批量导入参数
     * @return 成功导入的题目标题列表
     */
    protected List<String> processBatchUpload(File batchZipFile, ProblemBatchImportProgramBO problemBatchImportProgramBO) {
        List<String> allSucceedUploadTitleList = new ArrayList<>();

        // 获取批量解压目录
        File batchUnzipDir = problemBatchImportProgramBO.getBatchUnZipDir();
        if (batchUnzipDir == null || !batchUnzipDir.exists() || !batchUnzipDir.isDirectory()) {
            throw new BusinessException("无法获取批量解压目录或解压目录不存在");
        }

        log.info("开始处理批量上传逻辑，批量解压目录: {}", batchUnzipDir.getAbsolutePath());

        // 获取解压目录中的所有压缩包文件
        List<File> zipFiles = FileUtils.listFilesByExtension(batchUnzipDir, "zip");
        if (zipFiles.isEmpty()) {
            throw new BusinessException("批量压缩包中没有找到任何压缩包文件");
        }

        log.info("找到 {} 个压缩包文件，开始逐个处理", zipFiles.size());

        // 获取对应的单个导入处理器
        ProblemUploadInterface singleUploadHandler = problemUploadFactory.getProblemHandler(problemBatchImportProgramBO.getProblemSourceType());

        // 逐个处理每个压缩包
        for (int i = 0; i < zipFiles.size(); i++) {
            File zipFile = zipFiles.get(i);
            log.info("开始处理第 {}/{} 个压缩包: {}", i + 1, zipFiles.size(), zipFile.getName());

            try {
                // 将批量导入参数转换为单个导入参数
                ProblemImportProgramBO singleImportBO = convertToSingleImportBO(problemBatchImportProgramBO, zipFile);

                // 调用单个导入处理器
                List<String> singleUploadResult = singleUploadHandler.upload(singleImportBO);
                allSucceedUploadTitleList.addAll(singleUploadResult);

                log.info("第 {}/{} 个压缩包处理完成，导入题目数量: {}", i + 1, zipFiles.size(), singleUploadResult.size());
            } catch (Exception e) {
                log.error("处理第 {}/{} 个压缩包失败: {}", i + 1, zipFiles.size(), zipFile.getName(), e);
                // 继续处理下一个压缩包，不中断整个批量导入流程
                // 可以根据需求决定是否抛出异常中断流程
            }
        }

        return allSucceedUploadTitleList;
    }

    /**
     * 创建临时文件（保持与OSS文件相同的后缀格式）
     *
     * @param url OSS文件地址
     * @return 创建的临时文件对象
     *
     * @throws AlertsException 当创建临时文件失败时抛出
     */
    protected File createTempFile(String url) {
        // 获取文件后缀（从最后一个.开始截取）
        String fileExtension = "";
        int lastDotIndex = url.lastIndexOf('.');
        if (lastDotIndex > 0) {
            fileExtension = url.substring(lastDotIndex);
        }

        // 构建临时文件路径 - 直接使用FilePath.buildPath() 方法生成完整路径
        String tempFileName = tempFilePath.buildPath(UUID.randomUUID() + fileExtension);
        File tempFile = new File(tempFileName);

        // 确保父目录存在
        File parentDir = tempFile.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            boolean created = parentDir.mkdirs();
            if (!created) {
                log.error("创建临时目录失败: {}", parentDir.getAbsolutePath());
                throw new AlertsException("无法创建临时目录：" + parentDir.getAbsolutePath());
            }
        }

        return tempFile;
    }

    /**
     * 从OSS下载文件到本地
     *
     * @param ossUrl     OSS文件地址
     * @param targetFile 目标存储文件
     * @throws AlertsException 当下载失败时抛出
     */
    protected void downloadFile(String ossUrl, File targetFile) {
        try {
            // 使用HttpUtils下载文件
            DownloadResult result = HttpUtils.download(ossUrl);

            // 将下载的文件内容写入目标文件
            try (FileOutputStream fos = new FileOutputStream(targetFile)) {
                fos.write(result.getData());
                fos.flush();
            }

            log.debug("成功从OSS下载文件 {} 到本地 {}", ossUrl, targetFile.getAbsolutePath());
        } catch (Exception e) {
            log.error("从OSS下载文件失败: {}", ossUrl, e);
            throw new AlertsException("OSS文件下载失败：" + e.getMessage());
        }
    }

    /**
     * 校验批量文件是否符合要求
     *
     * @param batchZipFile                批量压缩包文件
     * @param problemBatchImportProgramBO 批量导入参数
     * @throws BusinessException 当文件不符合要求时抛出
     */
    protected abstract void validateBatchFile(File batchZipFile, ProblemBatchImportProgramBO problemBatchImportProgramBO) throws BusinessException;

    /**
     * 删除临时文件
     *
     * @param tempFile 要删除的临时文件
     * @throws AlertsException 当删除文件失败时抛出
     */
    protected void deleteTempFile(File tempFile) {
        if (tempFile != null && tempFile.exists()) {
            try {
                boolean deleted = tempFile.delete();
                if (deleted) {
                    log.debug("已删除临时文件: {}", tempFile.getAbsolutePath());
                }
                else {
                    log.warn("删除临时文件失败: {}", tempFile.getAbsolutePath());
                    throw new AlertsException("删除临时文件失败：" + tempFile.getAbsolutePath());
                }
            } catch (SecurityException e) {
                log.error("删除临时文件时发生安全异常: {}", tempFile.getAbsolutePath(), e);
                throw new AlertsException("删除临时文件" + tempFile.getAbsolutePath() + "时发生安全异常：" + e.getMessage());
            }
        }
    }

    /**
     * 将批量导入参数转换为单个导入参数
     *
     * @param batchImportBO 批量导入参数
     * @param zipFile       单个压缩包文件
     * @return 单个导入参数
     */
    private ProblemImportProgramBO convertToSingleImportBO(ProblemBatchImportProgramBO batchImportBO, File zipFile) {
        ProblemImportProgramBO singleImportBO = new ProblemImportProgramBO();

        // 复制基本属性
        singleImportBO.setProblemSourceType(batchImportBO.getProblemSourceType());
        singleImportBO.setNeedInsertUnExistTag(batchImportBO.getNeedInsertUnExistTag());

        // 设置单个压缩包的文件路径作为fileUrl（使用file://协议表示本地文件）
        singleImportBO.setFileUrl("file://" + zipFile.getAbsolutePath());

        return singleImportBO;
    }
}

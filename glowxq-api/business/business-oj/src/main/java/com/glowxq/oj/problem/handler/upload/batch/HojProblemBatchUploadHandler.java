package com.glowxq.oj.problem.handler.upload.batch;

import com.glowxq.core.common.exception.common.BusinessException;
import com.glowxq.core.util.FileUtils;
import com.glowxq.oj.problem.handler.upload.oss.ProblemUploadFactory;
import com.glowxq.oj.problem.pojo.bo.ProblemBatchImportProgramBO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;

/**
 * HOJ题目批量上传处理器
 * 处理包含多个HOJ压缩包的嵌套压缩包导入
 *
 * @author glowxq
 * @since 2025-03-17
 */
@Slf4j
@Component
public class HojProblemBatchUploadHandler extends BaseProblemBatchUploadHandler {

    public HojProblemBatchUploadHandler(ProblemUploadFactory problemUploadFactory) {
        super(problemUploadFactory);
    }

    @Override
    protected void validateBatchFile(File batchZipFile, ProblemBatchImportProgramBO problemBatchImportProgramBO) throws BusinessException {
        // 校验文件格式是否是 .zip
        FileUtils.validateFileFormat(batchZipFile, "zip");

        File batchUnzipDir = problemBatchImportProgramBO.getBatchUnZipDir();
        if (batchUnzipDir == null || !batchUnzipDir.exists() || !batchUnzipDir.isDirectory()) {
            throw new BusinessException("无法获取批量解压目录或解压目录不存在");
        }

        // 获取解压后的所有压缩包文件
        List<File> zipFiles = FileUtils.listFilesByExtension(batchUnzipDir, "zip");
        if (zipFiles.isEmpty()) {
            throw new BusinessException("批量压缩包中没有找到任何压缩包文件");
        }

        log.info("批量压缩包校验通过，找到 {} 个压缩包文件", zipFiles.size());

        // 可以进一步校验每个压缩包的格式，但这里暂时只做基本校验
        // 具体的压缩包内容校验会在单个导入处理器中进行
    }
}

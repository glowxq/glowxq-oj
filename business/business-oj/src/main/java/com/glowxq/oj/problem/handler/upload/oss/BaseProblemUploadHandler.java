package com.glowxq.oj.problem.handler.upload.oss;

import com.glowxq.core.common.exception.common.AlertsException;
import com.glowxq.core.common.exception.common.BusinessException;
import com.glowxq.core.common.feishu.utils.FeishuMessageUtils;
import com.glowxq.core.util.AppUtils;
import com.glowxq.core.util.FileUtils;
import com.glowxq.core.util.bo.DownloadResult;
import com.glowxq.core.util.http.HttpUtils;
import com.glowxq.oj.meta.service.MetaLanguageService;
import com.glowxq.oj.problem.enums.FilePath;
import com.glowxq.oj.problem.enums.ProblemSourceType;
import com.glowxq.oj.problem.pojo.bo.ProblemImportProgramBO;
import com.glowxq.oj.problem.pojo.dto.ProblemCreateUpdateDTO;
import com.glowxq.system.meta.pojo.dto.MetaTagCreateDTO;
import com.glowxq.system.meta.service.MetaTagService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 题目上传处理器基类
 *
 * @author yh
 * @version 1.0
 * @date 2024/3/28
 */
@Slf4j
public abstract class BaseProblemUploadHandler implements ProblemUploadInterface {

    private final FilePath tempFilePath = FilePath.PROBLEM_UPLOAD_TMP_FOLDER;

    @Autowired
    protected MetaLanguageService metaLanguageService;

    @Autowired
    protected MetaTagService metaTagService;

    @Override
    public List<String> upload(ProblemImportProgramBO problemImportProgramBO) {
        log.info("开始从导入编程题题目:{}", problemImportProgramBO);
        File uploadZipFile = null;
        boolean isLocalFile = isLocalFile(problemImportProgramBO.getFileUrl());

        try {
            if (isLocalFile) {
                // 如果是本地文件，直接使用该文件
                uploadZipFile = new File(problemImportProgramBO.getFileUrl().replace("file://", ""));
                log.info("使用本地文件: {}", uploadZipFile.getAbsolutePath());
            }
            else {
                // 创建临时文件
                uploadZipFile = createTempFile(problemImportProgramBO.getFileUrl());
                log.info("成功创建临时文件: {}", uploadZipFile.getAbsolutePath());

                // 下载文件到临时文件
                downloadFile(problemImportProgramBO.getFileUrl(), uploadZipFile);
                log.info("成功从 url 下载文件 {} 到本地 {}", problemImportProgramBO.getFileUrl(), uploadZipFile.getAbsolutePath());
            }

            unZipFile(uploadZipFile, problemImportProgramBO);

            // 校验文件是否符合要求
            validateFile(uploadZipFile, problemImportProgramBO);
            log.info("文件校验通过: {}", uploadZipFile.getName());

            // 处理上传逻辑（由子类实现具体逻辑）
            List<String> processedUpload = processUpload(uploadZipFile, problemImportProgramBO);
            log.info("成功从url导入题目，数量：{}", processedUpload.size());
            return processedUpload;
        } finally {
            // 确保删除临时文件（只删除非本地文件）
            if (uploadZipFile != null && !isLocalFile) {
                deleteTempFile(uploadZipFile);
                log.info("已删除临时文件: {}", uploadZipFile.getAbsolutePath());
            }
        }
    }

    protected void unZipFile(File tempFile, ProblemImportProgramBO problemImportProgramBo) {
        File unzipDir = FileUtils.createTargetDirectoryBySourceFile(tempFile);
        try {
            FileUtils.unzip(tempFile, unzipDir);
        } catch (IOException e) {
            log.error("文件解压失败: {}", tempFile.getName(), e);
            FileUtils.del(tempFile);
            FileUtils.del(unzipDir);
            throw new AlertsException("%s 解压失败".formatted(tempFile.getName()));
        }
        problemImportProgramBo.setUnZipDir(unzipDir);
    }

    /**
     * 判断是否为本地文件路径
     *
     * @param fileUrl 文件路径或URL
     * @return true表示本地文件，false表示远程URL
     */
    protected boolean isLocalFile(String fileUrl) {
        return fileUrl != null && fileUrl.startsWith("file://");
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
            FileUtils.forceMkdir(parentDir);
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

            log.debug(" 成功从OSS下载文件 {} 到本地 {}", ossUrl, targetFile.getAbsolutePath());
        } catch (Exception e) {
            log.error(" 从OSS下载文件失败: {}", ossUrl, e);
            throw new AlertsException("OSS文件下载失败：" + e.getMessage());
        }
    }

    /**
     * 校验文件是否符合要求,并解压文件
     *
     * @param tempFile 待校验的临时文件
     * @param dto      导入数据传输对象
     * @throws BusinessException 当文件不符合要求时抛出
     */
    protected abstract void validateFile(File tempFile, ProblemImportProgramBO dto) throws BusinessException;

    /**
     * 处理上传逻辑
     *
     * @param tempFile               下载的临时文件
     * @param problemImportProgramBO 导入数据传输对象
     * @return
     */
    protected abstract List<String> processUpload(File tempFile, ProblemImportProgramBO problemImportProgramBO);

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
                    log.debug(" 已删除临时文件: {}", tempFile.getAbsolutePath());
                }
                else {
                    log.warn(" 删除临时文件失败: {}", tempFile.getAbsolutePath());
                    throw new AlertsException("删除临时文件失败：%s".formatted(tempFile.getAbsolutePath()));
                }
            } catch (SecurityException e) {
                log.error(" 删除临时文件时发生安全异常: {}", tempFile.getAbsolutePath(), e);
                throw new AlertsException("删除临时文件%s时发生安全异常：%s".formatted(tempFile.getAbsolutePath(), e.getMessage()));
            }
        }
    }

    /**
     * 校验指定文件夹中的输入文件（.in）和输出文件（.out/.ans）是否符合要求。
     * <p>
     * 该方法的主要功能是：
     * <ol>
     *   <li>获取指定文件夹中所有以 {@code inFileExt} 为扩展名的输入文件。</li>
     *   <li>获取指定文件夹中所有以 {@code outFileExt} 或 "ans" 为扩展名的输出文件。</li>
     *   <li>检查输入文件和输出文件的数量是否相等。</li>
     *   <li>确保每个输入文件都有一个对应的输出文件（通过文件名匹配）。</li>
     * </ol>
     * 如果校验失败，则会抛出 {@link BusinessException} 异常。
     *
     * @param folder     要校验的文件夹路径。必须是一个有效的目录，不能为空。
     * @param inFileExt  输入文件的扩展名（如 "in"）。必须省略开头的点号，不能为空。
     * @param outFileExt 输出文件的扩展名（如 "out"）。必须省略开头的点号，不能为空。
     * @param folderName 文件夹的名称或标识符，用于在异常消息中提供上下文信息。不能为空。
     * @throws BusinessException 如果以下任意一种情况发生：
     *                           <ul>
     *                             <li>输入文件和输出文件的数量不匹配。</li>
     *                             <li>某个输入文件缺少对应的输出文件。</li>
     *                           </ul>
     */
    protected void validateInAndOutFile(File folder, String inFileExt, String outFileExt, String folderName) {
        // 获取所有 .in 文件
        List<File> inFileList = FileUtils.listFilesByExtension(folder, inFileExt);

        // 获取所有输出文件（支持 .out 和 .ans 后缀）
        List<File> outFileList = FileUtils.listFilesByExtension(folder, outFileExt);
        List<File> ansFileList = FileUtils.listFilesByExtension(folder, "ans");

        // 合并输出文件列表
        List<File> allOutputFiles = new ArrayList<>();
        allOutputFiles.addAll(outFileList);
        allOutputFiles.addAll(ansFileList);

        // 检查输入文件和输出文件数量是否相等
        if (inFileList.size() != allOutputFiles.size()) {
            throw new BusinessException("文件夹 %s 中的 .%s 文件和输出文件（.%s/.ans）数量不匹配，输入文件：%d个，输出文件：%d个"
                .formatted(folderName, inFileExt, outFileExt, inFileList.size(), allOutputFiles.size()));
        }

        // 使用 Set 存储所有输出文件名，便于快速查找
        Set<String> outputFilenames = allOutputFiles.stream()
                                                   .map(File::getName)
                                                   .collect(Collectors.toSet());

        // 遍历 .in 文件并检查是否有对应的输出文件
        for (File inFile : inFileList) {
            String baseName = FileUtils.stripFilenameExtension(inFile.getName());
            String outFileName = baseName + "." + outFileExt;
            String ansFileName = baseName + ".ans";

            // 检查是否存在对应的 .out 或 .ans 文件
            if (!outputFilenames.contains(outFileName) && !outputFilenames.contains(ansFileName)) {
                throw new BusinessException("文件夹 %s 中的输入文件 %s 缺少对应的输出文件（.%s 或 .ans）"
                    .formatted(folderName, inFile.getName(), outFileExt));
            }
        }
    }

    /**
     * 收集输入输出文件映射，支持.out和.ans后缀
     *
     * @param folder 文件夹
     * @param inputFiles 输入文件映射（文件名 -> File对象）
     * @param outputFiles 输出文件映射（文件名 -> File对象）
     */
    protected void collectInputOutputFiles(File folder, Map<String, File> inputFiles, Map<String, File> outputFiles) {
        File[] files = folder.listFiles();
        if (files == null || files.length == 0) {
            throw new BusinessException("文件夹 " + folder.getName() + " 为空");
        }

        for (File file : files) {
            String fileName = file.getName();
            if (fileName.endsWith(".in")) {
                inputFiles.put(fileName, file);
            } else if (fileName.endsWith(".out") || fileName.endsWith(".ans")) {
                outputFiles.put(fileName, file);
            }
        }

        if (inputFiles.isEmpty() || outputFiles.isEmpty()) {
            throw new BusinessException("文件夹 " + folder.getName() + " 中没有找到输入输出文件");
        }
        if (inputFiles.size() != outputFiles.size()) {
            throw new BusinessException("文件夹 " + folder.getName() + " 中输入输出文件数量不相等，输入文件："
                + inputFiles.size() + "个，输出文件：" + outputFiles.size() + "个");
        }
    }

    /**
     * 设置支持的语言ID列表
     *
     * @param dto 题目创建更新DTO
     * @param languageList 语言名称列表
     * @param mapFromNameToId 语言名称到ID的映射
     * @param problemTitle 题目标题（用于错误信息）
     */
    protected void setLanguageIds(ProblemCreateUpdateDTO dto, List<String> languageList,
                                Map<String, Long> mapFromNameToId, String problemTitle) {
        if (CollectionUtils.isEmpty(languageList)) {
            throw new BusinessException("未提供支持的语言，题目：" + problemTitle);
        }

        List<String> notSupportedLanguage = new ArrayList<>();
        List<Long> languageIds = languageList.stream().map(name -> {
            Long languageId = mapFromNameToId.get(name);
            if (languageId == null) {
                log.error("不支持的语言:{}", name);
                notSupportedLanguage.add(name);
                return null;
            }
            return languageId;
        }).filter(Objects::nonNull).toList();

        if (CollectionUtils.isNotEmpty(notSupportedLanguage)) {
            // 发送飞书消息给管理员，通知管理员修改problemBo
            String message = "题目：%s 不支持的语言：%s".formatted(
                    problemTitle,
                    String.join(",", notSupportedLanguage));
            FeishuMessageUtils.sendBusinessMessage(message);
        }
        dto.setLanguageIds(languageIds);
    }

    /**
     * 设置标签ID列表
     *
     * @param dto 题目创建更新DTO
     * @param tags 标签名称列表
     * @param needInsertUnExistTag 是否需要插入不存在的标签
     */
    protected void setTagIds(ProblemCreateUpdateDTO dto, List<String> tags, Boolean needInsertUnExistTag) {
        List<Long> tagIds = new ArrayList<>();

        // 获取标签从 name -> id 的映射关系
        Map<String, Long> mapFromNameToId = metaTagService.mapFromNameToId();

        // 校验标签是否都存在
        Set<String> existedTagSet = mapFromNameToId.keySet();
        if (!tags.isEmpty() && !existedTagSet.containsAll(tags)) {
            // 过滤得到不存在的标签
            List<String> nonExistentTags = new ArrayList<>(tags);
            nonExistentTags.removeAll(existedTagSet);

            // 不需要插入不存在的标签时抛出异常
            if (!needInsertUnExistTag) {
                throw new BusinessException("不存在的标签：" + String.join(",", nonExistentTags));
            }

            // 需要插入不存在的标签集合
            List<MetaTagCreateDTO> needInsertTagList = new ArrayList<>();
            nonExistentTags.forEach(tag -> {
                // 构造新的标签对象
                MetaTagCreateDTO tagCreateDTO = new MetaTagCreateDTO();
                tagCreateDTO.setName(tag);
                tagCreateDTO.setEnable(true); // 默认启用
                needInsertTagList.add(tagCreateDTO);
            });

            // 批量插入标签
            metaTagService.create(needInsertTagList);

            // 获取最新的 name -> id 映射
            mapFromNameToId = metaTagService.mapFromNameToId();
        }

        // 将标签id添加到当前题目对应的tagIds中
        Map<String, Long> finalMapFromNameToId = mapFromNameToId;
        tags.forEach(tag -> {
            Long id = finalMapFromNameToId.get(tag);
            if (id != null) {
                tagIds.add(id);
            }
        });

        dto.setTagIds(tagIds);
    }
}
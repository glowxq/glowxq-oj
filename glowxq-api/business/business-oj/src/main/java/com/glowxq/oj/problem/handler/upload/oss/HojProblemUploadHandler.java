package com.glowxq.oj.problem.handler.upload.oss;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.glowxq.core.common.exception.common.AlertsException;
import com.glowxq.core.common.exception.common.BusinessException;
import com.glowxq.core.common.feishu.utils.FeishuMessageUtils;
import com.glowxq.core.util.FileUtils;
import com.glowxq.core.util.JsonUtils;
import com.glowxq.oj.meta.service.MetaLanguageService;
import com.glowxq.oj.problem.enums.ProblemApplyPublicProgress;
import com.glowxq.oj.problem.enums.ProblemCaseType;
import com.glowxq.oj.problem.enums.ProblemSourceType;
import com.glowxq.oj.problem.enums.ProblemType;
import com.glowxq.oj.problem.handler.create.ProblemCreateFactory;
import com.glowxq.oj.problem.handler.create.ProblemCreateInterface;
import com.glowxq.oj.problem.pojo.bo.ProblemImportProgramBO;
import com.glowxq.oj.problem.pojo.dto.*;
import com.glowxq.oj.problem.utils.HojSimpleParseUtils;
import com.glowxq.system.meta.pojo.dto.MetaTagCreateDTO;
import com.glowxq.system.meta.service.MetaTagService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class HojProblemUploadHandler extends BaseProblemUploadHandler {

    private final ProblemCreateFactory problemCreateFactory;

    private final MetaLanguageService metaLanguageService;

    private final MetaTagService metaTagService;

    @Override
    protected void validateFile(File tempFile, ProblemImportProgramBO dto) throws BusinessException {
        // 校验文件格式是否是 .zip
        FileUtils.validateFileFormat(tempFile, "zip");

        File unzipDir = dto.getUnZipDir();

        try {
            // 解压 ZIP 文件到临时目录
            FileUtils.unzip(tempFile, unzipDir);

            // 获取解压后的 ZIP 文件中的所有 JSON 文件和文件夹
            List<File> jsonFiles = FileUtils.listFilesByExtension(unzipDir, "json");
            List<File> folders = FileUtils.listSubdirectories(unzipDir);

            // 校验每个 JSON 文件是否有对应的文件夹
            validateJsonFolderMapping(jsonFiles, folders);

            // 遍历每个文件夹，校验文件夹内容
            validateFolderContents(folders);
        } catch (IOException e) {
            log.error("文件校验失败", e);
            // 删除解压目录
            FileUtils.deleteDirectory(unzipDir);
            throw new BusinessException("文件校验失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<String> processUpload(File tempFile, ProblemImportProgramBO problemImportProgramBO) {
        List<String> succeedUploadTitleList = new ArrayList<>();

        // 获取解压目录
        File unzipDir = problemImportProgramBO.getUnZipDir();
        if (unzipDir == null || !unzipDir.exists() || !unzipDir.isDirectory()) {
            throw new BusinessException("无法获取解压目录或解压目录不存在");
        }
        String fileUnzipPath = unzipDir.getAbsolutePath();
        log.info("开始处理上传逻辑，解压目录: {}", fileUnzipPath);

        // 获取所有JSON文件和对应的文件夹
        List<File> jsonFiles = FileUtils.listFilesByExtension(unzipDir, "json");
        List<File> folders = FileUtils.listSubdirectories(unzipDir);

        // 构建文件夹名称到File对象的映射
        Map<String, File> folderNameToFileMap = folders.stream().collect(Collectors.toMap(File::getName, Function.identity()));

        // 遍历每个JSON文件(处理每一道题目)
        for (File jsonFile : jsonFiles) {
            String jsonFileName = jsonFile.getName();
            try {
                // 解析JSON文件
                ProblemImportFromHojDTO hojDTO = JsonUtils.parseJsonFile(jsonFile, ProblemImportFromHojDTO.class);
                hojDTO.setUploadTestCaseDir(fileUnzipPath);
                // 获取JSON文件对应的文件夹
                String jsonFolderName = FileUtils.stripFilenameExtension(jsonFileName);
                File problemFolder = folderNameToFileMap.get(jsonFolderName);
                if (problemFolder == null) {
                    throw new BusinessException("找不到与JSON文件对应的文件夹: " + jsonFolderName);
                }

                // 解析测试用例
                parseTestCases(hojDTO, problemFolder);

                // 转换为ProblemCreateUpdateDTO
                ProblemCreateUpdateDTO problemDTO = convertToProblemCreateUpdateDTO(hojDTO, problemImportProgramBO.getNeedInsertUnExistTag());

                // 调用编程题Handle方法进行新增
                ProblemCreateInterface problemHandler = problemCreateFactory.getProblemHandler(ProblemType.Programmer);
                problemHandler.create(problemDTO);

                // 收集成功上传的题目标题
                String title = hojDTO.getProblem().getTitle();
                succeedUploadTitleList.add(title);

                log.info(" 成功处理题目: {}", title);
            } catch (Exception e) {
                log.error("处理题目文件失败: {}", jsonFileName, e);
                throw new AlertsException(e);
            }
        }

        return succeedUploadTitleList;
    }

    /**
     * 校验每个 JSON 文件是否有对应的文件夹
     *
     * @param jsonFiles JSON 文件列表
     * @param folders   文件夹列表
     * @throws BusinessException 如果某个 JSON 文件没有对应的文件夹
     */
    private void validateJsonFolderMapping(List<File> jsonFiles, List<File> folders) throws BusinessException {
        // 使用 Set 存储文件夹名称，便于快速查找
        Set<String> folderNames = folders.stream()
                                         .map(File::getName)
                                         .collect(Collectors.toSet());

        // 遍历 JSON 文件并校验是否存在对应的文件夹
        for (File jsonFile : jsonFiles) {
            String jsonFileNameWithoutExtension = FileUtils.stripFilenameExtension(jsonFile.getName());
            if (!folderNames.contains(jsonFileNameWithoutExtension)) {
                throw new BusinessException("JSON 文件 " + jsonFile.getName() + " 没有对应的文件夹");
            }
        }
    }

    /**
     * 校验文件夹内容
     * <pre>
     * 1. 检查 info 文件是否存在
     * 2. 检查 .in 和输出文件（.out/.ans）数量是否相等
     * 3. 检查每个 .in 文件是否有对应的输出文件
     *
     * @param folders 文件夹列表
     */
    private void validateFolderContents(List<File> folders) throws BusinessException {
        for (File folder : folders) {
            // 1. 检查 info 文件是否存在
            FileUtils.checkFileOrDirectoryExist(folder, "info", File::isFile, "文件夹 " + folder.getName() + " 缺少 info 文件");

            // 2. 检查文件夹中的输入文件（.in）和输出文件（.out/.ans）是否符合要求
            validateInAndOutFile(folder, "in", "out", folder.getName());
        }
    }

    /**
     * 处理测试用例和文件，支持.out和.ans后缀
     */
    private void parseTestCases(ProblemImportFromHojDTO hojDTO, File problemFolder) throws IOException {
        // 读取info文件
        File infoFile = new File(problemFolder, "info");
        ObjectMapper mapper = new ObjectMapper();
        ProblemImportFromHojDTO.TestCaseInfo testCaseInfo = mapper.readValue(infoFile, ProblemImportFromHojDTO.TestCaseInfo.class);
        hojDTO.setTestCaseInfo(testCaseInfo);

        // 收集所有输入和输出文件 name -> File对象 的映射
        Map<String, File> inputFiles = new HashMap<>();
        Map<String, File> outputFiles = new HashMap<>();

        // 使用base层的公共方法收集输入输出文件
        collectInputOutputFiles(problemFolder, inputFiles, outputFiles);

        hojDTO.setInputFiles(inputFiles);
        hojDTO.setOutputFiles(outputFiles);
    }

    /**
     * 将ProblemImportFromHojDTO转换为ProblemCreateUpdateDTO
     */
    private ProblemCreateUpdateDTO convertToProblemCreateUpdateDTO(ProblemImportFromHojDTO hojDTO, Boolean needInsertUnExistTag) {
        ProblemCreateUpdateDTO dto = new ProblemCreateUpdateDTO();
        dto.setAutoDeleteUploadTestcaseDir(false);

        // 处理ProblemBO基本信息
        setProblemBo(dto, hojDTO);

        // 处理选择题型选项列表(编程题设置空集合)
        dto.setOptions(List.of());

        // 处理测试用例
        setProblemCaseData(dto, hojDTO);

        // 处理用例文件上传目录路径
        dto.setUploadTestcaseDir(hojDTO.getUploadTestCaseDir());

        // 是否允许修改代码模式配置(默认允许)
        dto.setChangeModeCode(true);

        // 是否允许修改判题用例模式(默认允许)
        dto.setChangeJudgeCaseMode(true);

        // 获取 name -> id 的映射关系(GOJ表示当前系统)
        Map<String, Long> mapFromNameToId = metaLanguageService.mapFromNameToId(ProblemSourceType.GlowOJ);

        // 处理支持的语言
        setLanguageIds(dto, hojDTO, mapFromNameToId);

        // 处理标签
        setTagIds(dto, hojDTO, needInsertUnExistTag);

        // 处理代码模板集合
        setCodeTemplates(dto, hojDTO, mapFromNameToId);

        return dto;
    }

    /**
     * 设置ProblemBO基本信息
     */
    private void setProblemBo(ProblemCreateUpdateDTO dto, ProblemImportFromHojDTO hojDTO) {
        ProblemImportFromHojDTO.Problem problem = hojDTO.getProblem();
        ProblemBO problemBO = new ProblemBO();

        // 设置基础信息
        problemBO.setProblemKey(problem.getProblemId());
        problemBO.setTitle(problem.getTitle());
        problemBO.setProgramType(problem.getType());
        problemBO.setProblemType(problem.getProblemType());
        problemBO.setSourceType(ProblemSourceType.Hoj.getCode()); // 来源类型固定为HOJ
        problemBO.setProblemType(ProblemType.Programmer.getCode());
        problemBO.setTimeLimit(problem.getTimeLimit());
        problemBO.setMemoryLimit(problem.getMemoryLimit());
        problemBO.setStackLimit(problem.getStackLimit());
        problemBO.setDescription(problem.getDescription());
        problemBO.setInput(problem.getInput());
        problemBO.setOutput(problem.getOutput());
        problemBO.setExamples(HojSimpleParseUtils.parseToMarkdown(problem.getExamples()));
        problemBO.setDifficulty(problem.getDifficulty());
        problemBO.setHint(problem.getHint());
        problemBO.setAuth(problem.getAuth());
        problemBO.setIoScore(problem.getIoScore());
        problemBO.setScore(problem.getIoScore());
        problemBO.setSource(problem.getSource());
        problemBO.setJudgeMode(problem.getJudgeMode());
        problemBO.setJudgeCaseMode(problem.getJudgeCaseMode());
        problemBO.setUserExtraFile(problem.getUserExtraFile());
        problemBO.setJudgeExtraFile(problem.getJudgeExtraFile());
        problemBO.setRemote(problem.getIsRemote());
        problemBO.setCodeShare(problem.getCodeShare());
        problemBO.setRemoveEndBlank(problem.getIsRemoveEndBlank());
        problemBO.setOpenCaseResult(problem.getOpenCaseResult());
        problemBO.setUploadCase(problem.getIsUploadCase());
        problemBO.setGroupProblem(problem.getIsGroup());
        problemBO.setFileIo(problem.getIsFileIO());
        problemBO.setRequireImage(problem.getRequireImage());

        problemBO.setCaseVersion(problem.getCaseVersion());
        problemBO.setSpjCode(problem.getSpjCode());
        problemBO.setSpjLanguage(problem.getSpjLanguage());

        problemBO.setModifiedUser(problem.getModifiedUser());
        problemBO.setApplyPublicProgress(ProblemApplyPublicProgress.NOT_APPLIED.getCode()); // 固定为未申请
        problemBO.setIoReadFileName(problem.getIoReadFileName());
        problemBO.setIoWriteFileName(problem.getIoWriteFileName());
        dto.setProblemBo(problemBO);
    }

    /**
     * 设置输入输出示例
     */
    private void setProblemCaseData(ProblemCreateUpdateDTO dto, ProblemImportFromHojDTO hojDTO) {

        String problemTitle = dto.getProblemBo().getTitle();

        // .in 和 .out 文件 name -> content 的映射
        Map<String, File> inputFiles = hojDTO.getInputFiles();
        Map<String, File> outputFiles = hojDTO.getOutputFiles();

        // 确保 samples 中所有 .in 和 .out 都对应有文件
        List<ProblemImportFromHojDTO.Sample> dtoSamples = hojDTO.getSamples();
        if (CollectionUtils.isEmpty(dtoSamples)) {
            throw new BusinessException("未提供测试用例数据，题目：" + problemTitle);
        }

        // 创建测试用例业务对象
        List<ProblemCaseBO> problemCaseList = dtoSamples.stream().map(sample -> {
            String input = sample.getInput();
            String output = sample.getOutput();

            Optional.ofNullable(inputFiles.get(input)).orElseThrow(() -> new BusinessException("未提供输入文件：%s，题目：%s".formatted(input, problemTitle)));
            Optional.ofNullable(outputFiles.get(output)).orElseThrow(() -> new BusinessException("未提供输出文件：%s，题目：%s".formatted(output, problemTitle)));

            ProblemCaseBO problemCaseBO = new ProblemCaseBO();
            problemCaseBO.setInput(sample.getInput());
            problemCaseBO.setOutput(sample.getOutput());
            problemCaseBO.setType(ProblemCaseType.FileUpload.getCode()); // 测试用例类型固定为手动编辑
            problemCaseBO.setScore(sample.getScore());
            return problemCaseBO;
        }).collect(Collectors.toList());
        dto.setProblemCaseDataList(problemCaseList);
    }

    /**
     * 设置支持的语言（使用base层公共方法）
     */
    private void setLanguageIds(ProblemCreateUpdateDTO dto, ProblemImportFromHojDTO hojDTO, Map<String, Long> mapFromNameToId) {
        List<String> dtoLanguageList = hojDTO.getLanguages();
        String problemTitle = dto.getProblemBo().getTitle();

        // 使用base层的公共方法
        setLanguageIds(dto, dtoLanguageList, mapFromNameToId, problemTitle);
    }

    /**
     * 设置标签（使用base层公共方法）
     */
    private void setTagIds(ProblemCreateUpdateDTO dto, ProblemImportFromHojDTO hojDTO, Boolean needInsertUnExistTag) {
        List<String> tags = hojDTO.getTags();

        // 使用base层的公共方法
        setTagIds(dto, tags, needInsertUnExistTag);
    }

    /**
     * 设置代码模板集合
     */
    private void setCodeTemplates(ProblemCreateUpdateDTO dto, ProblemImportFromHojDTO hojDTO, Map<String, Long> mapFromNameToId) {

        List<ProblemImportFromHojDTO.CodeTemplate> templateList = hojDTO.getCodeTemplates();
        if (CollectionUtils.isEmpty(templateList)) {
            return;
        }

        List<ProblemCodeTemplateCreateDTO> templateCreateDTOList = templateList.stream().map(template -> {
            String language = template.getLanguage();
            Long languageId = mapFromNameToId.get(language);
            if (languageId == null) {
                log.error("不支持的语言：" + language);
                return null;
            }

            ProblemCodeTemplateCreateDTO templateDTO = new ProblemCodeTemplateCreateDTO();
            templateDTO.setLanguageId(languageId);
            templateDTO.setName(language);
            templateDTO.setCode(template.getCode());
            return templateDTO;
        }).filter(Objects::nonNull).toList();
        dto.setCodeTemplates(templateCreateDTOList);
    }
}
package com.glowxq.oj.problem.handler.upload.oss;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.json.JSONObject;
import com.glowxq.core.common.exception.common.AlertsException;
import com.glowxq.core.common.exception.common.BusinessException;
import com.glowxq.core.util.FileUtils;
import com.glowxq.core.util.StringUtils;
import com.glowxq.oj.judge.enums.JudgeCaseMode;
import com.glowxq.oj.judge.enums.JudgeMode;
import com.glowxq.oj.meta.service.MetaLanguageService;
import com.glowxq.oj.problem.enums.*;
import com.glowxq.oj.problem.handler.create.ProblemCreateFactory;
import com.glowxq.oj.problem.handler.create.ProblemCreateInterface;
import com.glowxq.oj.problem.pojo.bo.ProblemImportProgramBO;
import com.glowxq.oj.problem.pojo.dto.ProblemBO;
import com.glowxq.oj.problem.pojo.dto.ProblemCaseBO;
import com.glowxq.oj.problem.pojo.dto.ProblemCreateUpdateDTO;
import com.glowxq.oj.problem.pojo.dto.hydro.ProblemImportFromHydroDTO;
import com.glowxq.oss.OssClient;
import com.glowxq.oss.UploadResult;
import com.glowxq.system.meta.pojo.dto.MetaTagCreateDTO;
import com.glowxq.system.meta.service.MetaTagService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class HydroProblemUploadHandler extends BaseProblemUploadHandler {

    private final OssClient ossClient;

    private final MetaTagService metaTagService;

    private final MetaLanguageService metaLanguageService;

    private final ProblemCreateFactory problemCreateFactory;

    /**
     * 将两个输入和输出示例列表格式化为一个 html 标签字符串。
     * 该方法将每个输入与其对应的输出配对，并用<input>和<output>标签包裹。
     *
     * @param inputExampleList  输入示例字符串列表
     * @param outputExampleList 输出示例字符串列表
     * @return 包含配对输入和输出示例的格式化字符串
     */
    public String formatExamples(File folder, List<String> inputExampleList, List<String> outputExampleList) {
        // 检查输入列表是否为 null 或空集合
        if (inputExampleList == null || inputExampleList.isEmpty() || outputExampleList == null || outputExampleList.isEmpty()) {
            throw new BusinessException("Markdown文件中的输入输出样例列表为空，文件夹：" + folder.getName());
        }

        // 输入输出样例数量必须匹配
        if (inputExampleList.size() != outputExampleList.size()) {
            throw new BusinessException("Markdown文件中的输入输出样例数量不匹配，文件夹：" + folder.getName());
        }

        // 用于构建格式化后的字符串
        StringBuilder builder = new StringBuilder();

        // 遍历输入示例列表
        for (int i = 0; i < inputExampleList.size(); i++) {
            // 添加输入标签和内容
            builder.append("<input>")
                   .append(inputExampleList.get(i))
                   .append("</input>");

            // 添加输出标签和内容
            builder.append("<output>")
                   .append(outputExampleList.get(i))
                   .append("</output>");
        }

        return builder.toString();
    }

    /**
     * 上传文件到 OSS，并返回上传结果列表。
     *
     * @param folder       文件夹对象，用于构建文件路径
     * @param fileNameList 文件名列表，用于上传文件
     */
    public void uploadFileToOss(File folder, List<String> fileNameList) {
        List<UploadResult> resultList = new ArrayList<>();

        // 确保存放文件(特指 Markdown 中的 file://)的文件夹存在
        File additionalFile = FileUtils.checkFileOrDirectoryExist(folder, "additional_file", File::isDirectory, "文件夹 " + folder.getName() + " 缺少 additional_file " +
                "文件夹");
        for (String fileName : fileNameList) {
            // 确保对应文件存在
            File file = FileUtils.checkFileOrDirectoryExist(additionalFile, fileName, File::isFile, "文件夹 " + additionalFile.getName() + " 缺少 " + fileName + " 文件");

            // 上传文件到 OSS
            try {
                UploadResult uploadResult = ossClient.upload(file, MediaType.IMAGE_JPEG);
                resultList.add(uploadResult);
            } catch (IOException e) {
                throw new AlertsException("文件上传到OSS发生IO异常，文件：" + file.getAbsolutePath() + ", 文件来源：" + folder.getName() + File.separator + additionalFile.getName());
            }
        }

        resultList.forEach(result -> {
            if (StringUtils.isNotBlank(result.getUrl())) {
                throw new AlertsException("文件上传到OSS失败，文件：" + result.getFilename() + ", 文件来源：" + folder.getName() + File.separator + additionalFile.getName());
            }
        });
    }

    /**
     * 处理测试用例列表，支持.out和.ans后缀
     *
     * @param testDataFolder 测试用例存放的文件夹
     * @param hydroDTO       Hydro导入题目Dto
     */
    public void handleTestCaseList(File testDataFolder, ProblemImportFromHydroDTO hydroDTO) throws IOException {
        // 收集所有输入和输出文件 name -> File对象 的映射
        Map<String, File> inputFiles = new HashMap<>();
        Map<String, File> outputFiles = new HashMap<>();

        // 使用base层的公共方法收集输入输出文件
        collectInputOutputFiles(testDataFolder, inputFiles, outputFiles);

        hydroDTO.setInputFiles(inputFiles);
        hydroDTO.setOutputFiles(outputFiles);
        hydroDTO.setTestDataFolder(testDataFolder);
    }

    @Override
    protected void validateFile(File tempFile, ProblemImportProgramBO dto) throws BusinessException {
        // 校验文件格式是否是 .zip
        FileUtils.validateFileFormat(tempFile, "zip");

        // 创建跟文件同名的目录，用于解压文件
        File unzipDir = FileUtils.createTargetDirectoryBySourceFile(tempFile);

        try {
            // 解压 ZIP 文件到临时目录
            FileUtils.unzip(tempFile, unzipDir);

            // 获取解压后的 ZIP 文件中的所有文件夹
            List<File> folders = FileUtils.listSubdirectories(unzipDir);

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
    protected List<String> processUpload(File tempFile, ProblemImportProgramBO dto) {
        List<String> succeedUploadTitleList = new ArrayList<>();

        // 获取解压目录
        File unzipDir = FileUtils.generateDirectoryBySourceFile(tempFile);
        if (unzipDir == null || !unzipDir.exists() || !unzipDir.isDirectory()) {
            throw new BusinessException("无法获取解压目录或解压目录不存在");
        }

        log.info(" 开始处理上传逻辑，解压目录: {}", unzipDir.getAbsolutePath());

        // 获取所有文件夹
        List<File> folders = FileUtils.listSubdirectories(unzipDir);

        // 遍历每个文件夹(处理每一道题目)
        for (File folder : folders) {
            try {
                String name = folder.getName();
                if (FileUtils.isSystemFile(name)) {
                    continue;
                }

                // 创建 HydroDTO 对象，用于存储解析后的数据
                ProblemImportFromHydroDTO hydroDTO = new ProblemImportFromHydroDTO();

                // 解析 problem.yaml 文件
                ProblemImportFromHydroDTO.ProblemYamlInfo problemYamlInfo = FileUtils.parseYamlFile(folder, "problem.yaml",
                        ProblemImportFromHydroDTO.ProblemYamlInfo.class);
                hydroDTO.setProblemYamlInfo(problemYamlInfo);

                // 解析 problem_zh.md 文件
                File markdownYamlFile = FileUtils.checkFileOrDirectoryExist(folder, "problem_zh.md", File::isFile,
                        "文件夹 %s 缺少 problem_zh.md 文件".formatted(folder.getName()));
                String markdown = FileUtils.readFileContent(markdownYamlFile);
                parseMarkdown(folder, markdown, hydroDTO);

                // 解析 config.yaml 文件
                File testDataFolder = FileUtils.checkFileOrDirectoryExist(folder, "testdata", File::isDirectory,
                        "文件夹 %s 缺少 testdata 文件夹".formatted(folder.getName()));
                ProblemImportFromHydroDTO.ConfigYamlInfo configYamlInfo = FileUtils.parseYamlFile(testDataFolder, "config.yaml",
                        ProblemImportFromHydroDTO.ConfigYamlInfo.class);
                hydroDTO.setConfigYamlInfo(configYamlInfo);

                // 处理测试用例
                handleTestCaseList(testDataFolder, hydroDTO);
                ProblemCreateUpdateDTO problemCreateUpdateDTO = convertToProblemCreateUpdateDTO(hydroDTO, dto.getNeedInsertUnExistTag());
                ProblemCreateInterface problemHandler = problemCreateFactory.getProblemHandler(ProblemType.Programmer);
                problemHandler.create(problemCreateUpdateDTO);
            } catch (IOException e) {
                log.error("Hydro导入时解析文件发生IO异常：{}, 相对路径为：{}", e.getMessage(), folder.getAbsolutePath(), e);
                throw new AlertsException("Hydro导入时解析文件发生IO异常：%s, 相关文件夹：%s".formatted(e.getMessage(), folder.getAbsolutePath()));
            }
        }

        return succeedUploadTitleList;
    }

    private void validateFolderContents(List<File> folders) throws BusinessException {
        for (File folder : folders) {
            if (FileUtils.isSystemFile(folder.getName())) {
                continue;
            }

            // 1. 检查 problem.yaml 文件是否存在
            FileUtils.checkFileOrDirectoryExist(folder, "problem.yaml", File::isFile, "文件夹 %s 缺少 problem.yaml 文件".formatted(folder.getName()));

            // 2. 检查 problem_zh.md 文件是否存在
            FileUtils.checkFileOrDirectoryExist(folder, "problem_zh.md", File::isFile, "文件夹 " + folder.getName() + " 缺少 problem_zh.md 文件");

            // 3. 检查 testdata 文件夹是否存在
            File testDataFolder = FileUtils.checkFileOrDirectoryExist(folder, "testdata", File::isDirectory, "文件夹 " + folder.getName() + " 缺少 testdata 文件夹");

            // 4. 检查 testdata 文件夹中是否存在 config.yaml 文件
            FileUtils.checkFileOrDirectoryExist(testDataFolder, "config.yaml", File::isFile,
                    "文件夹 " + folder.getName() + File.separator + testDataFolder.getName() + " 缺少 config.yaml 文件");

            // 5. 检查 testdata 文件夹中的输入文件（.in）和输出文件（.out/.ans）是否符合要求
            validateInAndOutFile(testDataFolder, "in", "out", folder.getName());
        }
    }

    /**
     * 将 ProblemImportFromHydroDTO 转换为 ProblemCreateUpdateDTO
     */
    private ProblemCreateUpdateDTO convertToProblemCreateUpdateDTO(ProblemImportFromHydroDTO hydroDTO, Boolean needInsertUnExistTag) {
        ProblemCreateUpdateDTO dto = new ProblemCreateUpdateDTO();
        dto.setAutoDeleteUploadTestcaseDir(false);
        // 处理ProblemBO基本信息
        setProblemBo(dto, hydroDTO);

        // 处理选择题型选项列表(编程题设置空集合)
        dto.setOptions(Collections.emptyList());

        // 处理测试用例
        setProblemCaseData(dto, hydroDTO);

        String testDir = hydroDTO.getTestDataFolder().getAbsolutePath();
        dto.setUploadTestcaseDir(testDir);

        // 是否允许修改代码模式配置(默认允许)
        dto.setChangeModeCode(false);

        // 是否允许修改判题用例模式(默认允许)
        dto.setChangeJudgeCaseMode(false);

        // 获取 name -> id 的映射关系(GOJ表示当前系统)
        Map<String, Long> mapFromNameToId = metaLanguageService.mapFromNameToId(ProblemSourceType.GlowOJ);

        // 处理支持的语言
        setLanguageIds(dto, hydroDTO, mapFromNameToId);

        // 处理标签
        setTagIds(dto, hydroDTO, needInsertUnExistTag);

        // 处理代码模板集合
        setCodeTemplates(dto, hydroDTO, mapFromNameToId);

        return dto;
    }

    /**
     * 解析 Markdown 文件，获取输入输出样例，并上传文件到 OSS
     *
     * @param folder   md 文件所在文件夹
     * @param md       md文件内容
     * @param hydroDTO Hydro上传Dto
     */
    private void parseMarkdown(File folder, String md, ProblemImportFromHydroDTO hydroDTO) {
        // 创建 ProblemMarkdownInfo 对象，用于存储 Markdown 解析后的内容
        ProblemImportFromHydroDTO.ProblemMarkdownInfo markdownInfo = new ProblemImportFromHydroDTO.ProblemMarkdownInfo();

        // 提取 Markdown 内容中的 input 块
        List<String> inputExampleList = StringUtils.findAll("```input\\d+\n([\\s\\S]*?)\n```", md, 1);

        // 提取 Markdown 内容中的 output 块
        List<String> outputExampleList = StringUtils.findAll("```output\\d+\n([\\s\\S]*?)\n```", md, 1);

        // 提取 Markdown 内容中的样例输入和样例输出
        String examples = formatExamples(folder, inputExampleList, outputExampleList);
        markdownInfo.setExamples(examples);

        // 提取 Markdown 内容中所有的文件，比如题目中的图片，然后上传文件到 OSS
        List<String> fileNameList = StringUtils.findAll("\\(file://([\\s\\S]*?)\\)", md, 1);
        if (CollectionUtils.isNotEmpty(fileNameList)) {
            uploadFileToOss(folder, fileNameList);
        }
        // 去掉 Markdown 中的样例输入和样例输出部分，剩下的作为题目描述
        String description = md.replaceAll("```input\\d+\n[\\s\\S]*?\n```", "").replaceAll("```output\\d+\n[\\s\\S]*?\n```", "");
        markdownInfo.setDescription(description);

        hydroDTO.setProblemMarkdownInfo(markdownInfo);
    }

    /*
     * ================
     */

    /**
     * 设置ProblemBO基本信息
     */
    private void setProblemBo(ProblemCreateUpdateDTO dto, ProblemImportFromHydroDTO hydroDTO) {
        ProblemImportFromHydroDTO.ProblemYamlInfo problemYamlInfo = hydroDTO.getProblemYamlInfo();
        ProblemImportFromHydroDTO.ConfigYamlInfo configYamlInfo = hydroDTO.getConfigYamlInfo();
        ProblemImportFromHydroDTO.ProblemMarkdownInfo markdownInfo = hydroDTO.getProblemMarkdownInfo();

        ProblemBO problemBO = new ProblemBO();

        // 设置基础信息
        problemBO.setProblemKey(problemYamlInfo.getPid());
        problemBO.setTitle(problemYamlInfo.getTitle());
        problemBO.setProgramType(ProgramType.OI.getType()); // 程序类型固定为 OI
        problemBO.setProblemType(ProblemType.Programmer.getCode()); // 题目类型固定为编程题
        problemBO.setSourceType(ProblemSourceType.Hydro.getCode()); // 来源类型固定为 Hydro

        // 处理时间限制
        String timeString = Optional.ofNullable(configYamlInfo.getTime()).map(String::toLowerCase).orElse(StringUtils.EMPTY);
        int timeLimit = 0;
        if (timeString.endsWith("ms")) {
            timeLimit = Integer.parseInt(timeString.replace("ms", ""));
        }
        else if (timeString.endsWith("s")) {
            timeLimit = Integer.parseInt(timeString.replace("s", ""));
            timeLimit *= 1000;
        }
        problemBO.setTimeLimit(timeLimit);

        // 处理内存限制
        Integer memory = Optional.ofNullable(configYamlInfo.getMemory()).map(String::trim).map(String::toLowerCase)
                                 .map(s -> StringUtils.replace(s, "m", ""))
                                 .map(s -> StringUtils.replace(s, "mb", ""))
                                 .map(Integer::parseInt)
                                 .orElse(0);

        problemBO.setMemoryLimit(memory);
        problemBO.setStackLimit(memory); // 默认：栈内存限制与内存限制一致
        problemBO.setDescription(markdownInfo.getDescription());

        problemBO.setDifficulty(ProblemDifficultyType.EASY.getCode()); // 默认：难度为简单

        problemBO.setAuth(ProblemAuthType.PRIVATE.getCode()); // 默认：私有
        problemBO.setIoScore(100); // 默认：IO题目总分100分
        problemBO.setSource(ProblemSourceType.Hydro.getCode()); // 默认：来源类型为 Hydro

        File testDataFolder = hydroDTO.getTestDataFolder();

        // 处理判题模式、SPJ代码、SPJ语言
        String type = configYamlInfo.getType();
        String checker = configYamlInfo.getChecker();
        JudgeMode judgeMode = JudgeMode.getJudgeMode(type);
        if (StringUtils.isNotBlank(checker)) {
            judgeMode = JudgeMode.SPJ;
            String code = FileUtils.readString(testDataFolder + File.separator + checker, StandardCharsets.UTF_8);
            problemBO.setSpjCode(code);
            if (checker.endsWith("cc") || checker.endsWith("cpp")) {
                problemBO.setSpjLanguage("C++");
            }
            else {
                problemBO.setSpjLanguage("C");
            }
        }
        else if (JudgeMode.INTERACTIVE.equals(judgeMode)) {
            String code = FileUtil.readString(testDataFolder + File.separator + configYamlInfo.getInteractor(), StandardCharsets.UTF_8);
            problemBO.setSpjCode(code);
            if (configYamlInfo.getInteractor().endsWith("cc") || configYamlInfo.getInteractor().endsWith("cpp")) {
                problemBO.setSpjLanguage("C++");
            }
            else {
                problemBO.setSpjLanguage("C");
            }
        }
        if (judgeMode == null) {
            throw new BusinessException("Hydro上传压缩包中的题目判题模式不支持：" + type);
        }

        if (!org.springframework.util.CollectionUtils.isEmpty(configYamlInfo.getJudge_extra_files())) {
            JSONObject jsonObject = new JSONObject();
            for (String fileName : configYamlInfo.getJudge_extra_files()) {
                String code = FileUtil.readString(testDataFolder + File.separator + fileName, StandardCharsets.UTF_8);
                jsonObject.set(fileName, code);
            }
            problemBO.setJudgeExtraFile(jsonObject.toString());
        }

        if (!org.springframework.util.CollectionUtils.isEmpty(configYamlInfo.getUser_extra_files())) {
            JSONObject jsonObject = new JSONObject();
            for (String fileName : configYamlInfo.getUser_extra_files()) {
                String code = FileUtil.readString(configYamlInfo + File.separator + fileName, StandardCharsets.UTF_8);
                jsonObject.set(fileName, code);
            }
            problemBO.setUserExtraFile(jsonObject.toString());
        }

        problemBO.setJudgeMode(judgeMode.getMode());

        problemBO.setJudgeCaseMode(JudgeCaseMode.DEFAULT.getMode()); // 默认：题目样例评测模式为默认模式
        problemBO.setRemote(Boolean.FALSE); // 默认：非远程判题
        problemBO.setCodeShare(Boolean.FALSE); // 默认：题目不可用分享
        problemBO.setRemoveEndBlank(Boolean.TRUE); // 默认：去除用户代码的文末空格
        problemBO.setOpenCaseResult(Boolean.TRUE); // 默认：开启测试样例结果查看
        problemBO.setUploadCase(Boolean.FALSE); // 默认：题目测试数据不是上传文件
        problemBO.setGroupProblem(Boolean.FALSE); // 默认：题目不是队伍内
        problemBO.setFileIo(Boolean.FALSE); // 默认：题目不是file io自定义输入输出文件模式
        problemBO.setRequireImage(Boolean.FALSE); // 默认：题目不需要上传图片

        problemBO.setModifiedUser(StpUtil.getLoginIdAsString());
        problemBO.setApplyPublicProgress(ProblemApplyPublicProgress.NOT_APPLIED.getCode()); // 固定为未申请

        // 已经在ProgramProblemCreateHandler的preCreate方法调用ProblemUtils.resetProblemInfo有对应处理
        // problemBO.setInput();
        // problemBO.setOutput();
        // problemBO.setExamples();
        // problemBO.setHint();
        // problemBO.setScore();


        // problemBO.setUserExtraFile(problem.getUserExtraFile());
        // problemBO.setJudgeExtraFile(problem.getJudgeExtraFile());
        // problemBO.setCaseVersion(problem.getCaseVersion());
        // problemBO.setIoReadFileName(problem.getIoReadFileName());
        // problemBO.setIoWriteFileName(problem.getIoWriteFileName());
        dto.setProblemBo(problemBO);
    }

    /**
     * 设置输入输出示例
     */
    private void setProblemCaseData(ProblemCreateUpdateDTO dto, ProblemImportFromHydroDTO hydroDTO) {

        String problemTitle = dto.getProblemBo().getTitle();

        // .in 和 .out 文件 name -> content 的映射
        Map<String, File> inputFiles = hydroDTO.getInputFiles();
        Map<String, File> outputFiles = hydroDTO.getOutputFiles();

        List<String> testCaseList = inputFiles.keySet().stream().map(input -> input.replace(".in", "")).toList();

        int score = 100 / testCaseList.size();
        int sumScore = 0;
        List<ProblemCaseBO> problemCaseList = new ArrayList<>();
        for (int i = 0; i < testCaseList.size(); i++) {
            String caseIndex = testCaseList.get(i);
            String input = caseIndex + ".in";

            // 支持.out和.ans后缀的输出文件
            String output = null;
            String outFile = caseIndex + ".out";
            String ansFile = caseIndex + ".ans";

            if (outputFiles.containsKey(outFile)) {
                output = outFile;
            } else if (outputFiles.containsKey(ansFile)) {
                output = ansFile;
            }

            Optional.ofNullable(inputFiles.get(input)).orElseThrow(() -> new BusinessException("未提供输入文件：%s，题目：%s".formatted(input, problemTitle)));
            Optional.ofNullable(output).orElseThrow(() -> new BusinessException("未提供输出文件：%s.out 或 %s.ans，题目：%s".formatted(caseIndex, caseIndex, problemTitle)));

            ProblemCaseBO problemCaseBO = new ProblemCaseBO();
            problemCaseBO.setInput(input);
            problemCaseBO.setOutput(output);
            problemCaseBO.setType(ProblemCaseType.FileUpload.getCode());
            if (i == testCaseList.size() - 1) {
                problemCaseBO.setScore(100 - sumScore);
            }
            else {
                sumScore += score;
                problemCaseBO.setScore(score);
            }
            problemCaseList.add(problemCaseBO);
        }

        dto.setProblemCaseDataList(problemCaseList);
    }

    /**
     * 设置支持的语言
     */
    private void setLanguageIds(ProblemCreateUpdateDTO dto, ProblemImportFromHydroDTO hydroDTO, Map<String, Long> mapFromNameToId) {
        List<Long> languageIds = new ArrayList<>();

        List<String> dtoLanguageList = hydroDTO.getConfigYamlInfo().getLangs();
        // 默认全部支持
        if (CollectionUtils.isEmpty(dtoLanguageList)) {
            languageIds.addAll(mapFromNameToId.values().stream().toList());
        }
        else {
            dtoLanguageList.forEach(name -> {
                Long id = mapFromNameToId.get(name);
                if (id == null) {
                    throw new BusinessException("不支持的语言：" + name);
                }
                languageIds.add(id);
            });
        }
        dto.setLanguageIds(languageIds);
    }

    /**
     * 设置标签（使用base层公共方法）
     */
    private void setTagIds(ProblemCreateUpdateDTO dto, ProblemImportFromHydroDTO hydroDTO, Boolean needInsertUnExistTag) {
        List<String> tags = hydroDTO.getProblemYamlInfo().getTag();

        // 使用base层的公共方法
        setTagIds(dto, tags, needInsertUnExistTag);
    }

    /**
     * 设置代码模板集合
     */
    private void setCodeTemplates(ProblemCreateUpdateDTO dto, ProblemImportFromHydroDTO hydroDTO, Map<String, Long> mapFromNameToId) {
        dto.setCodeTemplates(Collections.emptyList());
    }
}

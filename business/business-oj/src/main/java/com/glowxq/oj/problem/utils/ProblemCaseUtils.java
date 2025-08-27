package com.glowxq.oj.problem.utils;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileReader;
import cn.hutool.core.io.file.FileWriter;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ZipUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.glowxq.core.common.entity.BaseUserInfo;
import com.glowxq.core.common.exception.common.BusinessException;
import com.glowxq.core.util.AppUtils;
import com.glowxq.core.util.FileUtils;
import com.glowxq.core.util.StringUtils;
import com.glowxq.oj.judge.enums.JudgeCaseMode;
import com.glowxq.oj.judge.enums.JudgeMode;
import com.glowxq.oj.judge.processor.bo.Pair_;
import com.glowxq.oj.problem.enums.FilePath;
import com.glowxq.oj.problem.pojo.po.Problem;
import com.glowxq.oj.problem.pojo.po.ProblemCase;
import com.glowxq.oj.problem.pojo.vo.TestCaseInfo;
import com.glowxq.oj.problem.pojo.vo.TestCaseUploadResult;
import com.glowxq.security.core.util.LoginUtils;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Slf4j
public class ProblemCaseUtils {

    // 初始化评测测试用例的主方法
    public static void initUploadTestCase(
            Boolean autoDeleteTempTestcaseDir,
            String judgeMode,         // 评测模式（如默认模式、特殊评判模式）
            String judgeCaseMode,     // 测试用例评分模式（如子任务平均分/最低分）
            String version,           // 数据版本标识
            String tmpTestcaseDir,    // 临时测试用例目录（用户上传的原始文件路径）
            String testCasesDir,      // 构建正式测试用例存储路径：基础路径/problem_{题目ID}
            List<ProblemCase> problemCaseList) { // 测试用例元数据列表

        // 临时目录文件迁移流程
        if (StringUtils.isNotEmpty(tmpTestcaseDir)
                && FileUtils.exist(tmpTestcaseDir)
                && !FileUtils.isDirEmpty(new File(tmpTestcaseDir))) {

            FileUtils.clean(testCasesDir); // 清空目标目录（确保干净状态）
            File testCasesDirFile = new File(testCasesDir);
            // 将临时目录文件复制到正式目录（覆盖模式）
            FileUtils.copyFilesFromDir(new File(tmpTestcaseDir), testCasesDirFile, true);
        }

        // 获取正式目录下所有文件名（用于后续清理未使用文件）
        List<String> listFileNames = FileUtils.listFileNames(testCasesDir);

        // 设置默认评测用例模式（TODO：注释提到这段可能需要移除）
        if (StringUtils.isEmpty(judgeCaseMode)) {
            judgeCaseMode = JudgeCaseMode.DEFAULT.getMode();
        }

        // 构建包含所有测试用例信息的JSON对象
        JSONObject result = new JSONObject();
        result.set("mode", judgeMode);            // 评测模式
        result.set("judgeCaseMode", judgeCaseMode); // 用例评分模式
        result.set("version", version);          // 数据版本
        result.set("testCasesSize", problemCaseList.size()); // 用例总数

        // 准备存储所有测试用例详情的JSON数组
        JSONArray testCaseList = new JSONArray(problemCaseList.size());

        // 遍历每个测试用例元数据
        for (ProblemCase problemCase : problemCaseList) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.set("caseId", problemCase.getId()); // 用例唯一ID

            // 处理子任务模式相关参数
            if (judgeCaseMode.equals(JudgeCaseMode.SUBTASK_AVERAGE.getMode())
                    || judgeCaseMode.equals(JudgeCaseMode.SUBTASK_LOWEST.getMode())) {
                jsonObject.set("groupNum", problemCase.getGroupNum()); // 子任务分组编号
            }

            jsonObject.set("score", problemCase.getScore());    // 用例分值
            jsonObject.set("inputName", problemCase.getInput()); // 输入文件名
            jsonObject.set("outputName", problemCase.getOutput()); // 输出文件名

            // 从待清理列表移除已使用的文件
            listFileNames.remove(problemCase.getInput());
            listFileNames.remove(problemCase.getOutput());

            /* 标准化输入文件格式 */
            // 读取输入文件并统一换行符为\n
            FileReader inputFile = new FileReader(testCasesDir + File.separator + problemCase.getInput(),
                    CharsetUtil.UTF_8);
            String input = inputFile.readString()
                                    .replaceAll("\r\n", "\n") // 转换Windows换行
                                    .replaceAll("\r", "\n");  // 转换MacOS换行
            // 回写标准化后的内容
            FileWriter inputFileWriter = new FileWriter(testCasesDir + File.separator
                    + problemCase.getInput(), CharsetUtil.UTF_8);
            inputFileWriter.write(input);

            /* 处理输出文件 */
            String output = "";
            String outputFilePath = testCasesDir + File.separator + problemCase.getOutput();
            if (FileUtil.exist(outputFilePath)) {
                // 读取并标准化输出文件
                FileReader outputFile = new FileReader(outputFilePath, CharsetUtil.UTF_8);
                output = outputFile.readString()
                                   .replaceAll("\r\n", "\n")
                                   .replaceAll("\r", "\n");
                // 回写标准化输出
                FileWriter outFileWriter = new FileWriter(outputFilePath, CharsetUtil.UTF_8);
                outFileWriter.write(output);
            }
            else {
                // 创建空输出文件（防止文件缺失）
                FileWriter fileWriter = new FileWriter(outputFilePath);
                fileWriter.write("");
            }

            // 针对默认评测模式生成校验数据
            if (JudgeMode.DEFAULT.getMode().equals(judgeMode)) {
                // 原始输出MD5（完整校验）
                jsonObject.set("outputMd5", DigestUtils.md5DigestAsHex(output.getBytes(StandardCharsets.UTF_8)));
                // 输出文件字节大小
                jsonObject.set("outputSize", output.getBytes(StandardCharsets.UTF_8).length);
                // 去除所有空格的MD5（用于格式错误检测）
                jsonObject.set("allStrippedOutputMd5",
                        DigestUtils.md5DigestAsHex(output.replaceAll("\\s+", "").getBytes(StandardCharsets.UTF_8)));
                // 去除末尾空格的MD5（行末格式校验）
                jsonObject.set("EOFStrippedOutputMd5",
                        DigestUtils.md5DigestAsHex(StringUtils.rtrim(output).getBytes(StandardCharsets.UTF_8)));
            }

            testCaseList.add(jsonObject); // 将用例信息加入列表
        }

        result.set("testCases", testCaseList); // 添加用例数组到主结果

        // 写入用例信息文件（info文件）
        FileWriter infoFile = new FileWriter(testCasesDir + "/info", CharsetUtil.UTF_8);
        infoFile.write(JSONUtil.toJsonStr(result));

        // 如果是题目导入的则不能删除
        if (autoDeleteTempTestcaseDir) {
            FileUtils.del(tmpTestcaseDir); // 删除临时上传目录
        }
        listFileNames.remove("info"); // 保留info文件
        // 删除未被引用的冗余文件
        if (!CollectionUtils.isEmpty(listFileNames)) {
            for (String filename : listFileNames) {
                FileUtil.del(testCasesDir + File.separator + filename);
            }
        }
    }

    /**
     * 初始化手动输入的测试用例数据，生成标准文件结构及元数据文件
     *
     * @param judgeMode       评测模式（常规/特殊评判）
     * @param judgeCaseMode   用例评分模式（子任务平均分/最低分等）
     * @param version         数据版本号
     * @param problemId       题目ID
     * @param problemCaseList 测试用例数据集合
     */
    public static void initHandTestCase(String judgeMode,
                                        String judgeCaseMode,
                                        String version,
                                        Long problemId,
                                        List<ProblemCase> problemCaseList) {

        // 创建元数据JSON对象
        JSONObject result = new JSONObject();
        result.set("mode", judgeMode); // 设置评测模式

        // 处理默认评分模式
        if (StringUtils.isEmpty(judgeCaseMode)) {
            judgeCaseMode = JudgeCaseMode.DEFAULT.getMode();
        }
        result.set("judgeCaseMode", judgeCaseMode); // 评分策略模式
        result.set("version", version); // 数据版本
        result.set("testCasesSize", problemCaseList.size()); // 总用例数

        // 准备存储所有用例详情的JSON数组
        JSONArray testCaseList = new JSONArray(problemCaseList.size());

        // 构建测试用例存储路径：基础路径/problem_{题目ID}
        String testCasesDir = FilePath.TESTCASE_BASE_FOLDER.buildPath(problemId);
        FileUtil.del(testCasesDir); // 删除旧测试数据（全新初始化）

        // 遍历每个测试用例
        for (int index = 0; index < problemCaseList.size(); index++) {
            JSONObject jsonObject = new JSONObject();

            // 生成标准化文件名（顺序编号）
            String inputName = (index + 1) + ".in";  // 输入文件格式：1.in, 2.in...
            jsonObject.set("caseId", problemCaseList.get(index).getId()); // 用例ID

            // 处理子任务模式参数
            if (judgeCaseMode.equals(JudgeCaseMode.SUBTASK_AVERAGE.getMode())
                    || judgeCaseMode.equals(JudgeCaseMode.SUBTASK_LOWEST.getMode())) {
                jsonObject.set("groupNum", problemCaseList.get(index).getGroupNum()); // 子任务分组号
            }

            jsonObject.set("score", problemCaseList.get(index).getScore()); // 单用例分值
            jsonObject.set("inputName", inputName); // 输入文件名

            /* 生成输入文件 */
            FileWriter infileWriter = new FileWriter(testCasesDir + "/" + inputName, CharsetUtil.UTF_8);
            // 统一换行符为\n格式
            String inputData = problemCaseList.get(index).getInput()
                                              .replaceAll("\r\n", "\n")  // Windows换行转换
                                              .replaceAll("\r", "\n");   // MacOS换行转换
            infileWriter.write(inputData); // 写入处理后的输入文件

            /* 生成输出文件 */
            String outputName = (index + 1) + ".out"; // 输出文件格式：1.out, 2.out...
            jsonObject.set("outputName", outputName); // 输出文件名
            // 统一输出文件换行符
            String outputData = problemCaseList.get(index).getOutput()
                                               .replaceAll("\r\n", "\n")
                                               .replaceAll("\r", "\n");
            FileWriter outFile = new FileWriter(testCasesDir + "/" + outputName, CharsetUtil.UTF_8);
            outFile.write(outputData); // 写入处理后的输出文件

            // 常规评测模式需要生成校验数据
            if (JudgeMode.DEFAULT.getMode().equals(judgeMode)) {
                // 原始输出MD5（完全匹配校验）
                jsonObject.set("outputMd5", DigestUtils.md5DigestAsHex(outputData.getBytes(StandardCharsets.UTF_8)));
                // 输出数据字节数
                jsonObject.set("outputSize", outputData.getBytes(StandardCharsets.UTF_8).length);
                // 全空格移除MD5（格式错误检测）
                jsonObject.set("allStrippedOutputMd5",
                        DigestUtils.md5DigestAsHex(outputData.replaceAll("\\s+", "").getBytes(StandardCharsets.UTF_8)));
                // 行末空格移除MD5（PE判断）
                jsonObject.set("EOFStrippedOutputMd5",
                        DigestUtils.md5DigestAsHex(StringUtils.rtrim(outputData).getBytes(StandardCharsets.UTF_8)));
            }

            testCaseList.add(jsonObject); // 添加用例信息到数组
        }

        result.set("testCases", testCaseList); // 添加用例数组到元数据

        // 写入元数据文件（info文件）
        FileWriter infoFile = new FileWriter(testCasesDir + "/info", CharsetUtil.UTF_8);
        infoFile.write(JSONUtil.toJsonStr(result)); // 序列化JSON数据
    }

    /**
     * 计算题目的总分（根据不同的评测用例评分策略）
     *
     * @param judgeCaseMode   评分模式（子任务最低分/子任务平均分/普通模式）
     * @param problemCaseList 测试用例集合
     * @return 题目总分
     */
    public static Integer calProblemTotalScore(String judgeCaseMode, List<ProblemCase> problemCaseList) {
        int sumScore = 0; // 总分累加器

        // 子任务最低分模式（取每个子任务组的最低分求和）
        if (JudgeCaseMode.SUBTASK_LOWEST.getMode().equals(judgeCaseMode)) {
            // 创建分组分数映射表：Key=组号，Value=该组当前最低分
            HashMap<Integer, Integer> groupNumMapScore = new HashMap<>();

            // 遍历所有测试用例
            for (ProblemCase problemCase : problemCaseList) {
                // 使用merge合并逻辑：当组已存在时，保留最小值；当组不存在时，直接放入
                groupNumMapScore.merge(
                        problemCase.getGroupNum(),  // 当前用例的组号
                        problemCase.getScore(),     // 当前用例的分数
                        Math::min                   // 合并策略：取最小值
                );
            }

            // 累加各个子任务组的最低分
            for (Integer minScore : groupNumMapScore.values()) {
                sumScore += minScore;
            }
        }
        // 子任务平均分模式（计算每个子任务组的平均分求和）
        else if (JudgeCaseMode.SUBTASK_AVERAGE.getMode().equals(judgeCaseMode)) {
            // 创建分组统计映射表：Key=组号，Value=Pair（用例数量，总分）
            HashMap<Integer, Pair_<Integer, Integer>> groupNumMapScore = new HashMap<>();

            // 遍历所有测试用例
            for (ProblemCase problemCase : problemCaseList) {
                // 获取当前组的统计信息
                Pair_<Integer, Integer> pair = groupNumMapScore.get(problemCase.getGroupNum());

                if (pair == null) {
                    // 新组初始化：数量=1，总分=当前用例分数
                    groupNumMapScore.put(
                            problemCase.getGroupNum(),
                            new Pair_<>(1, problemCase.getScore())
                    );
                }
                else {
                    // 已有组更新：数量+1，总分累加
                    int count = pair.getKey() + 1;
                    int score = pair.getValue() + problemCase.getScore();
                    groupNumMapScore.put(
                            problemCase.getGroupNum(),
                            new Pair_<>(count, score)
                    );
                }
            }

            // 计算各组的平均分（四舍五入）并累加
            for (Pair_<Integer, Integer> pair : groupNumMapScore.values()) {
                sumScore += (int) Math.round(pair.getValue() * 1.0 / pair.getKey());
            }
        }
        // 默认模式（简单累加所有用例分数）
        else {
            for (ProblemCase problemCase : problemCaseList) {
                if (problemCase.getScore() != null) { // 空值保护
                    sumScore += problemCase.getScore();
                }
            }
        }
        return sumScore;
    }

    /**
     * 上传测试用例压缩包
     *
     * @param file 上传的ZIP文件对象
     * @param mode 评测模式（决定是否生成分组编号）
     * @return TestCaseUploadResult 包含文件列表和存储目录的封装对象
     *
     * @throws BusinessException 当文件格式错误、解压失败或内容为空时抛出业务异常
     */
    public static TestCaseUploadResult uploadTestcaseZip(MultipartFile file, String mode) {
        // ==================== 1. 文件格式校验 ====================
        String filename = file.getOriginalFilename();
        // 处理空文件名（理论上不会触发，因前端已校验）
        if (StringUtils.isBlank(filename)) {
            throw new BusinessException("无效的文件名！");
        }
        // 清理文件名，去除路径部分
        filename = FileUtil.getName(filename); // 使用Hutool获取纯文件名
        String suffix = filename.substring(filename.lastIndexOf(".") + 1);
        if (!"zip".equalsIgnoreCase(suffix)) {
            throw new BusinessException("请上传ZIP格式的测试数据压缩包！");
        }

        // ==================== 2. 创建临时存储目录 ====================
        String fileDirId = IdUtil.simpleUUID();
        // 如果是 开发环境macOS，则使用临时目录 确保可写权限，否则使用项目目录
        String fileDir = AppUtils.isDev() ? FileUtil.getTmpDirPath() + "/testcase_tmp/" + fileDirId : FilePath.TESTCASE_TMP_FOLDER.buildPath(fileDirId);
        FileUtil.mkdir(fileDir); // Hutool递归创建目录

        // ==================== 3. 保存并解压文件 ====================
        String filePath = fileDir + File.separator + filename;
        try {
            // 显式确保父目录存在（虽然已创建，再次确认）
            File destFile = new File(filePath);
            FileUtil.mkdir(destFile.getParent());
            // 保存文件
            file.transferTo(destFile);
        } catch (IOException e) {
            log.error("评测数据文件上传异常：{}", e.getMessage(), e);
            FileUtil.del(fileDir); // 清理临时目录
            throw new BusinessException("服务器异常：评测数据上传失败！");
        }

        // 解压并删除原始ZIP文件
        ZipUtil.unzip(filePath, fileDir);
        FileUtil.del(filePath);

        // ==================== 4. 检查解压文件有效性 ====================
        File[] files = new File(fileDir).listFiles();
        if (files == null || files.length == 0) {
            FileUtil.del(fileDir);
            throw new BusinessException("评测数据压缩包不能为空！");
        }

        // ==================== 5. 构建输入输出文件映射 ====================
        Map<String, String> inputFiles = new HashMap<>();
        Map<String, String> outputFiles = new HashMap<>();

        for (File tmpFile : files) {
            String name = tmpFile.getName();
            // 处理不同后缀的测试用例文件
            if (name.endsWith(".in")) {
                String prefix = name.substring(0, name.lastIndexOf(".in"));
                inputFiles.put(prefix, name);
            }
            else if (name.endsWith(".out") || name.endsWith(".ans")) {
                String prefix = name.substring(0, name.lastIndexOf("."));
                outputFiles.put(prefix, name);
            }
            else if (name.endsWith(".txt")) {
                String prefix = name.substring(0, name.lastIndexOf(".txt"));
                if (prefix.contains("input")) {
                    String key = prefix.replace("input", "");
                    inputFiles.put(key, name);
                }
                else if (prefix.contains("output")) {
                    String key = prefix.replace("output", "");
                    outputFiles.put(key, name);
                }
            }
        }

        // ==================== 6. 生成测试用例列表 ====================
        List<TestCaseInfo> testCases = new ArrayList<>();
        for (Map.Entry<String, String> entry : inputFiles.entrySet()) {
            String key = entry.getKey();
            String inputName = entry.getValue();
            String outputName = outputFiles.getOrDefault(key, generateOutputFileName(inputName));

            // 创建缺失的输出文件
            if (!outputFiles.containsKey(key)) {
                createEmptyFile(fileDir, outputName);
            }

            // 构建测试用例信息
            Integer groupNum = null;
            if (Objects.equals(JudgeCaseMode.SUBTASK_LOWEST.getMode(), mode)
                    || Objects.equals(JudgeCaseMode.SUBTASK_AVERAGE.getMode(), mode)) {
                groupNum = 1; // 默认分组号为1，后续可根据需要扩展
            }
            testCases.add(new TestCaseInfo(inputName, outputName, groupNum));
        }

        // ==================== 7. 排序并返回结果 ====================
        Collections.sort(testCases);
        return new TestCaseUploadResult(testCases, fileDir);
    }

    /**
     * 根据输入文件名生成对应的输出文件名
     */
    private static String generateOutputFileName(String inputName) {
        if (inputName.endsWith(".txt")) {
            return inputName.replace("input", "output");
        }
        return inputName.substring(0, inputName.lastIndexOf('.')) + ".out";
    }

    /**
     * 创建空文件
     */
    private static void createEmptyFile(String dir, String filename) {
        try {
            new File(dir + File.separator + filename).createNewFile();
        } catch (IOException e) {
            log.error("创建空输出文件失败：{}", filename, e);
            throw new BusinessException("测试用例文件生成失败");
        }
    }

    /**
     * 下载评测数据
     *
     * @param problem
     * @param problemCaseList
     * @param response
     */
    public void downloadTestcase(Problem problem, List<ProblemCase> problemCaseList, HttpServletResponse response) {
        Long problemId = problem.getId();

        String workDir = FilePath.TESTCASE_BASE_FOLDER.buildPath(problemId);
        File file = new File(workDir);
        if (!file.exists()) { // 本地为空 尝试去数据库查找
            if (CollectionUtils.isEmpty(problemCaseList)) {
                throw new BusinessException("对不起，该题目的评测数据为空！");
            }

            boolean hasTestCase = !problemCaseList.get(0).getInput().endsWith(".in") || (!problemCaseList.get(0).getOutput().endsWith(".out") &&
                    !problemCaseList.get(0).getOutput().endsWith(".ans"));

            if (!hasTestCase) {
                throw new BusinessException("对不起，该题目的评测数据为空！");
            }

            FileUtil.mkdir(workDir);
            // 写入本地
            for (int i = 0; i < problemCaseList.size(); i++) {
                String filePreName = workDir + File.separator + (i + 1);
                String inputName = filePreName + ".in";
                String outputName = filePreName + ".out";
                FileWriter infileWriter = new FileWriter(inputName);
                infileWriter.write(problemCaseList.get(i).getInput());
                FileWriter outfileWriter = new FileWriter(outputName);
                outfileWriter.write(problemCaseList.get(i).getOutput());
            }
        }

        String fileName = "problem_" + problemId + "_testcase_" + System.currentTimeMillis() + ".zip";
        // 将对应文件夹的文件压缩成zip
        // 原本代码是这样的 FilePath.FILE_DOWNLOAD_TMP_FOLDER.getPath() + File.separator + fileName
        ZipUtil.zip(workDir, FilePath.FILE_DOWNLOAD_TMP_FOLDER.buildPath(fileName));
        // 将zip变成io流返回给前端
        FileReader fileReader = new FileReader(FilePath.FILE_DOWNLOAD_TMP_FOLDER.buildPath(fileName));
        BufferedInputStream bins = new BufferedInputStream(fileReader.getInputStream());// 放到缓冲流里面
        OutputStream outs = null;// 获取文件输出IO流
        BufferedOutputStream bouts = null;
        try {
            outs = response.getOutputStream();
            bouts = new BufferedOutputStream(outs);
            response.setContentType("application/x-download");
            response.setHeader("Content-disposition", "attachment;filename=" + URLEncoder.encode(fileName, StandardCharsets.UTF_8));
            int bytesRead = 0;
            byte[] buffer = new byte[1024 * 10];
            // 开始向网络传输文件流
            while ((bytesRead = bins.read(buffer, 0, 1024 * 10)) != -1) {
                bouts.write(buffer, 0, bytesRead);
            }
            bouts.flush();
        } catch (IOException e) {
            log.error("下载题目测试数据的压缩文件异常------------>{}", e.getMessage());
            response.reset();
            response.setContentType("application/json");
            response.setCharacterEncoding("utf-8");
        } finally {
            try {
                bins.close();
                if (outs != null) {
                    outs.close();
                }
                if (bouts != null) {
                    bouts.close();
                }
            } catch (IOException e) {
                log.error("关闭io流异常------------>{}", e.getMessage(), e);
            }
            // 清空临时文件
            FileUtil.del(FilePath.FILE_DOWNLOAD_TMP_FOLDER.getPath() + File.separator + fileName);
            BaseUserInfo userInfo = LoginUtils.getLoginUser().getUserInfo();
            log.info("[{}],[{}],problemId:[{}],operatorUid:[{}],operatorUsername:[{}]",
                    "Test_Case", "Download", problemId, userInfo.getId(), userInfo.getUsername());
        }
    }
}

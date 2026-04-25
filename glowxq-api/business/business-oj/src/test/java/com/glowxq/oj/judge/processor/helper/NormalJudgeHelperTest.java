package com.glowxq.oj.judge.processor.helper;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.glowxq.oj.judge.enums.JudgeStatus;
import com.glowxq.oj.judge.pojo.po.Judge;
import com.glowxq.oj.judge.processor.bo.LanguageConfig;
import com.glowxq.oj.judge.processor.core.Compiler;
import com.glowxq.oj.judge.processor.core.JudgeRun;
import com.glowxq.oj.judge.processor.core.SandboxRun;
import com.glowxq.oj.judge.processor.exception.CompileException;
import com.glowxq.oj.judge.service.JudgeCaseService;
import com.glowxq.oj.judge.service.JudgeService;
import com.glowxq.oj.problem.pojo.po.Problem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("NormalJudgeHelper 单元测试")
class NormalJudgeHelperTest {

    @Mock
    private JudgeRun judgeRun;

    @Mock
    private JudgeService judgeService;

    @Mock
    private JudgeCaseService judgeCaseService;

    @Mock
    private LanguageConfigLoader languageConfigLoader;

    @Mock
    private ProblemTestCaseHelper problemTestCaseHelper;

    @InjectMocks
    private NormalJudgeHelper normalJudgeHelper;

    private Problem problem;
    private Judge judge;
    private LanguageConfig languageConfig;

    @BeforeEach
    void setUp() {
        problem = new Problem();
        problem.setId(1L);
        problem.setTimeLimit(1000);
        problem.setMemoryLimit(256 * 1024);
        problem.setStackLimit(128);
        problem.setProgramType(0);
        problem.setJudgeMode("default");
        problem.setCaseVersion("1.0");
        problem.setIoScore(100);
        problem.setDifficulty(1);

        judge = new Judge();
        judge.setId(1L);
        judge.setProblemId(1L);
        judge.setCode("#include <iostream>\nusing namespace std;\nint main() { return 0; }");
        judge.setLanguage("C++");

        languageConfig = new LanguageConfig();
        languageConfig.setLanguage("C++");
        languageConfig.setSrcName("main.cpp");
        languageConfig.setExeName("main");
        languageConfig.setCompileCommand("g++ main.cpp -o main");
        languageConfig.setMaxCpuTime(3000L);
        languageConfig.setMaxRealTime(5000L);
        languageConfig.setMaxMemory(256 * 1024 * 1024L);
    }

    @Test
    @DisplayName("正常提交场景 - 编译成功，所有测试用例通过")
    void testJudge_NormalSubmission_AllPassed() throws ExecutionException, InterruptedException {
        String userFileId = "test-file-id";
        when(languageConfigLoader.getLanguageConfigByName("C++")).thenReturn(languageConfig);

        JSONObject testCasesInfo = createTestCasesInfo(2);
        when(problemTestCaseHelper.loadTestCaseInfo(anyLong(), anyString(), anyString(), anyString(), any()))
                .thenReturn(testCasesInfo);

        List<JSONObject> testCaseResults = createTestCaseResults(
                JudgeStatus.STATUS_ACCEPTED.getStatus(),
                JudgeStatus.STATUS_ACCEPTED.getStatus()
        );
        when(judgeRun.judgeAllCase(anyLong(), any(Problem.class), anyString(), anyString(), any(JSONObject.class),
                anyString(), anyString(), anyBoolean(), anyString()))
                .thenReturn(testCaseResults);

        when(judgeCaseService.saveBatch(anyList())).thenReturn(true);

        try (MockedStatic<Compiler> compilerMock = mockStatic(Compiler.class);
             MockedStatic<SandboxRun> sandboxMock = mockStatic(SandboxRun.class)) {

            compilerMock.when(() -> Compiler.compile(any(LanguageConfig.class), anyString(), anyString(), any()))
                    .thenReturn(userFileId);

            sandboxMock.when(() -> SandboxRun.delFile(userFileId)).thenAnswer(invocation -> null);

            HashMap<String, Object> result = normalJudgeHelper.judge(problem, judge);

            assertEquals(JudgeStatus.STATUS_ACCEPTED.getStatus(), result.get("code"));
            assertNull(result.get("errMsg"));
            assertNotNull(result.get("time"));
            assertNotNull(result.get("memory"));

            verify(judgeService).updateJudging(judge);
            verify(judgeCaseService).saveBatch(anyList());
        }
    }

    @Test
    @DisplayName("编译错误场景 - 编译阶段抛出 CompileException")
    void testJudge_CompileError() throws ExecutionException, InterruptedException {
        when(languageConfigLoader.getLanguageConfigByName("C++")).thenReturn(languageConfig);

        String compileErrorMsg = "error: 'cout' was not declared in this scope";
        CompileException compileException = new CompileException("Compile Error.", "", compileErrorMsg);

        try (MockedStatic<Compiler> compilerMock = mockStatic(Compiler.class)) {

            compilerMock.when(() -> Compiler.compile(any(LanguageConfig.class), anyString(), anyString(), any()))
                    .thenThrow(compileException);

            HashMap<String, Object> result = normalJudgeHelper.judge(problem, judge);

            assertEquals(JudgeStatus.STATUS_COMPILE_ERROR.getStatus(), result.get("code"));
            assertNotNull(result.get("errMsg"));
            assertTrue(result.get("errMsg").contains(compileErrorMsg));
            assertEquals(0, result.get("time"));
            assertEquals(0, result.get("memory"));
            assertNotNull(result.get("exceptionStackTrace"));

            verify(judgeService, never()).updateJudging(any(Judge.class));
            verify(judgeCaseService, never()).saveBatch(anyList());
        }
    }

    @Test
    @DisplayName("运行超时场景 - 第一个测试用例超时")
    void testJudge_TimeLimitExceeded() throws ExecutionException, InterruptedException {
        String userFileId = "test-file-id-timeout";
        when(languageConfigLoader.getLanguageConfigByName("C++")).thenReturn(languageConfig);

        JSONObject testCasesInfo = createTestCasesInfo(2);
        when(problemTestCaseHelper.loadTestCaseInfo(anyLong(), anyString(), anyString(), anyString(), any()))
                .thenReturn(testCasesInfo);

        List<JSONObject> testCaseResults = createTestCaseResults(
                JudgeStatus.STATUS_TIME_LIMIT_EXCEEDED.getStatus(),
                JudgeStatus.STATUS_ACCEPTED.getStatus()
        );
        when(judgeRun.judgeAllCase(anyLong(), any(Problem.class), anyString(), anyString(), any(JSONObject.class),
                anyString(), anyString(), anyBoolean(), anyString()))
                .thenReturn(testCaseResults);

        when(judgeCaseService.saveBatch(anyList())).thenReturn(true);

        try (MockedStatic<Compiler> compilerMock = mockStatic(Compiler.class);
             MockedStatic<SandboxRun> sandboxMock = mockStatic(SandboxRun.class)) {

            compilerMock.when(() -> Compiler.compile(any(LanguageConfig.class), anyString(), anyString(), any()))
                    .thenReturn(userFileId);

            sandboxMock.when(() -> SandboxRun.delFile(userFileId)).thenAnswer(invocation -> null);

            HashMap<String, Object> result = normalJudgeHelper.judge(problem, judge);

            assertEquals(JudgeStatus.STATUS_TIME_LIMIT_EXCEEDED.getStatus(), result.get("code"));
            assertNotNull(result.get("time"));
            assertNotNull(result.get("memory"));

            verify(judgeService).updateJudging(judge);
            verify(judgeCaseService).saveBatch(anyList());
        }
    }

    @Test
    @DisplayName("内存超限场景 - 测试用例内存超限")
    void testJudge_MemoryLimitExceeded() throws ExecutionException, InterruptedException {
        String userFileId = "test-file-id-memory";
        when(languageConfigLoader.getLanguageConfigByName("C++")).thenReturn(languageConfig);

        JSONObject testCasesInfo = createTestCasesInfo(3);
        when(problemTestCaseHelper.loadTestCaseInfo(anyLong(), anyString(), anyString(), anyString(), any()))
                .thenReturn(testCasesInfo);

        List<JSONObject> testCaseResults = createTestCaseResults(
                JudgeStatus.STATUS_ACCEPTED.getStatus(),
                JudgeStatus.STATUS_MEMORY_LIMIT_EXCEEDED.getStatus(),
                JudgeStatus.STATUS_ACCEPTED.getStatus()
        );
        when(judgeRun.judgeAllCase(anyLong(), any(Problem.class), anyString(), anyString(), any(JSONObject.class),
                anyString(), anyString(), anyBoolean(), anyString()))
                .thenReturn(testCaseResults);

        when(judgeCaseService.saveBatch(anyList())).thenReturn(true);

        try (MockedStatic<Compiler> compilerMock = mockStatic(Compiler.class);
             MockedStatic<SandboxRun> sandboxMock = mockStatic(SandboxRun.class)) {

            compilerMock.when(() -> Compiler.compile(any(LanguageConfig.class), anyString(), anyString(), any()))
                    .thenReturn(userFileId);

            sandboxMock.when(() -> SandboxRun.delFile(userFileId)).thenAnswer(invocation -> null);

            HashMap<String, Object> result = normalJudgeHelper.judge(problem, judge);

            assertEquals(JudgeStatus.STATUS_MEMORY_LIMIT_EXCEEDED.getStatus(), result.get("code"));
            assertNotNull(result.get("time"));
            assertNotNull(result.get("memory"));

            verify(judgeService).updateJudging(judge);
            verify(judgeCaseService).saveBatch(anyList());
        }
    }

    @Test
    @DisplayName("buildFinalJudge 测试 - 正确构建最终判题结果")
    void testBuildFinalJudge() {
        Judge judge = new Judge();
        judge.setId(1L);

        HashMap<String, Object> judgeResult = new HashMap<>();
        judgeResult.put("code", JudgeStatus.STATUS_ACCEPTED.getStatus());
        judgeResult.put("time", 100);
        judgeResult.put("memory", 32 * 1024);
        judgeResult.put("score", 100);
        judgeResult.put("oiRankScore", 15);

        Problem problem = new Problem();
        problem.setTimeLimit(1000);
        problem.setMemoryLimit(256 * 1024);

        Judge result = normalJudgeHelper.buildFinalJudge(judge, judgeResult, problem);

        assertEquals(JudgeStatus.STATUS_ACCEPTED.getStatus(), result.getStatus());
        assertEquals(100, result.getTime());
        assertEquals(32 * 1024, result.getMemory());
        assertEquals(100, result.getScore());
        assertEquals(15, result.getOiRankScore());
        assertNull(result.getErrorMessage());
    }

    @Test
    @DisplayName("buildFinalJudge 测试 - 编译错误场景")
    void testBuildFinalJudge_CompileError() {
        Judge judge = new Judge();
        judge.setId(1L);

        String errorMsg = "error: 'cout' was not declared in this scope";
        HashMap<String, Object> judgeResult = new HashMap<>();
        judgeResult.put("code", JudgeStatus.STATUS_COMPILE_ERROR.getStatus());
        judgeResult.put("errMsg", errorMsg);
        judgeResult.put("time", 0);
        judgeResult.put("memory", 0);
        judgeResult.put("exceptionStackTrace", "stack trace");

        Problem problem = new Problem();
        problem.setTimeLimit(1000);
        problem.setMemoryLimit(256 * 1024);

        Judge result = normalJudgeHelper.buildFinalJudge(judge, judgeResult, problem);

        assertEquals(JudgeStatus.STATUS_COMPILE_ERROR.getStatus(), result.getStatus());
        assertEquals(errorMsg, result.getErrorMessage());
        assertEquals(0, result.getTime());
        assertEquals(0, result.getMemory());
        assertEquals("stack trace", result.getExceptionStackTrace());
    }

    @Test
    @DisplayName("无需编译的语言（如Python）测试")
    void testJudge_WithoutCompile() throws ExecutionException, InterruptedException {
        LanguageConfig pythonConfig = new LanguageConfig();
        pythonConfig.setLanguage("Python");
        pythonConfig.setSrcName("main.py");
        pythonConfig.setExeName("main.py");
        pythonConfig.setRunCommand("python3 main.py");
        pythonConfig.setCompileCommand(null);

        judge.setLanguage("Python");
        when(languageConfigLoader.getLanguageConfigByName("Python")).thenReturn(pythonConfig);

        JSONObject testCasesInfo = createTestCasesInfo(1);
        when(problemTestCaseHelper.loadTestCaseInfo(anyLong(), anyString(), anyString(), anyString(), any()))
                .thenReturn(testCasesInfo);

        List<JSONObject> testCaseResults = createTestCaseResults(JudgeStatus.STATUS_ACCEPTED.getStatus());
        when(judgeRun.judgeAllCase(anyLong(), any(Problem.class), anyString(), anyString(), any(JSONObject.class),
                isNull(), anyString(), anyBoolean(), anyString()))
                .thenReturn(testCaseResults);

        when(judgeCaseService.saveBatch(anyList())).thenReturn(true);

        try (MockedStatic<Compiler> compilerMock = mockStatic(Compiler.class);
             MockedStatic<SandboxRun> sandboxMock = mockStatic(SandboxRun.class)) {

            HashMap<String, Object> result = normalJudgeHelper.judge(problem, judge);

            assertEquals(JudgeStatus.STATUS_ACCEPTED.getStatus(), result.get("code"));

            compilerMock.verify(() -> Compiler.compile(any(), any(), any(), any()), never());

            sandboxMock.verify(() -> SandboxRun.delFile(any()), never());
        }
    }

    private JSONObject createTestCasesInfo(int caseCount) {
        JSONObject info = new JSONObject();
        info.set("judgeCaseMode", "default");

        JSONArray testCases = new JSONArray();
        for (int i = 1; i <= caseCount; i++) {
            JSONObject testCase = new JSONObject();
            testCase.set("inputName", i + ".in");
            testCase.set("outputName", i + ".out");
            testCase.set("caseId", (long) i);
            testCase.set("score", 100 / caseCount);
            testCase.set("outputSize", 100L);
            testCases.add(testCase);
        }
        info.set("testCases", testCases);

        return info;
    }

    private List<JSONObject> createTestCaseResults(Integer... statuses) {
        return java.util.stream.IntStream.range(0, statuses.length)
                .mapToObj(i -> {
                    JSONObject result = new JSONObject();
                    result.set("time", 50L + i * 10);
                    result.set("memory", 10 * 1024L + i * 100);
                    result.set("status", statuses[i]);
                    result.set("seq", i + 1);
                    result.set("score", 100 / statuses.length);
                    result.set("errMsg", "");
                    return result;
                })
                .toList();
    }
}

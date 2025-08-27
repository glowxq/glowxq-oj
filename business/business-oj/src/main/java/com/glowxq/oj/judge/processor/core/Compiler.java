package com.glowxq.oj.judge.processor.core;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.glowxq.core.common.exception.common.AlertsException;
import com.glowxq.core.util.SpringUtils;
import com.glowxq.oj.judge.enums.JudgeDir;
import com.glowxq.oj.judge.enums.JudgeStatus;
import com.glowxq.oj.judge.processor.bo.LanguageConfig;
import com.glowxq.oj.judge.processor.exception.CompileException;
import com.glowxq.oj.judge.processor.exception.SubmitException;
import com.glowxq.oj.judge.processor.exception.SystemException;
import com.glowxq.oj.judge.processor.helper.LanguageConfigLoader;
import com.glowxq.oj.judge.processor.utils.JudgeUtils;
import org.springframework.util.StringUtils;

import java.io.File;
import java.util.HashMap;
import java.util.List;

/**
 * @Author: Himit_ZH
 * @Date: 2021/4/16 12:14
 * @Description: 判题流程解耦重构2.0，该类只负责编译
 */
public class Compiler {

    public static String compile(LanguageConfig languageConfig, String code,
                                 String language, HashMap<String, String> extraFiles) throws SystemException, CompileException, SubmitException {

        if (languageConfig == null) {
            throw new AlertsException("Unsupported language " + language);
        }

        // 调用安全沙箱进行编译
        JSONArray result = SandboxRun.compile(languageConfig.getMaxCpuTime(),
                languageConfig.getMaxRealTime(),
                languageConfig.getMaxMemory(),
                256 * 1024 * 1024L,
                languageConfig.getSrcName(),
                languageConfig.getExeName(),
                parseCompileCommand(languageConfig.getCompileCommand()),
                languageConfig.getCompileEnvs(),
                code,
                extraFiles,
                true,
                false,
                null
        );
        JSONObject compileResult = (JSONObject) result.get(0);
        if (compileResult.getInt("status").intValue() != JudgeStatus.STATUS_ACCEPTED.getStatus()) {
            throw new CompileException("Compile Error.", ((JSONObject) compileResult.get("files")).getStr("stdout"),
                    ((JSONObject) compileResult.get("files")).getStr("stderr"));
        }

        String fileId = ((JSONObject) compileResult.get("fileIds")).getStr(languageConfig.getExeName());
        if (StringUtils.isEmpty(fileId)) {
            throw new SubmitException("Executable file not found.", ((JSONObject) compileResult.get("files")).getStr("stdout"),
                    ((JSONObject) compileResult.get("files")).getStr("stderr"));
        }
        return fileId;
    }

    public static Boolean compileSpj(String code, Long pid, String language, HashMap<String, String> extraFiles) throws SystemException {

        LanguageConfigLoader languageConfigLoader = SpringUtils.getBean(LanguageConfigLoader.class);
        LanguageConfig languageConfig = languageConfigLoader.getLanguageConfigByName("SPJ-" + language);

        if (languageConfig == null) {
            throw new RuntimeException("Unsupported SPJ language:" + language);
        }

        boolean copyOutExe = true;
        if (pid == null) { // 题目id为空，则不进行本地存储，可能为新建题目时测试特判程序是否正常的判断而已
            copyOutExe = false;
        }

        // 调用安全沙箱对特别判题程序进行编译
        JSONArray res = SandboxRun.compile(languageConfig.getMaxCpuTime(),
                languageConfig.getMaxRealTime(),
                languageConfig.getMaxMemory(),
                256 * 1024 * 1024L,
                languageConfig.getSrcName(),
                languageConfig.getExeName(),
                parseCompileCommand(languageConfig.getCompileCommand()),
                languageConfig.getCompileEnvs(),
                code,
                extraFiles,
                false,
                copyOutExe,
                JudgeDir.SPJ_WORKPLACE_DIR.getContent() + File.separator + pid
        );
        JSONObject compileResult = (JSONObject) res.get(0);
        if (compileResult.getInt("status").intValue() != JudgeStatus.STATUS_ACCEPTED.getStatus()) {
            throw new SystemException("Special Judge Code Compile Error.", ((JSONObject) compileResult.get("files")).getStr("stdout"),
                    ((JSONObject) compileResult.get("files")).getStr("stderr"));
        }
        return true;
    }

    public static Boolean compileInteractive(String code, Long pid, String language, HashMap<String, String> extraFiles) throws SystemException {

        LanguageConfigLoader languageConfigLoader = SpringUtils.getBean(LanguageConfigLoader.class);
        LanguageConfig languageConfig = languageConfigLoader.getLanguageConfigByName("INTERACTIVE-" + language);

        if (languageConfig == null) {
            throw new RuntimeException("Unsupported interactive language:" + language);
        }

        boolean copyOutExe = true;
        if (pid == null) { // 题目id为空，则不进行本地存储，可能为新建题目时测试特判程序是否正常的判断而已
            copyOutExe = false;
        }

        // 调用安全沙箱对特别判题程序进行编译
        JSONArray res = SandboxRun.compile(languageConfig.getMaxCpuTime(),
                languageConfig.getMaxRealTime(),
                languageConfig.getMaxMemory(),
                256 * 1024 * 1024L,
                languageConfig.getSrcName(),
                languageConfig.getExeName(),
                parseCompileCommand(languageConfig.getCompileCommand()),
                languageConfig.getCompileEnvs(),
                code,
                extraFiles,
                false,
                copyOutExe,
                JudgeDir.INTERACTIVE_WORKPLACE_DIR.getContent() + File.separator + pid
        );
        JSONObject compileResult = (JSONObject) res.get(0);
        if (compileResult.getInt("status").intValue() != JudgeStatus.STATUS_ACCEPTED.getStatus()) {
            throw new SystemException("Interactive Judge Code Compile Error.", ((JSONObject) compileResult.get("files")).getStr("stdout"),
                    ((JSONObject) compileResult.get("files")).getStr("stderr"));
        }
        return true;
    }

    private static List<String> parseCompileCommand(String command) {
        return JudgeUtils.translateCommandline(command);
    }
}
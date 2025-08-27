package com.glowxq.oj.judge.processor.helper;

import com.glowxq.core.common.exception.common.BusinessException;
import com.glowxq.oj.judge.enums.JudgeStatus;
import com.glowxq.oj.judge.processor.bo.LanguageConfig;
import com.glowxq.oj.judge.processor.bo.TestJudgeReq;
import com.glowxq.oj.judge.processor.bo.TestJudgeRes;
import com.glowxq.oj.judge.processor.core.Compiler;
import com.glowxq.oj.judge.processor.core.JudgeRun;
import com.glowxq.oj.judge.processor.core.SandboxRun;
import com.glowxq.oj.judge.processor.exception.CompileException;
import com.glowxq.oj.judge.processor.exception.SubmitException;
import com.glowxq.oj.judge.processor.exception.SystemException;
import com.glowxq.oj.judge.processor.utils.MessageUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * @author glowxq
 * @version 1.0
 * @date 2025/3/20
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TestJudgeHelper {

    private final JudgeRun judgeRun;

    private final LanguageConfigLoader languageConfigLoader;

    public TestJudgeRes testJudge(TestJudgeReq testJudgeReq) {
        // c和c++为一倍时间和空间，其它语言为2倍时间和空间
        LanguageConfig languageConfig = languageConfigLoader.getLanguageConfigByName(testJudgeReq.getLanguage());
        if (languageConfig == null) {
            throw new BusinessException("语言[%s]不支持".formatted(testJudgeReq.getLanguage()));
        }
        if (languageConfig.isNotC()) {
            testJudgeReq.setTimeLimit(testJudgeReq.getTimeLimit() * 2);
            testJudgeReq.setMemoryLimit(testJudgeReq.getMemoryLimit() * 2);
        }
        // 编译好的临时代码文件id
        String userFileId = null;
        try {
            // 有的语言可能不支持编译,目前有js、php不支持编译，需要提供源代码
            if (languageConfig.getCompileCommand() != null) {
                userFileId = Compiler.compile(languageConfig,
                        testJudgeReq.getCode(),
                        testJudgeReq.getLanguage(),
                        testJudgeReq.getExtraFile());
            }
            return judgeRun.testJudgeCase(userFileId, testJudgeReq);
        } catch (SystemException systemException) {
            log.error("[Test Judge] [System Error] [{}]", systemException.getMessage(), systemException);
            return TestJudgeRes.builder()
                               .memory(0L)
                               .time(0L)
                               .status(JudgeStatus.STATUS_COMPILE_ERROR.getStatus())
                               .stderr("Oops, something has gone wrong with the judgeServer. Please report this to administrator.")
                               .build();
        } catch (SubmitException submitException) {
            log.error("[Test Judge] [Submit Error] [{}]", submitException.getMessage(), submitException);
            return TestJudgeRes.builder()
                               .memory(0L)
                               .time(0L)
                               .status(JudgeStatus.STATUS_SUBMITTED_FAILED.getStatus())
                               .stderr(MessageUtils.mergeNonEmptyStrings(submitException.getMessage(), submitException.getStdout(), submitException.getStderr()))
                               .build();
        } catch (CompileException compileException) {
            log.error("[Test Judge] [Compile Error] [{}]", compileException.getMessage(), compileException);
            return TestJudgeRes.builder()
                               .memory(0L)
                               .time(0L)
                               .status(JudgeStatus.STATUS_COMPILE_ERROR.getStatus())
                               .stderr(MessageUtils.mergeNonEmptyStrings(compileException.getStdout(), compileException.getStderr()))
                               .build();
        } catch (Exception e) {
            log.error("[Test Judge] [Error] [{}]", e.getMessage(), e);
            return TestJudgeRes.builder()
                               .memory(0L)
                               .time(0L)
                               .status(JudgeStatus.STATUS_COMPILE_ERROR.getStatus())
                               .stderr("Oops, something has gone wrong with the judgeServer. Please report this to administrator.")
                               .build();
        } finally {
            // 删除tmpfs内存中的用户代码可执行文件
            if (StringUtils.isNotEmpty(userFileId)) {
                SandboxRun.delFile(userFileId);
            }
        }
    }
}

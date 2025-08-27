package com.glowxq.oj.submit.compile.impl;

import com.glowxq.oj.judge.processor.exception.SystemException;
import com.glowxq.oj.judge.processor.handler.JudgeHandlerImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;

/**
 * @author glowxq
 * @version 1.0
 * @date 2025/3/20
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CompileInteractiveSubmitImpl extends BaseCompileSubmitImpl {

    private final JudgeHandlerImpl judgeHandler;

    @Override
    protected void doSubmit(String code, Long problemId, String language, HashMap<String, String> extraFiles) throws SystemException {
        judgeHandler.compileInteractive(code, problemId, language, extraFiles);
    }
}

package com.glowxq.oj.submit.compile.impl;

import com.glowxq.core.common.exception.common.BusinessException;
import com.glowxq.core.util.JsonUtils;
import com.glowxq.oj.judge.processor.exception.SystemException;
import com.glowxq.oj.submit.compile.CompileSubmitInterface;
import com.glowxq.oj.submit.compile.bo.CompileSubmitDTO;
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
public abstract class BaseCompileSubmitImpl implements CompileSubmitInterface {

    @Override
    public void submit(CompileSubmitDTO judgeDto) {
        try {
            this.doSubmit(judgeDto.getCode(), judgeDto.getProblemId(), judgeDto.getLanguage(), judgeDto.getExtraFiles());
        } catch (Exception e) {
            log.error("compile submit error, judgeDto: {}", JsonUtils.toJsonString(judgeDto), e);
            throw new BusinessException("编译提交失败");
        }
    }

    protected abstract void doSubmit(String code, Long problemId, String language, HashMap<String, String> extraFiles) throws SystemException;
}

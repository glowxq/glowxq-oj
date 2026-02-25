package com.glowxq.oj.submit.compile;

import com.glowxq.core.util.SpringUtils;
import com.glowxq.oj.judge.enums.SubmitType;
import com.glowxq.oj.submit.compile.bo.CompileSubmitDTO;
import com.glowxq.oj.submit.compile.impl.CompileInteractiveSubmitImpl;
import com.glowxq.oj.submit.compile.impl.CompileSpjSubmitImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author glowxq
 * @version 1.0
 * @date 2025/3/20
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CompileSubmitFactory {

    public CompileSubmitInterface getCompileSubmitHandler(CompileSubmitDTO compileSubmitDTO) {
        SubmitType submitType = compileSubmitDTO.getSubmitType();
        switch (submitType) {
            case CompileSpj -> {
                return SpringUtils.getBean(CompileSpjSubmitImpl.class);
            }
            case CompileInteractive -> {
                return SpringUtils.getBean(CompileInteractiveSubmitImpl.class);
            }
            default -> throw new IllegalArgumentException("不支持的编译类型");
        }
    }
}

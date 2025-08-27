package com.glowxq.oj.problem.handler.create;

import com.glowxq.core.common.exception.common.AlertsException;
import com.glowxq.core.util.SpringUtils;
import com.glowxq.oj.problem.enums.ProblemType;
import org.springframework.stereotype.Component;

/**
 * @author glowxq
 * @version 1.0
 * @date 2025/3/18
 */
@Component
public class ProblemCreateFactory {

    public ProblemCreateInterface getProblemHandler(ProblemType problemType) {
        if (problemType.isFixed()) {
            return SpringUtils.getBean(FixedProblemCreateHandler.class);
        }
        if (problemType.isProgram()) {
            return SpringUtils.getBean(ProgramProblemCreateHandler.class);
        }
        throw new AlertsException("不支持的题目类型");
    }
}

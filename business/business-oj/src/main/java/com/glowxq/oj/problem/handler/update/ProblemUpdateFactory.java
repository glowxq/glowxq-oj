package com.glowxq.oj.problem.handler.update;

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
public class ProblemUpdateFactory {

    public ProblemUpdateInterface getProblemHandler(ProblemType problemType) {
        if (problemType.isFixed()) {
            return SpringUtils.getBean(FixedProblemUpdateHandler.class);
        }
        if (problemType.isProgram()) {
            return SpringUtils.getBean(ProgramProblemUpdateHandler.class);
        }
        throw new AlertsException("不支持的题目类型");
    }
}

package com.glowxq.oj.problem.handler.upload.oss;

import com.glowxq.core.util.SpringUtils;
import com.glowxq.oj.problem.enums.ProblemSourceType;
import org.springframework.stereotype.Component;

@Component
public class ProblemUploadFactory {

    public ProblemUploadInterface getProblemHandler(ProblemSourceType problemSourceType) {
        // 新增枚举值时编译器会强制要求处理新情况，此处不再显式处理 default 情况
        return switch (problemSourceType) {
            case GlowOJ -> SpringUtils.getBean(GojProblemUploadHandler.class);
            case Hoj -> SpringUtils.getBean(HojProblemUploadHandler.class);
            case Hydro -> SpringUtils.getBean(HydroProblemUploadHandler.class);
            case Other -> SpringUtils.getBean(OtherProblemUploadHandler.class);
        };
    }
}

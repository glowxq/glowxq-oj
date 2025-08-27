package com.glowxq.oj.problem.handler.upload.batch;

import com.glowxq.core.util.SpringUtils;
import com.glowxq.oj.problem.enums.ProblemSourceType;
import org.springframework.stereotype.Component;

/**
 * 题目批量上传工厂
 * 根据题目来源类型获取对应的批量上传处理器
 *
 * @author glowxq
 * @since 2025-03-17
 */
@Component
public class ProblemBatchUploadFactory {

    /**
     * 根据题目来源类型获取对应的批量上传处理器
     *
     * @param problemSourceType 题目来源类型
     * @return 对应的批量上传处理器
     */
    public ProblemBatchUploadInterface getProblemHandler(ProblemSourceType problemSourceType) {
        // 新增枚举值时编译器会强制要求处理新情况，此处不再显式处理 default 情况
        return switch (problemSourceType) {
            case GlowOJ -> SpringUtils.getBean(GojProblemBatchUploadHandler.class);
            case Hoj -> SpringUtils.getBean(HojProblemBatchUploadHandler.class);
            case Hydro -> SpringUtils.getBean(HydroProblemBatchUploadHandler.class);
            case Other -> SpringUtils.getBean(OtherProblemBatchUploadHandler.class);
        };
    }
}

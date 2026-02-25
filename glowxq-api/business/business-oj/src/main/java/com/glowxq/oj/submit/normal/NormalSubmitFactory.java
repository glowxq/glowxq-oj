package com.glowxq.oj.submit.normal;

import com.glowxq.core.common.exception.common.AlertsException;
import com.glowxq.core.util.SpringUtils;
import com.glowxq.oj.judge.enums.SubmitType;
import com.glowxq.oj.submit.normal.dto.GlobalNormalSubmitDTO;
import com.glowxq.oj.submit.normal.impl.FixedNormalSubmitImpl;
import com.glowxq.oj.submit.normal.impl.ProgramNormalSubmitImpl;
import com.glowxq.oj.submit.normal.impl.RemoteNormalSubmitImpl;
import org.springframework.stereotype.Component;

/**
 * @author glowxq
 * @version 1.0
 * @date 2025/3/18
 */
@Component
public class NormalSubmitFactory {

    public NormalSubmitInterface getSubmitHandler(GlobalNormalSubmitDTO judgeDto) {
        SubmitType submitType = judgeDto.getSubmitType();
        return switch (submitType) {
            case RemoteJudge -> SpringUtils.getBean(RemoteNormalSubmitImpl.class);
            case NormalJudge -> SpringUtils.getBean(ProgramNormalSubmitImpl.class);
            case FixedJudge -> SpringUtils.getBean(FixedNormalSubmitImpl.class);
            default -> throw new AlertsException("不支持的评测类型");
        };
    }
}

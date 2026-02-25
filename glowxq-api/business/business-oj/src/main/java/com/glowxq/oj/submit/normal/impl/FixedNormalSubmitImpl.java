package com.glowxq.oj.submit.normal.impl;

import com.glowxq.core.common.exception.common.BusinessException;
import com.glowxq.oj.judge.enums.JudgeSceneType;
import com.glowxq.oj.judge.enums.JudgeStatus;
import com.glowxq.oj.judge.pojo.po.Judge;
import com.glowxq.oj.problem.pojo.po.ProblemOption;
import com.glowxq.oj.submit.normal.dto.GlobalNormalSubmitDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author glowxq
 * @version 1.0
 * @date 2025/3/19
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class FixedNormalSubmitImpl extends BaseNormalSubmitImpl {

    private Judge judge;

    @Override
    public void checkJudgeDto(GlobalNormalSubmitDTO judgeDto) {
        super.checkJudgeDto(judgeDto);
        List<ProblemOption> replyOptions = judgeDto.getReplyOptions();
        if (CollectionUtils.isEmpty(replyOptions)) {
            throw new BusinessException("请选择、填写答案！");
        }

        // 根据提交场景进行校验
        JudgeSceneType judgeSceneType = judgeDto.getJudgeSceneType();
        if (judgeSceneType.isFixedTopic()) {
            // 作业提交 题目已经提交过
            Long businessId = judgeDto.getBusinessId();
            Judge judge = judgeService.getBySceneType(businessId, judgeDto.getProblemId(), judgeSceneType);
            if (judge != null) {
                throw new BusinessException("客观题在【%s】模式下只能提交一次".formatted(judgeSceneType.getName()));
            }
        }
    }

    @Override
    protected Judge preparationData(GlobalNormalSubmitDTO judgeDto) {
        this.judge = super.baseSaveSubmit(judgeDto);
        return judge;
    }

    @Override
    protected void submitExceptionHandler(Judge judge, Exception e) {
        super.submitExceptionHandler(judge, e);
        this.judge.setStatus(JudgeStatus.STATUS_SUBMITTED_FAILED.getStatus());
        judgeService.updateById(this.judge);
    }
}

package com.glowxq.oj.judge.processor.after;

import com.glowxq.core.common.exception.common.AlertsException;
import com.glowxq.oj.judge.enums.JudgeSceneType;
import com.glowxq.oj.judge.pojo.po.Judge;
import com.glowxq.oj.topic.enums.TopicType;
import com.glowxq.oj.topic.pojo.po.Topic;
import com.glowxq.oj.topic.pojo.po.TopicInfo;
import com.glowxq.oj.topic.pojo.po.TopicSubmit;
import com.glowxq.oj.topic.service.TopicInfoService;
import com.glowxq.oj.topic.service.TopicService;
import com.glowxq.oj.topic.service.TopicSubmitService;
import com.glowxq.oj.user.pojo.po.UserInfo;
import com.glowxq.oj.user.service.UserInfoService;
import com.glowxq.oj.user.service.UserProblemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * 基础判题场景处理器实现类。
 * 负责处理判题完成后的各种场景逻辑，包括用户题目记录保存、提交处理、AC处理、题目信息处理等。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JudgeSceneHandlerImpl implements JudgeSceneHandler {

    private final TopicService topicService;

    private final UserInfoService userInfoService;

    private final TopicInfoService topicInfoService;

    private final UserProblemService userProblemService;

    private final TopicSubmitService topicSubmitService;

    /**
     * 处理判题完成后的场景逻辑。
     * 包括保存用户题目记录、处理提交、处理AC状态、处理题目信息以及处理题目提交。
     *
     * @param judge          判题结果对象
     * @param judgeSceneType 判题场景类型
     */
    @Override
    public void judgeAfterScene(Judge judge, JudgeSceneType judgeSceneType) {
        UserInfo userInfo = userInfoService.getById(judge.getUserId());

        if (userInfo == null) {
            throw new AlertsException("用户不存在 请检查用户ID是否正确：%d".formatted(judge.getUserId()));
        }
        saveUserProblem(judge);
        doJudgeUserInfo(judge, userInfo);
        doTopicBusiness(judge, userInfo);
    }

    protected void doTopicBusiness(Judge judge, UserInfo userInfo) {
        if (judge.getBusinessId() == null) {
            return;
        }
        TopicType topicType = Optional.ofNullable(judge.sceneType()).map(JudgeSceneType::toTopicType).orElse(null);
        if (topicType == null || TopicType.Other.equals(topicType)) {
            return;
        }

        TopicSubmit topicSubmit = doTopicSubmit(judge);
        topicInfoService.autoJudge(judge, topicSubmit, userInfo);
    }

    /**
     * 保存用户题目记录。
     * 如果用户已经存在该题目的记录，则不再重复保存。
     *
     * @param judge 判题结果对象
     */
    protected void saveUserProblem(Judge judge) {
        userProblemService.saveUserProblem(judge);
    }

    private void doJudgeUserInfo(Judge judge, UserInfo userInfo) {
        userInfo.plusSubmitNum();
        if (judge.isAc()) {
            userInfo.acNumPlus();
        }
        userInfoService.updateById(userInfo);
    }

    /**
     * 处理题目提交逻辑。
     * 根据判题结果生成题目提交记录，并计算得分和罚时。
     *
     * @param judge 判题结果对象
     */
    private TopicSubmit doTopicSubmit(Judge judge) {
        Topic topic = topicService.getById(judge.getBusinessId());
        UserInfo userInfo = userInfoService.getById(judge.getUserId());
        TopicInfo topicInfo = topicInfoService.autoTopicId(judge.getBusinessId(), userInfo);
        TopicSubmit topicSubmit = new TopicSubmit();
        topicSubmit.setTopicId(judge.getBusinessId());
        topicSubmit.setProblemId(judge.getProblemId());
        topicSubmit.setProblemKey(judge.getProblemKey());
        topicSubmit.setProblemTitle(judge.getProblemTitle());
        topicSubmit.setProblemType(judge.getProblemType());
        topicSubmit.setTopicJudgeType(topic.getTopicJudgeType());
        topicSubmit.setUserId(judge.getUserId());
        topicSubmit.setName(topicInfo.getName());
        topicSubmit.setNickName(topicInfo.getNickName());
        topicSubmit.setJudgeStatus(judge.getStatus());
        topicSubmit.calculateScore(topic.getTimeLimit(), topic.timeRangeType(), topic.getStartTime(), topic.getDeductionRate(), judge.getScore());
        topicSubmit.setUseTime(judge.getUseTime());

        // 检查错误的题目数据进行罚时
        Integer punishmentTime = topicSubmitService.calculatePunishmentTime(judge, topicInfo.getPunishmentTime());
        topicSubmit.setPunishmentTime(punishmentTime);
        topicSubmitService.save(topicSubmit);
        return topicSubmit;
    }
}

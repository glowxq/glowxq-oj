package com.glowxq.oj.submit.normal.impl;

import cn.hutool.core.util.RandomUtil;
import com.glowxq.core.common.entity.BaseUserInfo;
import com.glowxq.core.common.entity.SocketMessage;
import com.glowxq.core.common.entity.TransferMessage;
import com.glowxq.core.common.enums.WebsocketBusinessType;
import com.glowxq.core.common.exception.common.BusinessException;
import com.glowxq.core.common.feishu.utils.FeishuMessageUtils;
import com.glowxq.core.util.HttpReqResUtil;
import com.glowxq.core.util.JsonUtils;
import com.glowxq.oj.judge.enums.JudgeStatus;
import com.glowxq.oj.judge.pojo.po.Judge;
import com.glowxq.oj.judge.processor.JudgeProcessor;
import com.glowxq.oj.judge.processor.JudgeProcessorFactory;
import com.glowxq.oj.judge.service.JudgeService;
import com.glowxq.oj.problem.pojo.po.Problem;
import com.glowxq.oj.problem.service.ProblemService;
import com.glowxq.oj.submit.normal.NormalSubmitInterface;
import com.glowxq.oj.submit.normal.dto.GlobalNormalSubmitDTO;
import com.glowxq.oj.topic.pojo.po.Topic;
import com.glowxq.oj.topic.pojo.po.TopicInfo;
import com.glowxq.oj.topic.service.TopicInfoService;
import com.glowxq.oj.topic.service.TopicService;
import com.glowxq.oj.user.pojo.po.UserInfo;
import com.glowxq.oj.user.service.UserInfoService;
import com.glowxq.security.core.util.LoginUtils;
import com.glowxq.websocket.stc.ServiceToClientMessageHandler;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * @author glowxq
 * @version 1.0
 * @date 2025/3/19
 * <p>
 * 判题提交的抽象基类，定义了判题提交的标准流程。
 * 采用模板方法模式，子类需实现具体提交逻辑。
 */
@Slf4j
public abstract class BaseNormalSubmitImpl implements NormalSubmitInterface {

    @Autowired
    protected JudgeService judgeService;

    @Autowired
    protected TopicService topicService;

    @Autowired
    protected ProblemService problemService;

    @Autowired
    protected TopicInfoService topicInfoService;

    @Autowired
    protected UserInfoService userInfoService;

    @Autowired
    protected JudgeProcessorFactory judgeProcessorFactory;

    @Autowired
    protected ServiceToClientMessageHandler serviceToClientMessageHandler;

    /**
     * 执行判题提交的标准流程：
     * 1. 参数校验
     * 2. 预处理
     * 3. 基础判题处理
     * 4. 具体提交操作（由子类实现）
     *
     * @param judgeDto 判题提交数据传输对象
     */
    @Override
    // @Transactional(rollbackFor = Exception.class)
    public Judge submit(GlobalNormalSubmitDTO judgeDto) {
        String jsonString = JsonUtils.toJsonString(judgeDto);
        log.info("submit start judgeDto:{}", jsonString);
        this.checkJudgeDto(judgeDto);
        log.info("submit checkJudgeDto end");
        Judge judge = this.preparationData(judgeDto);
        log.info("submit preparationData end");

        // 异步执行，提交到判题队列
        Thread.ofVirtual().start(() -> {
            try {
                this.doSubmit(judge);
                log.info("submit end judgeDto: {}", jsonString);
            } catch (Exception e) {
                this.submitExceptionHandler(judge, e);
            }
        });
        return judge;
    }

    /**
     * 参数校验模板方法
     * 子类可重写此方法实现具体的参数校验逻辑
     *
     * @param judgeDto 待校验的判题提交数据传输对象
     * @throws IllegalArgumentException 如果参数不符合要求
     */
    protected void checkJudgeDto(GlobalNormalSubmitDTO judgeDto) {
        Long problemId = judgeDto.getProblemId();
        Problem problem = problemService.getById(problemId);
        if (problem == null) {
            throw new BusinessException("题目不存在");
        }

        // 如果提交的是topic （比赛、作业、练习）
        if (judgeDto.getBusinessId() != null && judgeDto.getJudgeSceneType() != null && judgeDto.getJudgeSceneType().isFixedTopic()) {
            Topic topic = topicService.getById(judgeDto.getBusinessId());
            UserInfo userInfo = userInfoService.getById(LoginUtils.getUserId());
            TopicInfo topicInfo = topicInfoService.autoTopicId(judgeDto.getBusinessId(), userInfo);
            // 进行时间校验
            topic.checkTimeRange(topicInfo.getStartTime());
        }
    }

    /**
     * 预处理模板方法
     * 子类可重写此方法进行提交前的数据预处理
     *
     * @param judgeDto 待处理的判题提交数据传输对象
     */
    protected abstract Judge preparationData(GlobalNormalSubmitDTO judgeDto);

    /**
     * 基础判题处理模板方法
     * 子类应重写此方法实现核心判题逻辑
     *
     * @param judgeDto 判题提交数据传输对象
     * @return 初步的判题结果对象（通常需要进一步处理）
     */
    protected Judge baseSaveSubmit(GlobalNormalSubmitDTO judgeDto) {
        Problem problem = problemService.getById(judgeDto.getProblemId());
        BaseUserInfo userInfo = Objects.requireNonNull(LoginUtils.getLoginUser()).getUserInfo();
        Judge judge = new Judge();
        judge.setId(null);
        judge.setJudgeKey("%s-%s-%s".formatted(judgeDto.getJudgeSceneType(), problem.getProblemKey(), RandomUtil.randomStringUpper(6)));
        judge.setProblemId(problem.getId());
        judge.setProblemKey(problem.getProblemKey());
        judge.setProblemTitle(problem.getTitle());
        judge.setUserId(userInfo.getId());
        judge.setGroupId(judgeDto.getBusinessId());
        judge.setContestId(judgeDto.getBusinessId());
        judge.setBusinessId(judgeDto.getBusinessId());

        judge.setName(userInfo.getUsername());
        judge.setSceneType(judgeDto.getJudgeSceneType().getCode());
        judge.setSubmitType(judgeDto.getSubmitType().getCode());
        judge.setProblemType(problem.getProblemType());
        judge.setSubmitTime(LocalDateTime.now());
        judge.setStartTime(LocalDateTime.now());
        judge.setEndTime(LocalDateTime.now());
        judge.setStatus(JudgeStatus.STATUS_PENDING.getStatus());
        judge.setLength(judgeDto.getCode().length());
        judge.setFlowImage(judgeDto.getFlowImage());
        judge.setCode(judgeDto.getCode());
        judge.setReplyOptions(JsonUtils.toJsonString(judgeDto.getReplyOptions()));
        judge.setLanguage(judgeDto.getLanguage());
        HttpServletRequest request = ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getRequest();
        judge.setSubmitIp(HttpReqResUtil.getIpAddress(request));
        judge.setShareEnable(problem.getCodeShare());
        judge.setManualEvaluation(false);
        judgeService.save(judge);
        return judge;
    }

    /**
     * 提交判题
     */
    protected void doSubmit(Judge judge) {
        JudgeProcessor judgeProcessor = judgeProcessorFactory.getJudgeProcessor(judge);
        judgeProcessor.judge(judge);
    }

    /**
     * 提交异常处理
     *
     * @param judge
     * @param e
     */
    protected void submitExceptionHandler(Judge judge, Exception e) {
        Long judgeId = judge.getId();
        log.error("judgeId:{} doSubmit error", judgeId, e);
        FeishuMessageUtils.sendInternalMessage("judgeId:[%s] doSubmit error:%s\n stack:%s".formatted(judgeId, e.getMessage(), ExceptionUtils.getStackTrace(e)));

        judge.setStatus(JudgeStatus.STATUS_SYSTEM_ERROR.getStatus());
        judgeService.updateById(judge);

        TransferMessage<Judge> judgeTransferMessage = new TransferMessage<>();
        judgeTransferMessage.setMessage(new SocketMessage<>(judge, WebsocketBusinessType.JudgeNotify));
        judgeTransferMessage.toUsers(judge.getUserId().toString());
        judgeTransferMessage.setFromUser("System");
        judgeTransferMessage.setToPushAll(false);
        serviceToClientMessageHandler.handleTransferMessage(judgeTransferMessage);
    }
}
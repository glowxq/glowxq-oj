package com.glowxq.oj.judge.processor;

import com.glowxq.core.common.entity.SocketMessage;
import com.glowxq.core.common.entity.TransferMessage;
import com.glowxq.core.common.enums.WebsocketBusinessType;
import com.glowxq.core.common.exception.common.BusinessException;
import com.glowxq.oj.judge.enums.JudgeSceneType;
import com.glowxq.oj.judge.pojo.po.Judge;
import com.glowxq.oj.judge.processor.after.JudgeSceneHandler;
import com.glowxq.websocket.stc.ServiceToClientMessageHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @author glowxq
 * @version 1.0
 * @date 2025/4/1
 */
@Component
@Slf4j
public abstract class BaseJudgeProcessor implements JudgeProcessor {

    @Autowired
    protected ServiceToClientMessageHandler serviceToClientMessageHandler;

    @Autowired
    protected JudgeSceneHandler judgeSceneHandler;

    @Override
    public void judge(Judge judge) {
        log.info("judgeId:{} judge start", judge.getId());
        Long judgeId = judge.getId();
        log.info("judgeId:{} checkJudgeData", judgeId);
        this.checkJudgeData(judge);

        log.info("judgeId:{} doJudge", judgeId);
        Judge doneJudge = this.doJudge(judge);

        log.info("judgeId:{} websocketNotify", judgeId);
        this.websocketNotify(doneJudge);

        log.info("judgeId:{} judgeAfter", judgeId);
        this.judgeAfter(doneJudge);
        log.info("judgeId:{} judgeAfter done", judgeId);
    }

    protected void judgeAfter(Judge doneJudge) {
        JudgeSceneType sceneType = doneJudge.sceneType();
        if (JudgeSceneType.Normal.equals(sceneType)) {
            return;
        }
        judgeSceneHandler.judgeAfterScene(doneJudge, sceneType);
    }

    protected void websocketNotify(Judge judge) {
        TransferMessage<Judge> transferMessage = new TransferMessage<>();
        transferMessage.setMessage(new SocketMessage<>(judge, WebsocketBusinessType.JudgeNotify));
        transferMessage.toUsers(judge.getUserId().toString());
        transferMessage.setFromUser("System");
        transferMessage.setToPushAll(false);
        serviceToClientMessageHandler.handleTransferMessage(transferMessage);
    }

    protected void checkJudgeData(Judge judge) {
        if (judge.getId() == null || judge.getUserId() == null) {
            throw new BusinessException("判题核心参数丢失");
        }
    }

    protected abstract Judge doJudge(Judge judge);
}

package com.glowxq.oj.topic.mapper;

import com.glowxq.oj.topic.pojo.po.TopicSubmit;
import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.query.QueryWrapper;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

/**
 * <p>
 * 主题测评记录 Mapper 接口
 * </p>
 *
 * @author glowxq
 * @since 2025-04-12
 */
public interface TopicSubmitMapper extends BaseMapper<TopicSubmit> {

    default List<TopicSubmit> listByTopicIdProblemIdJudgeStatus(Long topicId, Long problemId, List<Integer> judgeStatusList) {
        if (CollectionUtils.isEmpty(judgeStatusList)) {
            return List.of();
        }
        QueryWrapper queryWrapper = QueryWrapper.create();
        queryWrapper.eq(TopicSubmit::getTopicId, topicId);
        queryWrapper.eq(TopicSubmit::getProblemId, problemId);
        queryWrapper.in(TopicSubmit::getJudgeStatus, judgeStatusList);
        return this.selectListByQuery(queryWrapper);
    }

    List<TopicSubmit> listByTopicIdMaxUserScore(Long topicId);
}
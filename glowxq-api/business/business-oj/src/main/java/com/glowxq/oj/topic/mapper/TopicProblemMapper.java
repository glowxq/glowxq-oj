package com.glowxq.oj.topic.mapper;

import com.glowxq.oj.topic.pojo.po.TopicProblem;
import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.query.QueryWrapper;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 主题题目 Mapper 接口
 * </p>
 *
 * @author glowxq
 * @since 2025-04-12
 */
public interface TopicProblemMapper extends BaseMapper<TopicProblem> {

    default void bindTopicProblem(Long topicId, List<Long> problemIds) {
        if (CollectionUtils.isEmpty(problemIds) || topicId == null) {
            return;
        }
        List<TopicProblem> topicProblems = problemIds.stream().map(problemId -> {
            TopicProblem topicProblem = new TopicProblem();
            topicProblem.setTopicId(topicId);
            topicProblem.setProblemId(problemId);
            topicProblem.setRequired(true);
            return topicProblem;
        }).collect(Collectors.toList());
        this.insertBatch(topicProblems);
    }

    default void unBindAllByTopicId(Long topicId) {
        if (topicId == null) {
            return;
        }
        QueryWrapper queryWrapper = QueryWrapper.create();
        queryWrapper.from(TopicProblem.class);
        queryWrapper.eq(TopicProblem::getTopicId, topicId);
        this.deleteByQuery(queryWrapper);
    }

    default List<TopicProblem> listByTopicId(Long topicId){
        QueryWrapper queryWrapper = QueryWrapper.create();
        queryWrapper.eq(TopicProblem::getTopicId, topicId);
        return this.selectListByQuery(queryWrapper);
    }
}
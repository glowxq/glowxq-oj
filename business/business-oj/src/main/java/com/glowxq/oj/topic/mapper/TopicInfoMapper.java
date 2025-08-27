package com.glowxq.oj.topic.mapper;

import com.glowxq.oj.topic.pojo.dto.TopicRankDTO;
import com.glowxq.oj.topic.pojo.po.TopicInfo;
import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.query.QueryWrapper;

import java.util.List;

/**
 * <p>
 * 主题数据 Mapper 接口
 * </p>
 *
 * @author glowxq
 * @since 2025-04-12
 */
public interface TopicInfoMapper extends BaseMapper<TopicInfo> {

    default TopicInfo getByTopicId(Long topicId, Long userId) {
        QueryWrapper queryWrapper = QueryWrapper.create();
        queryWrapper.eq(TopicInfo::getTopicId, topicId);
        queryWrapper.eq(TopicInfo::getUserId, userId);
        return selectOneByQuery(queryWrapper);
    }

    default List<TopicInfo> topicRank(TopicRankDTO dto) {
        QueryWrapper queryWrapper = QueryWrapper.create();
        queryWrapper.eq(TopicInfo::getTopicId, dto.getTopicId());
        queryWrapper.orderBy(TopicInfo::getScore, false);
        switch (dto.topicJudgeType()) {
            case ACM:
                queryWrapper.orderBy(TopicInfo::getAcNum).desc();
                queryWrapper.orderBy(TopicInfo::getPunishmentTime).asc();
                queryWrapper.orderBy(TopicInfo::getUseTime).asc();
                break;
            case OI:
                queryWrapper.orderBy(TopicInfo::getScore).desc();
                queryWrapper.orderBy(TopicInfo::getAcNum).desc();
                queryWrapper.orderBy(TopicInfo::getPunishmentTime).asc();
                queryWrapper.orderBy(TopicInfo::getUseTime).asc();
                break;
        }
        return selectListByQuery(queryWrapper);
    }
}
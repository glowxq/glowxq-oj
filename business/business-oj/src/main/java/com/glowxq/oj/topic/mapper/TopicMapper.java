package com.glowxq.oj.topic.mapper;

import com.glowxq.oj.topic.pojo.po.Topic;
import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.query.QueryWrapper;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

/**
 * <p>
 * 主题 Mapper 接口
 * </p>
 *
 * @author glowxq
 * @since 2025-04-12
 */
public interface TopicMapper extends BaseMapper<Topic> {

    default List<Topic> listByIds(List<Long> topicIds) {
        if (CollectionUtils.isEmpty(topicIds)) {
            return List.of();
        }
        return selectListByIds(topicIds);
    }

    default List<Topic> listByHome(Integer num) {
        QueryWrapper qw = QueryWrapper.create();
        qw.eq(Topic::getHomeShow, Boolean.TRUE);
        qw.limit(num);
        return selectListByQuery(qw);
    }
}
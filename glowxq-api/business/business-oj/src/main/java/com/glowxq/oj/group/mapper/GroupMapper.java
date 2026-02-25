package com.glowxq.oj.group.mapper;

import com.glowxq.oj.group.pojo.po.Group;
import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.query.QueryWrapper;

/**
 * <p>
 * 班级表 Mapper 接口
 * </p>
 *
 * @author glowxq
 * @since 2025-03-27
 */
public interface GroupMapper extends BaseMapper<Group> {

    default Group getByCode(String groupCode) {
        QueryWrapper qw = QueryWrapper.create();
        qw.from(Group.class);
        qw.eq(Group::getCode, groupCode);
        return selectOneByQuery(qw);
    }
}
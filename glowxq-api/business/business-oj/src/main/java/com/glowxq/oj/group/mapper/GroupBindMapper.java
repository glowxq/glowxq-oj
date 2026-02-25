package com.glowxq.oj.group.mapper;

import com.glowxq.oj.common.DataScopeUtils;
import com.glowxq.oj.group.pojo.po.GroupBind;
import com.glowxq.system.meta.base.TagBindEnum;
import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.query.QueryWrapper;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

/**
 * <p>
 * 班级绑定数据 Mapper 接口
 * </p>
 *
 * @author glowxq
 * @since 2025-03-28
 */
public interface GroupBindMapper extends BaseMapper<GroupBind> {

    default void bindGroups(Long businessId, List<Long> groupIdList, TagBindEnum bindType) {
        if (CollectionUtils.isEmpty(groupIdList)) {
            return;
        }
        List<Long> groupIdListDistinct = groupIdList.stream().distinct().toList();
        List<GroupBind> groupBindList = groupIdListDistinct.stream().map(groupId -> {
            GroupBind groupBind = new GroupBind();
            groupBind.setGroupId(groupId);
            groupBind.setBusinessId(businessId);
            groupBind.setType(bindType.getCode());
            return groupBind;
        }).toList();
        this.insertBatch(groupBindList);
    }

    default List<GroupBind> listByBusinessId(Long businessId, TagBindEnum tagBind) {
        QueryWrapper qw = QueryWrapper.create();
        qw.from(GroupBind.class);
        qw.eq(GroupBind::getBusinessId, businessId);
        qw.eq(GroupBind::getType, tagBind.getCode());
        return this.selectListByQuery(qw);
    }

    /**
     * 根据用户权限 过滤好groupIds 获取到的必然是用户权限内的班级绑定数据，如果是管理员，并且未筛选则远路返回
     * @param groupIds
     * @param tagBind
     * @return
     */
    default List<GroupBind> listByGroupIds(List<Long> groupIds, TagBindEnum tagBind) {
        groupIds = DataScopeUtils.scope(groupIds);
        if (CollectionUtils.isEmpty(groupIds)) {
            return List.of();
        }
        QueryWrapper qw = QueryWrapper.create();
        qw.from(GroupBind.class);
        qw.in(GroupBind::getGroupId, groupIds);
        qw.eq(GroupBind::getType, tagBind.getCode());
        return this.selectListByQuery(qw);
    }

    default List<GroupBind> listByBusinessIds(List<Long> businessIds, TagBindEnum tagBind) {
        if (CollectionUtils.isEmpty(businessIds) || tagBind == null) {
            return List.of();
        }
        QueryWrapper qw = QueryWrapper.create();
        qw.from(GroupBind.class);
        qw.in(GroupBind::getBusinessId, businessIds);
        qw.eq(GroupBind::getType, tagBind.getCode());
        return this.selectListByQuery(qw);
    }

    default void deleteByBusinessId(Long businessId, TagBindEnum bindType) {
        QueryWrapper qw = QueryWrapper.create();
        qw.from(GroupBind.class);
        qw.eq(GroupBind::getBusinessId, businessId);
        qw.eq(GroupBind::getType, bindType.getCode());
        this.deleteByQuery(qw);
    }

    default void deleteByBusinessIds(List<Long> businessIds, TagBindEnum tagBind) {
        QueryWrapper qw = QueryWrapper.create();
        qw.from(GroupBind.class);
        qw.in(GroupBind::getBusinessId, businessIds);
        qw.eq(GroupBind::getType, tagBind.getCode());
        this.deleteByQuery(qw);
    }

    default void bindsGroups(List<Long> businessIds, List<Long> groupIdList, TagBindEnum bindType) {
        if (CollectionUtils.isEmpty(businessIds) || CollectionUtils.isEmpty(groupIdList) || bindType == null) {
            return;
        }

        for (Long businessId : businessIds) {
            bindGroups(businessId, groupIdList, bindType);
        }
    }

    default List<GroupBind> listByGroupId(Long groupId, TagBindEnum tagBind) {
        QueryWrapper queryWrapper = QueryWrapper.create();
        queryWrapper.from(GroupBind.class);
        queryWrapper.eq(GroupBind::getGroupId, groupId);
        queryWrapper.eq(GroupBind::getType, tagBind.getCode());
        return this.selectListByQuery(queryWrapper);
    }

    default void unBindAll(Long businessId, TagBindEnum tagBind) {
        QueryWrapper queryWrapper = QueryWrapper.create();
        queryWrapper.from(GroupBind.class);
        queryWrapper.eq(GroupBind::getBusinessId, businessId);
        queryWrapper.eq(GroupBind::getType, tagBind.getCode());
        this.deleteByQuery(queryWrapper);
    }
}
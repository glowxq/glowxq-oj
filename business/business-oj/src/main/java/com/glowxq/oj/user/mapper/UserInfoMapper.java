package com.glowxq.oj.user.mapper;

import com.glowxq.oj.user.pojo.po.UserInfo;
import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.query.QueryWrapper;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

/**
 * <p>
 * 用户信息 Mapper 接口
 * </p>
 *
 * @author glowxq
 * @since 2025-06-23
 */
public interface UserInfoMapper extends BaseMapper<UserInfo> {

    default List<UserInfo> acRankingList(List<Long> userIds) {
        if (CollectionUtils.isEmpty(userIds)) {
            return List.of();
        }
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.from(UserInfo.class);
        queryWrapper.in(UserInfo::getUserId, userIds);
        queryWrapper.orderBy(UserInfo::getAcNum).desc();
        return this.selectListByQuery(queryWrapper);
    }

    default List<UserInfo> acRankingList() {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.from(UserInfo.class);
        queryWrapper.orderBy(UserInfo::getAcNum).desc();
        queryWrapper.limit(10);
        return this.selectListByQuery(queryWrapper);
    }

    default UserInfo getByPhone(String phone) {
        QueryWrapper queryWrapper = QueryWrapper.create();
        queryWrapper.from(UserInfo.class);
        queryWrapper.eq(UserInfo::getPhone, phone);
        return this.selectOneByQuery(queryWrapper);
    }
}
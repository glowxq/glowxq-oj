package com.glowxq.oj.code.mapper;

import com.glowxq.core.util.StringUtils;
import com.glowxq.core.util.Utils;
import com.glowxq.oj.code.pojo.dto.SearchMonitorCodeDTO;
import com.glowxq.oj.code.pojo.po.CodeMonitor;
import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.query.QueryWrapper;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

/**
 * <p>
 * 代码监控 Mapper 接口
 * </p>
 *
 * @author glowxq
 * @since 2025-04-04
 */
public interface CodeMonitorMapper extends BaseMapper<CodeMonitor> {

    default CodeMonitor getByUserId(Long userId, String codeMode) {
        QueryWrapper queryWrapper = QueryWrapper.create();
        queryWrapper.eq(CodeMonitor::getMonitorUserId, userId);
        queryWrapper.eq(CodeMonitor::getCodeMode, codeMode);
        return selectOneByQuery(queryWrapper);
    }

    default List<CodeMonitor> listByUserIds(List<Long> userIds) {
        if (CollectionUtils.isEmpty(userIds)) {
            return List.of();
        }
        QueryWrapper queryWrapper = QueryWrapper.create();
        queryWrapper.in(CodeMonitor::getMonitorUserId, userIds);
        return selectListByQuery(queryWrapper);
    }

    default List<CodeMonitor> searchMonitorCodeList(SearchMonitorCodeDTO dto) {
        QueryWrapper queryWrapper = QueryWrapper.create();
        queryWrapper.eq(CodeMonitor::getMonitorUserId, dto.getMonitorUserId(), Utils.isNotNull(dto.getMonitorUserId()));
        if (StringUtils.isNotBlank(dto.getSearchKey())) {
            queryWrapper.or(qw -> {qw.like(CodeMonitor::getMonitorName, dto.getSearchKey());});
            queryWrapper.or(qw -> {qw.like(CodeMonitor::getMonitorPhone, dto.getSearchKey());});
        }
        queryWrapper.limit(10);
        queryWrapper.orderBy(CodeMonitor::getVersion).desc();
        return selectListByQuery(queryWrapper);
    }
}
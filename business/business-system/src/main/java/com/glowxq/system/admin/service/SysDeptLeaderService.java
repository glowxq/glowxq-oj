package com.glowxq.system.admin.service;

import com.glowxq.system.admin.pojo.po.SysDeptLeader;
import com.mybatisflex.core.service.IService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 * 部门领导人表 Service
 * </p>
 *
 * @author glowxq
 * @since 2024-03-26
 */
public interface SysDeptLeaderService extends IService<SysDeptLeader> {

    @Transactional
    void syncLeader(Long deptId, List<Long> leaderIds);
}
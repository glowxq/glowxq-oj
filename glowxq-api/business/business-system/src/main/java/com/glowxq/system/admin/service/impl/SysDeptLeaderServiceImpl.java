package com.glowxq.system.admin.service.impl;

import com.glowxq.system.admin.mapper.SysDeptLeaderMapper;
import com.glowxq.system.admin.pojo.po.SysDeptLeader;
import com.glowxq.system.admin.service.SysDeptLeaderService;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 部门领导人表 服务实现类
 * </p>
 *
 * @author glowxq
 * @since 2024-03-26
 */
@Service
@RequiredArgsConstructor
public class SysDeptLeaderServiceImpl extends ServiceImpl<SysDeptLeaderMapper, SysDeptLeader> implements SysDeptLeaderService {

    @Override
    public void syncLeader(Long deptId, List<Long> leaderIds) {
        QueryWrapper wrapper = QueryWrapper.create().eq(SysDeptLeader::getDeptId, deptId);
        remove(wrapper); // 清除旧的信息
        List<SysDeptLeader> deptLeaders = new ArrayList<>();
        for (Long leaderId : leaderIds) {
            SysDeptLeader deptLeader = new SysDeptLeader();
            deptLeader.setDeptId(deptId);
            deptLeader.setLeaderId(leaderId);
            deptLeaders.add(deptLeader);
        }
        saveBatch(deptLeaders); // 批量生成新的信息
    }

}
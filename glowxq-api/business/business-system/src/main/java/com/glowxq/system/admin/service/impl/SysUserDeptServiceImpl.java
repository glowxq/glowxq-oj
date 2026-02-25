package com.glowxq.system.admin.service.impl;

import com.glowxq.system.admin.mapper.SysUserDeptMapper;
import com.glowxq.system.admin.pojo.dto.sysuser.UserDeptDTO;
import com.glowxq.system.admin.pojo.po.SysUserDept;
import com.glowxq.system.admin.service.SysUserDeptService;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.glowxq.system.admin.pojo.po.table.SysUserDeptTableDef.SYS_USER_DEPT;

/**
 * <p>
 * 用户-部门关系表 服务实现类
 * </p>
 *
 * @author glowxq
 * @since 2024-04-02
 */

@Service
@RequiredArgsConstructor
public class SysUserDeptServiceImpl extends ServiceImpl<SysUserDeptMapper, SysUserDept> implements SysUserDeptService {

    @Override
    @Transactional
    public void bind(UserDeptDTO dto) {
        List<Long> userIds = dto.getUserIds();
        List<Long> deptIds = dto.getDeptIds();
        if (!userIds.isEmpty()) {
            List<Long> removeIds = QueryChain.of(SysUserDept.class).select(SYS_USER_DEPT.ID).where(SYS_USER_DEPT.USER_ID.in(userIds)).listAs(Long.class);
            removeByIds(removeIds);
        }

        List<SysUserDept> batchList = new ArrayList<>();
        SysUserDept userDept;
        for (Long deptId : deptIds) {
            for (Long userId : userIds) {
                userDept = new SysUserDept();
                userDept.setUserId(userId);
                userDept.setDeptId(deptId);
                batchList.add(userDept);
            }
        }
        saveBatch(batchList); // 重新创建user 和dept的关系
    }

    @Override
    public void unbind(List<Long> userIds) {
        QueryWrapper removeWrapper = QueryWrapper.create().where(SYS_USER_DEPT.USER_ID.in(userIds));
        remove(removeWrapper);
    }

}
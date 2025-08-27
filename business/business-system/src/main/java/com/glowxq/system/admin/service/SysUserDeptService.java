package com.glowxq.system.admin.service;

import com.glowxq.system.admin.pojo.dto.sysuser.UserDeptDTO;
import com.glowxq.system.admin.pojo.po.SysUserDept;
import com.mybatisflex.core.service.IService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 * 用户-部门关系表 Service
 * </p>
 *
 * @author glowxq
 * @since 2024-04-02
 */
public interface SysUserDeptService extends IService<SysUserDept> {

    @Transactional
    void bind(UserDeptDTO dto);

    void unbind(List<Long> userIds);
}
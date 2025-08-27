package com.glowxq.system.admin.service;

import com.glowxq.system.admin.pojo.po.SysDeptClosure;
import com.mybatisflex.core.service.IService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 * 部门祖籍关系表 Service
 * </p>
 *
 * @author glowxq
 * @since 2024-03-28
 */
public interface SysDeptClosureService extends IService<SysDeptClosure> {

    List<SysDeptClosure> ancestorsPath(Long deptId);

    void create(Long deptId, Long parentDeptId);

    @Transactional
    void remove(Long nodeId);

    List<Long> descendants(List<Long> ancestorIds);

    void move(Long nodeId, Long newNodeId);
}
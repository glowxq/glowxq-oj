package com.glowxq.system.admin.service;

import com.glowxq.core.common.entity.PageResult;
import com.glowxq.core.common.entity.SelectIdsDTO;
import com.glowxq.system.admin.pojo.dto.sysdept.SysDeptCreateDTO;
import com.glowxq.system.admin.pojo.dto.sysdept.SysDeptListDTO;
import com.glowxq.system.admin.pojo.dto.sysdept.SysDeptUpdateDTO;
import com.glowxq.system.admin.pojo.po.SysDept;
import com.glowxq.system.admin.pojo.vo.sysdept.DeptTreeVO;
import com.glowxq.system.admin.pojo.vo.sysdept.SysDeptLeaderVO;
import com.glowxq.system.admin.pojo.vo.sysdept.SysDeptVO;
import com.mybatisflex.core.service.IService;

import java.util.List;

/**
 * <p>
 * 部门表 Service
 * </p>
 *
 * @author glowxq
 * @since 2024-03-20
 */
public interface SysDeptService extends IService<SysDept> {

    void create(SysDeptCreateDTO dto);

    void update(SysDeptUpdateDTO dto);

    PageResult<SysDeptVO> page(SysDeptListDTO dto);

    List<SysDeptVO> list(SysDeptListDTO dto);

    void remove(SelectIdsDTO dto);

    SysDeptVO detail(Object id);

    List<DeptTreeVO> getDepartmentTreeWithAdditionalNodes();

    List<DeptTreeVO> getDeptTree(Integer excludeNodeId, Boolean appendRoot, Boolean needSetTotal);

    SysDeptLeaderVO findSysUserDeptLeader();

    List<SysDept> searchDeptList(String searchKey, Long regionId);
}
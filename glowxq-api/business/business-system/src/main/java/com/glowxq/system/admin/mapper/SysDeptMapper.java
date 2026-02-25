package com.glowxq.system.admin.mapper;

import com.glowxq.system.admin.pojo.po.SysDept;
import com.glowxq.system.admin.pojo.vo.sysdept.TotalDeptVO;
import com.mybatisflex.core.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 部门表 Mapper 接口
 * </p>
 *
 * @author glowxq
 * @since 2024-03-20
 */
public interface SysDeptMapper extends BaseMapper<SysDept> {

    /**
     * （向上递归）查询指定层级的祖籍id
     *
     * @param deptId
     *            部门id
     * @return 祖籍id集合
     */
    List<Integer> iterUpDeptAncestors(@Param("deptId") Integer deptId);

    /**
     * 统计分配部门的用户数量
     *
     * @return 部门用户数量
     */
    List<TotalDeptVO> countUsersPerDept();

}
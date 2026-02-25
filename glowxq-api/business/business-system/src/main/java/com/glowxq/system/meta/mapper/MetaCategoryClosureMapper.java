package com.glowxq.system.meta.mapper;

import com.glowxq.system.meta.pojo.po.MetaCategoryClosure;
import com.mybatisflex.core.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
* <p>
* 分类祖籍关系表 Mapper 接口
* </p>
*
* @author glowxq
* @since 2025-04-24
*/
public interface MetaCategoryClosureMapper extends BaseMapper<MetaCategoryClosure> {

    /**
     * 分离子树
     *
     * @param nodeId
     *            节点ID
     * @return 分离的节点数
     */
    @Delete(" DELETE " + " FROM " + " meta_category_closure t " + " WHERE" + " EXISTS (" + " SELECT" + " 1 " + " FROM" + " meta_category_closure d " + " WHERE"
            + " d.ancestor_id = #{nodeId} and t.descendant_id = d.descendant_id " + " ) " + " and "
            + " EXISTS( select 1 from meta_category_closure a where a.descendant_id = #{nodeId} and a.ancestor_id != a.descendant_id and t.ancestor_id = a.ancestor_id )")
    Integer detach(@Param("nodeId") Integer nodeId);

    /**
     * 查询指定节点的子树
     *
     * @param nodeId
     *            节点ID
     * @return 子树
     */
    @Select(" SELECT ancestor_id,descendant_id,depth FROM meta_category_closure " + " WHERE EXISTS (" + "    SELECT 1 " + "    FROM meta_category_closure d "
            + "    WHERE d.ancestor_id = #{nodeId} " + "    AND meta_category_closure.descendant_id = d.descendant_id" + ")" + " AND EXISTS (" + "    SELECT 1 "
            + "    FROM meta_category_closure a " + "    WHERE a.descendant_id = #{nodeId} " + "    AND a.ancestor_id != a.descendant_id "
            + "    AND meta_category_closure.ancestor_id = a.ancestor_id " + " );")
    List<MetaCategoryClosure> selectDetachTree(@Param("nodeId") Long nodeId);

    /**
     * 嫁接子树
     *
     * @param nodeId
     *            节点ID
     * @param newNodeId
     *            新节点ID
     * @return 嫁接的节点数
     */
    @Insert("INSERT INTO meta_category_closure ( ancestor_id, descendant_id, depth ) SELECT" + " super.ancestor_id," + " sub.descendant_id,("
            + " super.depth + sub.depth + 1 " + " ) " + " FROM" + " meta_category_closure super" + " CROSS JOIN meta_category_closure sub " + " WHERE"
            + " super.descendant_id = #{newNodeId} " + " AND sub.ancestor_id = #{nodeId}")
    Integer graft(@Param("nodeId") Long nodeId, @Param("newNodeId") Long newNodeId);
}
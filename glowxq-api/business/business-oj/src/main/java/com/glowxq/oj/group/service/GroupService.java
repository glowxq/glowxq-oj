package com.glowxq.oj.group.service;

import com.glowxq.core.common.entity.ImportExcelDTO;
import com.glowxq.core.common.entity.PageResult;
import com.glowxq.core.common.entity.SelectIdsDTO;
import com.glowxq.oj.group.pojo.dto.GroupCreateDTO;
import com.glowxq.oj.group.pojo.dto.GroupListDTO;
import com.glowxq.oj.group.pojo.dto.GroupUpdateDTO;
import com.glowxq.oj.group.pojo.po.Group;
import com.glowxq.oj.group.pojo.vo.GroupVO;
import com.glowxq.system.meta.base.TagBindEnum;
import com.glowxq.system.meta.enums.SystemTagBind;
import com.mybatisflex.core.service.IService;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 班级表 Service
 * </p>
 *
 * @author glowxq
 * @since 2025-03-27
 */
public interface GroupService extends IService<Group> {

    void create(GroupCreateDTO dto);

    void update(GroupUpdateDTO dto);

    PageResult<GroupVO> page(GroupListDTO dto);

    List<GroupVO> list(GroupListDTO dto);

    void remove(SelectIdsDTO dto);

    GroupVO detail(Object id);

    void importExcel(ImportExcelDTO dto);

    void exportExcel(GroupListDTO dto, HttpServletResponse response);

    void bindGroups(Long businessId, List<Long> groupIdList, TagBindEnum bindType);

    void bindsGroups(List<Long> businessIds, List<Long> groupIdList, TagBindEnum bindType);

    void unBindAll(Long businessId, TagBindEnum tagBusinessBindType);

    void unBindsAll(List<Long> businessIds, TagBindEnum tagBusinessBindType);

    List<Group> listByBusinessId(Long businessId, TagBindEnum tagBusinessBindType);

    /**
     * 根据业务ID列表和绑定类型查询绑定的群组信息
     *
     * @param businessIds      业务ID列表（不可为null，但允许空列表）
     * @param tagBusinessBindType 业务绑定类型（用于区分不同的绑定场景）
     * @return 业务ID到对应群组列表的映射（Key：业务ID，Value：该业务绑定的群组列表）
     * 当输入为空或查询无结果时返回空Map
     */
    Map<Long, List<Group>> mapByBusinessIds(List<Long> businessIds, TagBindEnum tagBusinessBindType);

    List<Long> listBusinessIdByGroupIds(List<Long> groupIds, TagBindEnum tagBusinessBindType);

    List<Long> listBusinessIdByGroupId(Long groupId, TagBindEnum tagBusinessBindType);

    void bindByCode(Long userId, String groupCode, TagBindEnum tagBindEnum);
}
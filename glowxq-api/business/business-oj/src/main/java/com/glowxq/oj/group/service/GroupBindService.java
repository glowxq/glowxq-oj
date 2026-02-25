package com.glowxq.oj.group.service;

import com.glowxq.core.common.entity.ImportExcelDTO;
import com.glowxq.core.common.entity.PageResult;
import com.glowxq.core.common.entity.SelectIdsDTO;
import com.glowxq.oj.group.pojo.dto.GroupBindCreateDTO;
import com.glowxq.oj.group.pojo.dto.GroupBindListDTO;
import com.glowxq.oj.group.pojo.dto.GroupBindUpdateDTO;
import com.glowxq.oj.group.pojo.po.GroupBind;
import com.glowxq.oj.group.pojo.vo.GroupBindVO;
import com.mybatisflex.core.service.IService;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

/**
 * <p>
 * 班级绑定数据 Service
 * </p>
 *
 * @author glowxq
 * @since 2025-03-28
 */
public interface GroupBindService extends IService<GroupBind> {

    void create(GroupBindCreateDTO dto);

    void update(GroupBindUpdateDTO dto);

    PageResult<GroupBindVO> page(GroupBindListDTO dto);

    List<GroupBindVO> list(GroupBindListDTO dto);

    void remove(SelectIdsDTO dto);

    GroupBindVO detail(Object id);

    void importExcel(ImportExcelDTO dto);

    void exportExcel(GroupBindListDTO dto, HttpServletResponse response);


}
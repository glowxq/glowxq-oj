package com.glowxq.system.meta.service;

import com.glowxq.core.common.entity.ImportExcelDTO;
import com.glowxq.core.common.entity.PageResult;
import com.glowxq.core.common.entity.SelectIdsDTO;
import com.glowxq.system.meta.pojo.dto.MetaTagBindCreateDTO;
import com.glowxq.system.meta.pojo.dto.MetaTagBindListDTO;
import com.glowxq.system.meta.pojo.dto.MetaTagBindUpdateDTO;
import com.glowxq.system.meta.pojo.po.MetaTagBind;
import com.glowxq.system.meta.pojo.vo.MetaTagBindVO;
import com.mybatisflex.core.service.IService;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

/**
 * <p>
 * 绑定标签权限 Service
 * </p>
 *
 * @author glowxq
 * @since 2025-04-23
 */
public interface MetaTagBindService extends IService<MetaTagBind> {

    void create(MetaTagBindCreateDTO dto);

    void update(MetaTagBindUpdateDTO dto);

    PageResult<MetaTagBindVO> page(MetaTagBindListDTO dto);

    List<MetaTagBindVO> list(MetaTagBindListDTO dto);

    void remove(SelectIdsDTO dto);

    MetaTagBindVO detail(Object id);

    void importExcel(ImportExcelDTO dto);

    void exportExcel(MetaTagBindListDTO dto, HttpServletResponse response);

}
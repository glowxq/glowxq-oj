package com.glowxq.system.meta.service;

import com.glowxq.core.common.entity.ImportExcelDTO;
import com.glowxq.core.common.entity.PageResult;
import com.glowxq.core.common.entity.SelectIdsDTO;
import com.glowxq.system.meta.pojo.dto.MetaMenuCreateDTO;
import com.glowxq.system.meta.pojo.dto.MetaMenuListDTO;
import com.glowxq.system.meta.pojo.dto.MetaMenuUpdateDTO;
import com.glowxq.system.meta.pojo.po.MetaMenu;
import com.glowxq.system.meta.pojo.vo.MetaMenuVO;
import com.mybatisflex.core.service.IService;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

/**
 * <p>
 * 菜单 Service
 * </p>
 *
 * @author glowxq
 * @since 2025-04-23
 */
public interface MetaMenuService extends IService<MetaMenu> {

    void create(MetaMenuCreateDTO dto);

    void update(MetaMenuUpdateDTO dto);

    PageResult<MetaMenuVO> page(MetaMenuListDTO dto);

    List<MetaMenuVO> list(MetaMenuListDTO dto);

    void remove(SelectIdsDTO dto);

    MetaMenuVO detail(Object id);

    void importExcel(ImportExcelDTO dto);

    void exportExcel(MetaMenuListDTO dto, HttpServletResponse response);
}
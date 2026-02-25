package com.glowxq.system.meta.service;

import com.glowxq.core.common.entity.ImportExcelDTO;
import com.glowxq.core.common.entity.PageResult;
import com.glowxq.core.common.entity.SelectIdsDTO;
import com.glowxq.system.meta.pojo.dto.MetaCategoryCreateDTO;
import com.glowxq.system.meta.pojo.dto.MetaCategoryListDTO;
import com.glowxq.system.meta.pojo.dto.MetaCategoryUpdateDTO;
import com.glowxq.system.meta.pojo.po.MetaCategory;
import com.glowxq.system.meta.pojo.vo.MetaCategoryTreeVO;
import com.glowxq.system.meta.pojo.vo.MetaCategoryVO;
import com.mybatisflex.core.service.IService;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

/**
 * <p>
 * 分类 Service
 * </p>
 *
 * @author glowxq
 * @since 2025-04-23
 */
public interface MetaCategoryService extends IService<MetaCategory> {

    void create(MetaCategoryCreateDTO dto);

    void update(MetaCategoryUpdateDTO dto);

    PageResult<MetaCategoryVO> page(MetaCategoryListDTO dto);

    List<MetaCategoryVO> list(MetaCategoryListDTO dto);

    void remove(SelectIdsDTO dto);

    MetaCategoryVO detail(Object id);

    void importExcel(ImportExcelDTO dto);

    void exportExcel(MetaCategoryListDTO dto, HttpServletResponse response);

    List<MetaCategoryTreeVO> getTree(Integer excludeNodeId, Boolean appendRoot, Boolean needSetTotal);


}
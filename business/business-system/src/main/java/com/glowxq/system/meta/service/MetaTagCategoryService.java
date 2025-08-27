package com.glowxq.system.meta.service;

import com.glowxq.core.common.entity.ImportExcelDTO;
import com.glowxq.core.common.entity.PageResult;
import com.glowxq.core.common.entity.SelectIdsDTO;
import com.glowxq.system.meta.pojo.dto.MetaTagCategoryCreateDTO;
import com.glowxq.system.meta.pojo.dto.MetaTagCategoryListDTO;
import com.glowxq.system.meta.pojo.dto.MetaTagCategoryUpdateDTO;
import com.glowxq.system.meta.pojo.po.MetaTagCategory;
import com.glowxq.system.meta.pojo.vo.MetaTagCategoryVO;
import com.mybatisflex.core.service.IService;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

/**
 * <p>
 * 标签分类 Service
 * </p>
 *
 * @author glowxq
 * @since 2025-06-10
 */
public interface MetaTagCategoryService extends IService<MetaTagCategory> {

    void create(MetaTagCategoryCreateDTO dto);

    void update(MetaTagCategoryUpdateDTO dto);

    PageResult<MetaTagCategoryVO> page(MetaTagCategoryListDTO dto);

    List<MetaTagCategoryVO> list(MetaTagCategoryListDTO dto);

    void remove(SelectIdsDTO dto);

    MetaTagCategoryVO detail(Long id);

    void importExcel(ImportExcelDTO dto);

    void exportExcel(MetaTagCategoryListDTO dto, HttpServletResponse response);
}
package com.glowxq.system.meta.service;

import com.glowxq.core.common.entity.ImportExcelDTO;
import com.glowxq.core.common.entity.PageResult;
import com.glowxq.core.common.entity.SelectIdsDTO;
import com.glowxq.system.meta.pojo.dto.MetaTextCreateDTO;
import com.glowxq.system.meta.pojo.dto.MetaTextListDTO;
import com.glowxq.system.meta.pojo.dto.MetaTextUpdateDTO;
import com.glowxq.system.meta.pojo.po.MetaText;
import com.glowxq.system.meta.pojo.vo.MetaTextVO;
import com.mybatisflex.core.service.IService;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

/**
 * <p>
 * 文本 Service
 * </p>
 *
 * @author glowxq
 * @since 2025-04-23
 */
public interface MetaTextService extends IService<MetaText> {

    void create(MetaTextCreateDTO dto);

    void update(MetaTextUpdateDTO dto);

    PageResult<MetaTextVO> page(MetaTextListDTO dto);

    List<MetaTextVO> list(MetaTextListDTO dto);

    void remove(SelectIdsDTO dto);

    MetaTextVO detail(Object id);

    void importExcel(ImportExcelDTO dto);

    void exportExcel(MetaTextListDTO dto, HttpServletResponse response);

    MetaText getByKey(String key);
}
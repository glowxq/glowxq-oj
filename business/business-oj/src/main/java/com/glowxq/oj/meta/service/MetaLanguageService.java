package com.glowxq.oj.meta.service;

import com.glowxq.core.common.entity.ImportExcelDTO;
import com.glowxq.core.common.entity.PageResult;
import com.glowxq.core.common.entity.SelectIdsDTO;
import com.glowxq.oj.meta.pojo.dto.MetaLanguageCreateDTO;
import com.glowxq.oj.meta.pojo.dto.MetaLanguageListDTO;
import com.glowxq.oj.meta.pojo.dto.MetaLanguageUpdateDTO;
import com.glowxq.oj.meta.pojo.po.MetaLanguage;
import com.glowxq.oj.meta.pojo.vo.MetaLanguageVO;
import com.glowxq.oj.problem.enums.ProblemSourceType;
import com.mybatisflex.core.service.IService;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 语言 Service
 * </p>
 *
 * @author glowxq
 * @since 2025-03-17
 */
public interface MetaLanguageService extends IService<MetaLanguage> {

    void create(MetaLanguageCreateDTO dto);

    void update(MetaLanguageUpdateDTO dto);

    PageResult<MetaLanguageVO> page(MetaLanguageListDTO dto);

    List<MetaLanguageVO> list(MetaLanguageListDTO dto);

    Map<String, Long> mapFromNameToId(ProblemSourceType problemSourceType);

    void remove(SelectIdsDTO dto);

    MetaLanguageVO detail(Object id);

    void importExcel(ImportExcelDTO dto);

    void exportExcel(MetaLanguageListDTO dto, HttpServletResponse response);
}
package com.glowxq.oj.problem.service;

import com.glowxq.core.common.entity.ImportExcelDTO;
import com.glowxq.core.common.entity.PageResult;
import com.glowxq.core.common.entity.SelectIdsDTO;
import com.glowxq.oj.problem.pojo.dto.ProblemLanguageCreateDTO;
import com.glowxq.oj.problem.pojo.dto.ProblemLanguageListDTO;
import com.glowxq.oj.problem.pojo.dto.ProblemLanguageUpdateDTO;
import com.glowxq.oj.problem.pojo.po.ProblemLanguage;
import com.glowxq.oj.problem.pojo.vo.ProblemLanguageVO;
import com.mybatisflex.core.service.IService;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

/**
 * <p>
 * 题目语言 Service
 * </p>
 *
 * @author glowxq
 * @since 2025-03-17
 */
public interface ProblemLanguageService extends IService<ProblemLanguage> {

    void create(ProblemLanguageCreateDTO dto);

    void update(ProblemLanguageUpdateDTO dto);

    PageResult<ProblemLanguageVO> page(ProblemLanguageListDTO dto);

    List<ProblemLanguageVO> list(ProblemLanguageListDTO dto);

    void remove(SelectIdsDTO dto);

    ProblemLanguageVO detail(Object id);

    void importExcel(ImportExcelDTO dto);

    void exportExcel(ProblemLanguageListDTO dto, HttpServletResponse response);

    void bindLanguageIds(Long problemId, List<Long> languageIds);

    void deleteByProblemId(Long problemId);
}
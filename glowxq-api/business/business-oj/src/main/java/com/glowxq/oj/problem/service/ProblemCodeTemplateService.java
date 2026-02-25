package com.glowxq.oj.problem.service;

import com.glowxq.core.common.entity.ImportExcelDTO;
import com.glowxq.core.common.entity.PageResult;
import com.glowxq.core.common.entity.SelectIdsDTO;
import com.glowxq.oj.problem.pojo.dto.ProblemCodeTemplateCreateDTO;
import com.glowxq.oj.problem.pojo.dto.ProblemCodeTemplateListDTO;
import com.glowxq.oj.problem.pojo.dto.ProblemCodeTemplateUpdateDTO;
import com.glowxq.oj.problem.pojo.po.ProblemCodeTemplate;
import com.glowxq.oj.problem.pojo.vo.ProblemCodeTemplateVO;
import com.mybatisflex.core.service.IService;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

/**
 * <p>
 * 题目代码模版 Service
 * </p>
 *
 * @author glowxq
 * @since 2025-03-18
 */
public interface ProblemCodeTemplateService extends IService<ProblemCodeTemplate> {

    void create(ProblemCodeTemplateCreateDTO dto);

    void update(ProblemCodeTemplateUpdateDTO dto);

    PageResult<ProblemCodeTemplateVO> page(ProblemCodeTemplateListDTO dto);

    List<ProblemCodeTemplateVO> list(ProblemCodeTemplateListDTO dto);

    void remove(SelectIdsDTO dto);

    ProblemCodeTemplateVO detail(Object id);

    void importExcel(ImportExcelDTO dto);

    void exportExcel(ProblemCodeTemplateListDTO dto, HttpServletResponse response);

    void bindCodeTemplateIds(Long id, List<ProblemCodeTemplateCreateDTO> codeTemplates);

    void deleteByProblemId(Long problemId);

    List<ProblemCodeTemplate> listByProblemId(Long problemId);
}
package com.glowxq.oj.problem.service;

import com.glowxq.core.common.entity.ImportExcelDTO;
import com.glowxq.core.common.entity.PageResult;
import com.glowxq.core.common.entity.SelectIdsDTO;
import com.glowxq.oj.problem.pojo.dto.ProblemOptionBO;
import com.glowxq.oj.problem.pojo.dto.ProblemOptionListDTO;
import com.glowxq.oj.problem.pojo.dto.ProblemOptionUpdateDTO;
import com.glowxq.oj.problem.pojo.po.ProblemOption;
import com.glowxq.oj.problem.pojo.vo.ProblemOptionVO;
import com.mybatisflex.core.service.IService;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

/**
 * <p>
 * 题目选项 Service
 * </p>
 *
 * @author glowxq
 * @since 2025-03-17
 */
public interface ProblemOptionService extends IService<ProblemOption> {

    void create(ProblemOptionBO dto);

    void update(ProblemOptionUpdateDTO dto);

    PageResult<ProblemOptionVO> page(ProblemOptionListDTO dto);

    List<ProblemOptionVO> list(ProblemOptionListDTO dto);

    void remove(SelectIdsDTO dto);

    ProblemOptionVO detail(Object id);

    void importExcel(ImportExcelDTO dto);

    void exportExcel(ProblemOptionListDTO dto, HttpServletResponse response);

    List<ProblemOption> listProblemId(Long id);

    void deleteByProblemId(Long problemId);
}
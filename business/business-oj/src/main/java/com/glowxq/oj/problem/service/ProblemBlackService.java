package com.glowxq.oj.problem.service;

import com.glowxq.core.common.entity.ImportExcelDTO;
import com.glowxq.core.common.entity.PageResult;
import com.glowxq.core.common.entity.SelectIdsDTO;
import com.glowxq.oj.problem.pojo.dto.ProblemBlackCreateDTO;
import com.glowxq.oj.problem.pojo.dto.ProblemBlackListDTO;
import com.glowxq.oj.problem.pojo.dto.ProblemBlackUpdateDTO;
import com.glowxq.oj.problem.pojo.po.ProblemBlack;
import com.glowxq.oj.problem.pojo.vo.ProblemBlackVO;
import com.mybatisflex.core.service.IService;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

/**
 * <p>
 * 题目黑名单 Service
 * </p>
 *
 * @author glowxq
 * @since 2025-04-11
 */
public interface ProblemBlackService extends IService<ProblemBlack> {

    void create(ProblemBlackCreateDTO dto);

    void update(ProblemBlackUpdateDTO dto);

    PageResult<ProblemBlackVO> page(ProblemBlackListDTO dto);

    List<ProblemBlackVO> list(ProblemBlackListDTO dto);

    void remove(SelectIdsDTO dto);

    ProblemBlackVO detail(Object id);

    void importExcel(ImportExcelDTO dto);

    void exportExcel(ProblemBlackListDTO dto, HttpServletResponse response);
}
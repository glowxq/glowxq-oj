package com.glowxq.oj.judge.service;

import com.glowxq.core.common.entity.ImportExcelDTO;
import com.glowxq.core.common.entity.PageResult;
import com.glowxq.core.common.entity.SelectIdsDTO;
import com.glowxq.oj.judge.pojo.dto.JudgeCaseCreateDTO;
import com.glowxq.oj.judge.pojo.dto.JudgeCaseListDTO;
import com.glowxq.oj.judge.pojo.dto.JudgeCaseUpdateDTO;
import com.glowxq.oj.judge.pojo.po.JudgeCase;
import com.glowxq.oj.judge.pojo.vo.JudgeCaseVO;
import com.mybatisflex.core.service.IService;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

/**
 * <p>
 * 测评用例情况 Service
 * </p>
 *
 * @author glowxq
 * @since 2025-03-17
 */
public interface JudgeCaseService extends IService<JudgeCase> {

    void create(JudgeCaseCreateDTO dto);

    void update(JudgeCaseUpdateDTO dto);

    PageResult<JudgeCaseVO> page(JudgeCaseListDTO dto);

    List<JudgeCaseVO> list(JudgeCaseListDTO dto);

    void remove(SelectIdsDTO dto);

    JudgeCaseVO detail(Object id);

    void importExcel(ImportExcelDTO dto);

    void exportExcel(JudgeCaseListDTO dto, HttpServletResponse response);

    List<JudgeCase> listByJudgeId(Long id);
}
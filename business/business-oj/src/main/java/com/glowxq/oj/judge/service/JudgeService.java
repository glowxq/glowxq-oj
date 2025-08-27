package com.glowxq.oj.judge.service;

import com.glowxq.core.common.entity.ImportExcelDTO;
import com.glowxq.core.common.entity.PageResult;
import com.glowxq.core.common.entity.SelectIdsDTO;
import com.glowxq.oj.judge.enums.JudgeSceneType;
import com.glowxq.oj.judge.pojo.dto.JudgeCreateDTO;
import com.glowxq.oj.judge.pojo.dto.JudgeListDTO;
import com.glowxq.oj.judge.pojo.dto.ManualEvaluationDTO;
import com.glowxq.oj.judge.pojo.po.Judge;
import com.glowxq.oj.judge.pojo.vo.JudgeVO;
import com.mybatisflex.core.service.IService;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

/**
 * <p>
 * 测评记录 Service
 * </p>
 *
 * @author glowxq
 * @since 2025-03-17
 */
public interface JudgeService extends IService<Judge> {

    void create(JudgeCreateDTO dto);

    void update(ManualEvaluationDTO dto);

    PageResult<JudgeVO> page(JudgeListDTO dto);

    List<JudgeVO> list(JudgeListDTO dto);

    void remove(SelectIdsDTO dto);

    JudgeVO detail(Long id);

    void importExcel(ImportExcelDTO dto);

    void exportExcel(JudgeListDTO dto, HttpServletResponse response);

    /**
     * 更新测评记录为编译中
     *
     * @param judge
     */
    void updateCompiling(Judge judge);

    /**
     * 获取测评记录为测评中
     *
     * @param judge
     */
    void updateJudging(Judge judge);

    void manualEvaluation(ManualEvaluationDTO dto);

    Judge getBySceneType(Long businessId, Long problemId, JudgeSceneType judgeSceneType);

    Long countAc();
}
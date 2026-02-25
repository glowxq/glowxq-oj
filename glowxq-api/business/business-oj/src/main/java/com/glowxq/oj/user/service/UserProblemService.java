package com.glowxq.oj.user.service;

import com.glowxq.core.common.entity.ImportExcelDTO;
import com.glowxq.core.common.entity.PageResult;
import com.glowxq.core.common.entity.SelectIdsDTO;
import com.glowxq.oj.judge.enums.JudgeStatus;
import com.glowxq.oj.judge.pojo.po.Judge;
import com.glowxq.oj.user.pojo.dto.UserProblemCreateDTO;
import com.glowxq.oj.user.pojo.dto.UserProblemListDTO;
import com.glowxq.oj.user.pojo.dto.UserProblemUpdateDTO;
import com.glowxq.oj.user.pojo.po.UserProblem;
import com.glowxq.oj.user.pojo.vo.UserProblemVO;
import com.mybatisflex.core.service.IService;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

/**
 * <p>
 * 用户题目数据 Service
 * </p>
 *
 * @author glowxq
 * @since 2025-04-03
 */
public interface UserProblemService extends IService<UserProblem> {

    void create(UserProblemCreateDTO dto);

    void update(UserProblemUpdateDTO dto);

    PageResult<UserProblemVO> page(UserProblemListDTO dto);

    List<UserProblemVO> list(UserProblemListDTO dto);

    void remove(SelectIdsDTO dto);

    UserProblemVO detail(Object id);

    void importExcel(ImportExcelDTO dto);

    void exportExcel(UserProblemListDTO dto, HttpServletResponse response);

    UserProblem getByUserAndProblem(Long userId, Long problemId, JudgeStatus status);

    void saveUserProblem(Judge judge);
}
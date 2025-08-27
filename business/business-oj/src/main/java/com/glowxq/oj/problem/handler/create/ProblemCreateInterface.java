package com.glowxq.oj.problem.handler.create;

import com.glowxq.oj.problem.pojo.dto.ProblemCreateUpdateDTO;

/**
 * @author glowxq
 * @version 1.0
 * @date 2025/3/18
 */
public interface ProblemCreateInterface {

    /**
     * 创建题目
     *
     * @param dto
     */
    void create(ProblemCreateUpdateDTO dto);
}

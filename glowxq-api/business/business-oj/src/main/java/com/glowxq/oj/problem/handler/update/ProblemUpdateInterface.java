package com.glowxq.oj.problem.handler.update;

import com.glowxq.oj.problem.pojo.dto.ProblemCreateUpdateDTO;

/**
 * @author glowxq
 * @version 1.0
 * @date 2025/3/18
 */
public interface ProblemUpdateInterface {

    /**
     * 创建题目
     *
     * @param dto
     */
    void update(ProblemCreateUpdateDTO dto);
}

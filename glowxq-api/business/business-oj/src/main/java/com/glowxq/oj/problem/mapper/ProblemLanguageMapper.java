package com.glowxq.oj.problem.mapper;

import com.glowxq.oj.problem.pojo.po.ProblemLanguage;
import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.query.QueryWrapper;

/**
 * <p>
 * 题目语言 Mapper 接口
 * </p>
 *
 * @author glowxq
 * @since 2025-03-17
 */
public interface ProblemLanguageMapper extends BaseMapper<ProblemLanguage> {

    default void deleteByProblemId(Long problemId) {
        QueryWrapper wrapper = QueryWrapper.create()
                                           .eq(ProblemLanguage::getProblemId, problemId);
        this.deleteByQuery(wrapper);
    }
}
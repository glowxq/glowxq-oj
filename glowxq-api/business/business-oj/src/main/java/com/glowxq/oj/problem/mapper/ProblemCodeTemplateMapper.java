package com.glowxq.oj.problem.mapper;

import com.glowxq.oj.problem.pojo.po.ProblemCodeTemplate;
import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.query.QueryWrapper;

import java.util.List;

/**
 * <p>
 * 题目代码模版 Mapper 接口
 * </p>
 *
 * @author glowxq
 * @since 2025-03-18
 */
public interface ProblemCodeTemplateMapper extends BaseMapper<ProblemCodeTemplate> {

    default void deleteByProblemId(Long problemId) {
        QueryWrapper qw = QueryWrapper.create();
        qw.from(ProblemCodeTemplate.class);
        qw.eq(ProblemCodeTemplate::getProblemId, problemId);
        this.deleteByQuery(qw);
    }

    default List<ProblemCodeTemplate> listByProblemId(Long problemId) {
        QueryWrapper qw = QueryWrapper.create();
        qw.from(ProblemCodeTemplate.class);
        qw.eq(ProblemCodeTemplate::getProblemId, problemId);
        return this.selectListByQuery(qw);
    }
}
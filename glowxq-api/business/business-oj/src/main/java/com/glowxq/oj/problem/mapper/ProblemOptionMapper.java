package com.glowxq.oj.problem.mapper;

import com.glowxq.oj.problem.pojo.po.ProblemOption;
import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.query.QueryWrapper;

import java.util.List;

/**
 * <p>
 * 题目选项 Mapper 接口
 * </p>
 *
 * @author glowxq
 * @since 2025-03-17
 */
public interface ProblemOptionMapper extends BaseMapper<ProblemOption> {

    default List<ProblemOption> listProblemId(Long problemId) {
        QueryWrapper qw = QueryWrapper.create().from(ProblemOption.class).eq(ProblemOption::getProblemId, problemId);
        return this.selectListByQuery(qw);
    }

    default void deleteByProblemId(Long problemId){
        QueryWrapper qw = QueryWrapper.create().from(ProblemOption.class).eq(ProblemOption::getProblemId, problemId);
        this.deleteByQuery(qw);
    }
}
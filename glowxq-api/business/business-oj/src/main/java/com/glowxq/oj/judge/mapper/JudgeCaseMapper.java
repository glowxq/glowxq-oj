package com.glowxq.oj.judge.mapper;

import com.glowxq.oj.judge.pojo.po.JudgeCase;
import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.query.QueryWrapper;

import java.util.List;

/**
 * <p>
 * 测评用例情况 Mapper 接口
 * </p>
 *
 * @author glowxq
 * @since 2025-03-17
 */
public interface JudgeCaseMapper extends BaseMapper<JudgeCase> {

    default List<JudgeCase> listByJudgeId(Long id) {
        QueryWrapper query = QueryWrapper.create();
        query.from(JudgeCase.class).eq(JudgeCase::getJudgeId, id);
        return selectListByQuery(query);
    }
}
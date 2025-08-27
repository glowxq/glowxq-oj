package com.glowxq.oj.meta.mapper;

import com.glowxq.oj.meta.pojo.po.MetaLanguage;
import com.glowxq.oj.problem.enums.ProblemSourceType;
import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.query.QueryWrapper;

import java.util.List;

/**
 * <p>
 * 语言 Mapper 接口
 * </p>
 *
 * @author glowxq
 * @since 2025-03-17
 */
public interface MetaLanguageMapper extends BaseMapper<MetaLanguage> {
    default List<MetaLanguage> listByOj(ProblemSourceType problemSourceType) {
        QueryWrapper queryWrapper = QueryWrapper.create().from(MetaLanguage.class)
                .eq(MetaLanguage::getOj, problemSourceType.getCode());
        return this.selectListByQuery(queryWrapper);
    }
}
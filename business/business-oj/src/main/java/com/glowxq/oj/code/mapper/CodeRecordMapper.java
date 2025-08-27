package com.glowxq.oj.code.mapper;

import com.glowxq.oj.code.pojo.po.CodeRecord;
import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.query.QueryWrapper;

import java.util.List;

/**
 * <p>
 * 用户代码 Mapper 接口
 * </p>
 *
 * @author glowxq
 * @since 2025-04-04
 */
public interface CodeRecordMapper extends BaseMapper<CodeRecord> {

    default List<CodeRecord> listByUserId(Long userId) {
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.from(CodeRecord.class);
        wrapper.eq(CodeRecord::getUserId, userId);
        return this.selectListByQuery(wrapper);
    }
}
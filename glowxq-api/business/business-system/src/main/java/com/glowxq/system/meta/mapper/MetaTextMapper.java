package com.glowxq.system.meta.mapper;

import com.glowxq.system.meta.pojo.po.MetaText;
import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.query.QueryWrapper;

/**
 * <p>
 * 文本 Mapper 接口
 * </p>
 *
 * @author glowxq
 * @since 2025-04-23
 */
public interface MetaTextMapper extends BaseMapper<MetaText> {

    default MetaText getByKey(String key) {
        QueryWrapper queryWrapper = QueryWrapper.create().eq(MetaText::getTextKey, key);
        return this.selectOneByQuery(queryWrapper);
    }
}
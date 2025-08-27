package com.glowxq.system.meta.mapper;

import com.glowxq.system.meta.pojo.po.MetaTag;
import com.glowxq.system.meta.pojo.vo.MetaTagCountVO;
import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.query.QueryWrapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 标签 Mapper 接口
 * </p>
 *
 * @author glowxq
 * @since 2025-04-23
 */
public interface MetaTagMapper extends BaseMapper<MetaTag> {

    default List<MetaTag> listByEnable(boolean enable) {
        QueryWrapper queryWrapper = QueryWrapper.create().from(MetaTag.class);
        queryWrapper.eq(MetaTag::getEnable, enable);
        return this.selectListByQuery(queryWrapper);
    }

    @Select("select category_id as categoryId, count(1) as `count` from meta_tag where enable = 1 group by category_id")
    List<MetaTagCountVO> categoryCount();
}
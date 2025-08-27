package com.glowxq.system.meta.mapper;

import com.glowxq.system.meta.pojo.po.MetaRegion;
import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.query.QueryWrapper;

import java.util.List;

/**
* <p>
* 区域地址 Mapper 接口
* </p>
*
* @author glowxq
* @since 2025-06-20
*/
public interface MetaRegionMapper extends BaseMapper<MetaRegion> {

    default List<MetaRegion> listByLevel(int level) {
        QueryWrapper qw = QueryWrapper.create();
        qw.le(MetaRegion::getLevel, level);
        return selectListByQuery(qw);
    }
}
package com.glowxq.system.meta.mapper;

import com.glowxq.system.meta.base.TagBindEnum;
import com.glowxq.system.meta.pojo.po.MetaTagBind;
import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.query.QueryWrapper;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

/**
 * <p>
 * 绑定标签权限 Mapper 接口
 * </p>
 *
 * @author glowxq
 * @since 2025-04-23
 */
public interface MetaTagBindMapper extends BaseMapper<MetaTagBind> {

    default void deleteByBusinessId(Long businessId, TagBindEnum bindType) {
        QueryWrapper queryWrapper = QueryWrapper.create().from(MetaTagBind.class)
                                                .eq(MetaTagBind::getBusinessId, businessId)
                                                .eq(MetaTagBind::getType, bindType.getCode());
        this.deleteByQuery(queryWrapper);
    }

    default List<MetaTagBind> listByBusinessId(Long businessId, TagBindEnum bindType) {
        QueryWrapper queryWrapper = QueryWrapper.create().from(MetaTagBind.class)
                                                .eq(MetaTagBind::getBusinessId, businessId)
                                                .eq(MetaTagBind::getType, bindType.getCode());
        return this.selectListByQuery(queryWrapper);
    }

    default List<MetaTagBind> listByTagIds(List<Long> tagIds, TagBindEnum tagBusinessBind) {
        if (CollectionUtils.isEmpty(tagIds) || tagBusinessBind == null) {
            return List.of();
        }
        QueryWrapper queryWrapper = QueryWrapper.create().from(MetaTagBind.class)
                                                .in(MetaTagBind::getTagId, tagIds)
                                                .eq(MetaTagBind::getType, tagBusinessBind.getCode());
        return this.selectListByQuery(queryWrapper);
    }

    default void deleteByBusinessIds(List<Long> businessIds, TagBindEnum tagBusinessBind) {
        if (CollectionUtils.isEmpty(businessIds) || tagBusinessBind == null) {
            return;
        }
        QueryWrapper qw = QueryWrapper.create();
        qw.from(MetaTagBind.class);
        qw.in(MetaTagBind::getBusinessId, businessIds);
        qw.eq(MetaTagBind::getType, tagBusinessBind.getCode());
        this.deleteByQuery(qw);
    }

    default List<MetaTagBind> listByBusinessIds(List<Long> businessIds, TagBindEnum tagBusinessBind) {
        if (CollectionUtils.isEmpty(businessIds) || tagBusinessBind == null) {
            return List.of();
        }
        QueryWrapper qw = QueryWrapper.create();
        qw.from(MetaTagBind.class);
        qw.in(MetaTagBind::getBusinessId, businessIds);
        qw.eq(MetaTagBind::getType, tagBusinessBind.getCode());
        return this.selectListByQuery(qw);
    }
}
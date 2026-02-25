package com.glowxq.system.meta.mapper;

import com.glowxq.system.meta.enums.ImageBusinessType;
import com.glowxq.system.meta.pojo.po.MetaImage;
import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.query.QueryWrapper;

import java.util.List;

/**
 * <p>
 * 图片 Mapper 接口
 * </p>
 *
 * @author glowxq
 * @since 2025-04-25
 */
public interface MetaImageMapper extends BaseMapper<MetaImage> {

    default MetaImage getByKey(String key) {
        QueryWrapper queryWrapper = QueryWrapper.create();
        queryWrapper.select()
                    .from(MetaImage.class)
                    .eq(MetaImage::getImageKey, key);
        return this.selectOneByQuery(queryWrapper);
    }

    default List<MetaImage> listByType(ImageBusinessType imageBusinessType) {
        QueryWrapper queryWrapper = QueryWrapper.create();
        queryWrapper.select()
                    .from(MetaImage.class)
                    .eq(MetaImage::getBusinessType, imageBusinessType.getCode());
        return this.selectListByQuery(queryWrapper);
    }
}
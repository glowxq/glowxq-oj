package com.glowxq.system.meta.service;

import com.glowxq.core.common.entity.ImportExcelDTO;
import com.glowxq.core.common.entity.PageResult;
import com.glowxq.core.common.entity.SelectIdsDTO;
import com.glowxq.system.meta.enums.ImageBusinessType;
import com.glowxq.system.meta.pojo.dto.MetaImageCreateDTO;
import com.glowxq.system.meta.pojo.dto.MetaImageListDTO;
import com.glowxq.system.meta.pojo.dto.MetaImageUpdateDTO;
import com.glowxq.system.meta.pojo.po.MetaImage;
import com.glowxq.system.meta.pojo.vo.MetaImageVO;
import com.mybatisflex.core.service.IService;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

/**
 * <p>
 * 图片 Service
 * </p>
 *
 * @author glowxq
 * @since 2025-04-25
 */
public interface MetaImageService extends IService<MetaImage> {

    void create(MetaImageCreateDTO dto);

    void update(MetaImageUpdateDTO dto);

    PageResult<MetaImageVO> page(MetaImageListDTO dto);

    List<MetaImageVO> list(MetaImageListDTO dto);

    void remove(SelectIdsDTO dto);

    MetaImageVO detail(Object id);

    void importExcel(ImportExcelDTO dto);

    void exportExcel(MetaImageListDTO dto, HttpServletResponse response);

    MetaImage getByKey(String key);

    List<MetaImage> listByType(ImageBusinessType imageBusinessType);
}
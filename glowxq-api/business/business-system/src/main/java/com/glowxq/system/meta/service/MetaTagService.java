package com.glowxq.system.meta.service;

import com.glowxq.core.common.entity.ImportExcelDTO;
import com.glowxq.core.common.entity.PageResult;
import com.glowxq.core.common.entity.SelectIdsDTO;
import com.glowxq.system.meta.base.TagBindEnum;
import com.glowxq.system.meta.pojo.dto.MetaTagBatchUpdateCategoryDTO;
import com.glowxq.system.meta.pojo.dto.MetaTagCreateDTO;
import com.glowxq.system.meta.pojo.dto.MetaTagListDTO;
import com.glowxq.system.meta.pojo.dto.MetaTagUpdateDTO;
import com.glowxq.system.meta.pojo.po.MetaTag;
import com.glowxq.system.meta.pojo.vo.MetaTagCountVO;
import com.glowxq.system.meta.pojo.vo.MetaTagVO;
import com.mybatisflex.core.service.IService;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 标签 Service
 * </p>
 *
 * @author glowxq
 * @since 2025-04-23
 */
public interface MetaTagService extends IService<MetaTag> {

    void create(MetaTagCreateDTO dto);

    void create(List<MetaTagCreateDTO> dtoList);

    void update(MetaTagUpdateDTO dto);

    PageResult<MetaTagVO> page(MetaTagListDTO dto);

    List<MetaTagVO> list(MetaTagListDTO dto);

    Map<String, Long> mapFromNameToId();

    void remove(SelectIdsDTO dto);

    MetaTagVO detail(Object id);

    void importExcel(ImportExcelDTO dto);

    void exportExcel(MetaTagListDTO dto, HttpServletResponse response);

    void bindTags(Long id, List<Long> tagIds, TagBindEnum tagBind);

    void deleteByBusinessId(Long businessId, TagBindEnum bindType);

    void unBindAll(Long businessId, TagBindEnum tagBind);

    List<MetaTag> listByBusinessId(Long businessId, TagBindEnum tagBind);

    List<MetaTag> listByBusinessIds(List<Long> businessIds, TagBindEnum tagBind);

    List<Long> listBusinessIdByTagIds(List<Long> tagIds, TagBindEnum tagBind);

    void unBindsAll(List<Long> ids, TagBindEnum tagBind);

    List<MetaTagCountVO> categoryCount();

    void batchUpdateCategory(MetaTagBatchUpdateCategoryDTO dto);
}
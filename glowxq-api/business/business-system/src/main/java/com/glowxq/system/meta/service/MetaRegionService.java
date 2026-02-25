package com.glowxq.system.meta.service;

import com.glowxq.core.common.entity.ImportExcelDTO;
import com.glowxq.core.common.entity.PageResult;
import com.glowxq.core.common.entity.SelectIdsDTO;
import com.glowxq.system.meta.pojo.dto.MetaRegionCreateDTO;
import com.glowxq.system.meta.pojo.dto.MetaRegionListDTO;
import com.glowxq.system.meta.pojo.dto.MetaRegionUpdateDTO;
import com.glowxq.system.meta.pojo.po.MetaRegion;
import com.glowxq.system.meta.pojo.vo.MetaRegionVO;
import com.mybatisflex.core.service.IService;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

/**
 * <p>
 * 区域地址 Service
 * </p>
 *
 * @author glowxq
 * @since 2025-06-20
 */
public interface MetaRegionService extends IService<MetaRegion> {

    void create(MetaRegionCreateDTO dto);

    void update(MetaRegionUpdateDTO dto);

    PageResult<MetaRegionVO> page(MetaRegionListDTO dto);

    List<MetaRegionVO> list(MetaRegionListDTO dto);

    void remove(SelectIdsDTO dto);

    MetaRegionVO detail(Long id);

    void importExcel(ImportExcelDTO dto);

    void exportExcel(MetaRegionListDTO dto, HttpServletResponse response);

    List<MetaRegion> listByLevel(int level);
}
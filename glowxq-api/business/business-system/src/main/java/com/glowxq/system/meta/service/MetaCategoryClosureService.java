package com.glowxq.system.meta.service;

import com.glowxq.core.common.entity.ImportExcelDTO;
import com.glowxq.core.common.entity.PageResult;
import com.glowxq.core.common.entity.SelectIdsDTO;
import com.glowxq.system.meta.pojo.dto.MetaCategoryClosureCreateDTO;
import com.glowxq.system.meta.pojo.dto.MetaCategoryClosureListDTO;
import com.glowxq.system.meta.pojo.dto.MetaCategoryClosureUpdateDTO;
import com.glowxq.system.meta.pojo.po.MetaCategoryClosure;
import com.glowxq.system.meta.pojo.vo.MetaCategoryClosureVO;
import com.mybatisflex.core.service.IService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 * 分类祖籍关系表 Service
 * </p>
 *
 * @author glowxq
 * @since 2025-04-24
 */
public interface MetaCategoryClosureService extends IService<MetaCategoryClosure> {

    void create(MetaCategoryClosureCreateDTO dto);

    void update(MetaCategoryClosureUpdateDTO dto);

    PageResult<MetaCategoryClosureVO> page(MetaCategoryClosureListDTO dto);

    List<MetaCategoryClosureVO> list(MetaCategoryClosureListDTO dto);

    void remove(SelectIdsDTO dto);

    MetaCategoryClosureVO detail(Object id);

    void importExcel(ImportExcelDTO dto);

    void exportExcel(MetaCategoryClosureListDTO dto, HttpServletResponse response);

    List<MetaCategoryClosure> ancestorsPath(Long deptId);

    void create(Long deptId, Long parentDeptId);

    @Transactional
    void remove(Long nodeId);

    List<Long> descendants(List<Long> ancestorIds);

    void move(Long nodeId, Long newNodeId);
}
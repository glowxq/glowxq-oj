package com.glowxq.system.meta.service.impl;

import com.glowxq.core.common.entity.ImportExcelDTO;
import com.glowxq.core.common.entity.PageResult;
import com.glowxq.core.common.entity.SelectIdsDTO;
import com.glowxq.core.common.enums.CommonResponseEnum;
import com.glowxq.core.util.BeanCopyUtils;
import com.glowxq.core.util.FileUtils;
import com.glowxq.core.util.PageUtils;
import com.glowxq.core.util.Utils;
import com.glowxq.excel.core.ExcelResult;
import com.glowxq.excel.utils.ExcelUtils;
import com.glowxq.system.meta.mapper.MetaRegionMapper;
import com.glowxq.system.meta.pojo.dto.MetaRegionCreateDTO;
import com.glowxq.system.meta.pojo.dto.MetaRegionImportDTO;
import com.glowxq.system.meta.pojo.dto.MetaRegionListDTO;
import com.glowxq.system.meta.pojo.dto.MetaRegionUpdateDTO;
import com.glowxq.system.meta.pojo.po.MetaRegion;
import com.glowxq.system.meta.pojo.vo.MetaRegionVO;
import com.glowxq.system.meta.service.MetaRegionService;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.io.OutputStream;
import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 区域地址 服务实现类
 * </p>
 *
 * @author glowxq
 * @since 2025-06-20
 */
@Service
@RequiredArgsConstructor
public class MetaRegionServiceImpl extends ServiceImpl<MetaRegionMapper, MetaRegion> implements MetaRegionService {
    @Override
    public void create(MetaRegionCreateDTO dto){
        MetaRegion metaRegion = BeanCopyUtils.copy(dto, MetaRegion.class);
        save(metaRegion);
    }

    @Override
    public void update(MetaRegionUpdateDTO dto){
        MetaRegion metaRegion = BeanCopyUtils.copy(dto, MetaRegion.class);
        QueryWrapper wrapper;
        // id有效性校验
        wrapper = QueryWrapper.create()
            .eq(MetaRegion::getId, dto.getId());
        CommonResponseEnum.INVALID_ID.assertTrue(count(wrapper) <= 0);

        saveOrUpdate(metaRegion);
    }

    @Override
    public PageResult<MetaRegionVO> page(MetaRegionListDTO dto){
        Page<MetaRegionVO> page = pageAs(PageUtils.getPage(dto), buildQueryWrapper(dto), MetaRegionVO.class);
        return PageUtils.getPageResult(page);
    }

    @Override
    public List<MetaRegionVO> list(MetaRegionListDTO dto){
        return listAs(buildQueryWrapper(dto), MetaRegionVO.class);
    }

    @Override
    public void remove(SelectIdsDTO dto){
        CommonResponseEnum.INVALID_ID.assertTrue(dto.getIds().isEmpty());
        removeByIds(dto.getIds());
    }

    @Override
    public MetaRegionVO detail(Long id){
        MetaRegion metaRegion = getById((Serializable) id);
        CommonResponseEnum.INVALID_ID.assertNull(metaRegion);
        return BeanCopyUtils.copy(metaRegion, MetaRegionVO.class);
    }

    @SneakyThrows
    @Override
    public void importExcel(ImportExcelDTO dto) {
        ExcelResult<MetaRegionImportDTO> excelResult = ExcelUtils.importExcel(dto.getFile().getInputStream(), MetaRegionImportDTO.class, true);
        List<MetaRegionImportDTO> list = excelResult.getList();
        List<String> errorList = excelResult.getErrorList();
        String analysis = excelResult.getAnalysis();
        System.out.println(" analysis : " + analysis);
        System.out.println(" isCover : " + dto.getIsCover());
    }

    @SneakyThrows
    @Override
    public void exportExcel(MetaRegionListDTO dto, HttpServletResponse response) {
        List<MetaRegionVO> list = list(dto);
        String fileName = "区域地址模板";
        OutputStream os = FileUtils.getOutputStream(response, fileName + ".xlsx");
        ExcelUtils.exportExcel(list, "区域地址", MetaRegionVO.class, os);
    }

    @Override
    public List<MetaRegion> listByLevel(int level) {
        return mapper.listByLevel(level);
    }

    private static QueryWrapper buildQueryWrapper(MetaRegionListDTO dto) {
    QueryWrapper wrapper = QueryWrapper.create().from(MetaRegion.class);
            wrapper.eq(MetaRegion::getParentId, dto.getParentId(), Utils.isNotNull(dto.getParentId()));
            wrapper.eq(MetaRegion::getAncestors, dto.getAncestors(), Utils.isNotNull(dto.getAncestors()));
            wrapper.like(MetaRegion::getName, dto.getName(), Utils.isNotNull(dto.getName()));
            wrapper.eq(MetaRegion::getPinyin, dto.getPinyin(), Utils.isNotNull(dto.getPinyin()));
            wrapper.eq(MetaRegion::getPinyinPrefix, dto.getPinyinPrefix(), Utils.isNotNull(dto.getPinyinPrefix()));
            wrapper.eq(MetaRegion::getLevel, dto.getLevel(), Utils.isNotNull(dto.getLevel()));
            wrapper.eq(MetaRegion::getEnable, dto.getEnable(), Utils.isNotNull(dto.getEnable()));
            wrapper.eq(MetaRegion::getTenantId, dto.getTenantId(), Utils.isNotNull(dto.getTenantId()));
    return wrapper;
    }
}
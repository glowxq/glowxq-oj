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
import com.glowxq.system.meta.mapper.MetaTagCategoryMapper;
import com.glowxq.system.meta.pojo.dto.MetaTagCategoryCreateDTO;
import com.glowxq.system.meta.pojo.dto.MetaTagCategoryImportDTO;
import com.glowxq.system.meta.pojo.dto.MetaTagCategoryListDTO;
import com.glowxq.system.meta.pojo.dto.MetaTagCategoryUpdateDTO;
import com.glowxq.system.meta.pojo.po.MetaTagCategory;
import com.glowxq.system.meta.pojo.vo.MetaTagCategoryVO;
import com.glowxq.system.meta.service.MetaTagCategoryService;
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
 * 标签分类 服务实现类
 * </p>
 *
 * @author glowxq
 * @since 2025-06-10
 */
@Service
@RequiredArgsConstructor
public class MetaTagCategoryServiceImpl extends ServiceImpl<MetaTagCategoryMapper, MetaTagCategory> implements MetaTagCategoryService {

    private static QueryWrapper buildQueryWrapper(MetaTagCategoryListDTO dto) {
        QueryWrapper wrapper = QueryWrapper.create().from(MetaTagCategory.class);
        wrapper.like(MetaTagCategory::getName, dto.getName(), Utils.isNotNull(dto.getName()));
        wrapper.eq(MetaTagCategory::getEnable, dto.getEnable(), Utils.isNotNull(dto.getEnable()));
        wrapper.eq(MetaTagCategory::getTenantId, dto.getTenantId(), Utils.isNotNull(dto.getTenantId()));
        return wrapper;
    }

    @Override
    public void create(MetaTagCategoryCreateDTO dto) {
        MetaTagCategory metaTagCategory = BeanCopyUtils.copy(dto, MetaTagCategory.class);
        save(metaTagCategory);
    }

    @Override
    public void update(MetaTagCategoryUpdateDTO dto) {
        MetaTagCategory metaTagCategory = BeanCopyUtils.copy(dto, MetaTagCategory.class);
        QueryWrapper wrapper;
        // id有效性校验
        wrapper = QueryWrapper.create()
                              .eq(MetaTagCategory::getId, dto.getId());
        CommonResponseEnum.INVALID_ID.assertTrue(count(wrapper) <= 0);

        saveOrUpdate(metaTagCategory);
    }

    @Override
    public PageResult<MetaTagCategoryVO> page(MetaTagCategoryListDTO dto) {
        Page<MetaTagCategoryVO> page = pageAs(PageUtils.getPage(dto), buildQueryWrapper(dto), MetaTagCategoryVO.class);
        return PageUtils.getPageResult(page);
    }

    @Override
    public List<MetaTagCategoryVO> list(MetaTagCategoryListDTO dto) {
        return listAs(buildQueryWrapper(dto), MetaTagCategoryVO.class);
    }

    @Override
    public void remove(SelectIdsDTO dto) {
        CommonResponseEnum.INVALID_ID.assertTrue(dto.getIds().isEmpty());
        removeByIds(dto.getIds());
    }

    @Override
    public MetaTagCategoryVO detail(Long id) {
        MetaTagCategory metaTagCategory = getById((Serializable) id);
        CommonResponseEnum.INVALID_ID.assertNull(metaTagCategory);
        return BeanCopyUtils.copy(metaTagCategory, MetaTagCategoryVO.class);
    }

    @SneakyThrows
    @Override
    public void importExcel(ImportExcelDTO dto) {
        ExcelResult<MetaTagCategoryImportDTO> excelResult = ExcelUtils.importExcel(dto.getFile().getInputStream(), MetaTagCategoryImportDTO.class, true);
        List<MetaTagCategoryImportDTO> list = excelResult.getList();
        List<String> errorList = excelResult.getErrorList();
        String analysis = excelResult.getAnalysis();
        System.out.println(" analysis : " + analysis);
        System.out.println(" isCover : " + dto.getIsCover());
    }

    @SneakyThrows
    @Override
    public void exportExcel(MetaTagCategoryListDTO dto, HttpServletResponse response) {
        List<MetaTagCategoryVO> list = list(dto);
        String fileName = "标签分类模板";
        OutputStream os = FileUtils.getOutputStream(response, fileName + ".xlsx");
        ExcelUtils.exportExcel(list, "标签分类", MetaTagCategoryVO.class, os);
    }
}
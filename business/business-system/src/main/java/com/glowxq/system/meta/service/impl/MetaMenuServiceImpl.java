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
import com.glowxq.system.meta.mapper.MetaMenuMapper;
import com.glowxq.system.meta.pojo.dto.MetaMenuCreateDTO;
import com.glowxq.system.meta.pojo.dto.MetaMenuImportDTO;
import com.glowxq.system.meta.pojo.dto.MetaMenuListDTO;
import com.glowxq.system.meta.pojo.dto.MetaMenuUpdateDTO;
import com.glowxq.system.meta.pojo.po.MetaMenu;
import com.glowxq.system.meta.pojo.vo.MetaMenuVO;
import com.glowxq.system.meta.service.MetaMenuService;
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
 * 菜单 服务实现类
 * </p>
 *
 * @author glowxq
 * @since 2025-04-23
 */
@Service
@RequiredArgsConstructor
public class MetaMenuServiceImpl extends ServiceImpl<MetaMenuMapper, MetaMenu> implements MetaMenuService {
    @Override
    public void create(MetaMenuCreateDTO dto){
        MetaMenu metaMenu = BeanCopyUtils.copy(dto, MetaMenu.class);
        save(metaMenu);
    }

    @Override
    public void update(MetaMenuUpdateDTO dto){
        MetaMenu metaMenu = BeanCopyUtils.copy(dto, MetaMenu.class);
        QueryWrapper wrapper;
        // id有效性校验
        wrapper = QueryWrapper.create()
            .eq(MetaMenu::getId, dto.getId());
        CommonResponseEnum.INVALID_ID.assertTrue(count(wrapper) <= 0);

        saveOrUpdate(metaMenu);
    }

    @Override
    public PageResult<MetaMenuVO> page(MetaMenuListDTO dto){
        Page<MetaMenuVO> page = pageAs(PageUtils.getPage(dto), buildQueryWrapper(dto), MetaMenuVO.class);
        return PageUtils.getPageResult(page);
    }

    @Override
    public List<MetaMenuVO> list(MetaMenuListDTO dto){
        return listAs(buildQueryWrapper(dto), MetaMenuVO.class);
    }

    @Override
    public void remove(SelectIdsDTO dto){
        CommonResponseEnum.INVALID_ID.assertTrue(dto.getIds().isEmpty());
        removeByIds(dto.getIds());
    }

    @Override
    public MetaMenuVO detail(Object id){
        MetaMenu metaMenu = getById((Serializable) id);
        CommonResponseEnum.INVALID_ID.assertNull(metaMenu);
        return BeanCopyUtils.copy(metaMenu, MetaMenuVO.class);
    }

    @SneakyThrows
    @Override
    public void importExcel(ImportExcelDTO dto) {
        ExcelResult<MetaMenuImportDTO> excelResult = ExcelUtils.importExcel(dto.getFile().getInputStream(), MetaMenuImportDTO.class, true);
        List<MetaMenuImportDTO> list = excelResult.getList();
        List<String> errorList = excelResult.getErrorList();
        String analysis = excelResult.getAnalysis();
        System.out.println(" analysis : " + analysis);
        System.out.println(" isCover : " + dto.getIsCover());
    }

    @SneakyThrows
    @Override
    public void exportExcel(MetaMenuListDTO dto, HttpServletResponse response) {
        List<MetaMenuVO> list = list(dto);
        String fileName = "菜单模板";
        OutputStream os = FileUtils.getOutputStream(response, fileName + ".xlsx");
        ExcelUtils.exportExcel(list, "菜单", MetaMenuVO.class, os);
    }

    private static QueryWrapper buildQueryWrapper(MetaMenuListDTO dto) {
    QueryWrapper wrapper = QueryWrapper.create().from(MetaMenu.class);
            wrapper.like(MetaMenu::getName, dto.getName(), Utils.isNotNull(dto.getName()));
            wrapper.eq(MetaMenu::getActiveIcon, dto.getActiveIcon(), Utils.isNotNull(dto.getActiveIcon()));
            wrapper.eq(MetaMenu::getInactiveIcon, dto.getInactiveIcon(), Utils.isNotNull(dto.getInactiveIcon()));
            wrapper.eq(MetaMenu::getIconType, dto.getIconType(), Utils.isNotNull(dto.getIconType()));
            wrapper.eq(MetaMenu::getType, dto.getType(), Utils.isNotNull(dto.getType()));
            wrapper.eq(MetaMenu::getPath, dto.getPath(), Utils.isNotNull(dto.getPath()));
            wrapper.eq(MetaMenu::getEnable, dto.getEnable(), Utils.isNotNull(dto.getEnable()));
            wrapper.eq(MetaMenu::getSort, dto.getSort(), Utils.isNotNull(dto.getSort()));
            wrapper.eq(MetaMenu::getHasChildren, dto.getHasChildren(), Utils.isNotNull(dto.getHasChildren()));
            wrapper.eq(MetaMenu::getLock, dto.getLock(), Utils.isNotNull(dto.getLock()));
    return wrapper;
    }
}
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
import com.glowxq.system.meta.mapper.MetaTagBindMapper;
import com.glowxq.system.meta.pojo.dto.MetaTagBindCreateDTO;
import com.glowxq.system.meta.pojo.dto.MetaTagBindImportDTO;
import com.glowxq.system.meta.pojo.dto.MetaTagBindListDTO;
import com.glowxq.system.meta.pojo.dto.MetaTagBindUpdateDTO;
import com.glowxq.system.meta.pojo.po.MetaTagBind;
import com.glowxq.system.meta.pojo.vo.MetaTagBindVO;
import com.glowxq.system.meta.service.MetaTagBindService;
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
 * 绑定标签权限 服务实现类
 * </p>
 *
 * @author glowxq
 * @since 2025-04-23
 */
@Service
@RequiredArgsConstructor
public class MetaTagBindServiceImpl extends ServiceImpl<MetaTagBindMapper, MetaTagBind> implements MetaTagBindService {
    @Override
    public void create(MetaTagBindCreateDTO dto){
        MetaTagBind metaTagBind = BeanCopyUtils.copy(dto, MetaTagBind.class);
        save(metaTagBind);
    }

    @Override
    public void update(MetaTagBindUpdateDTO dto){
        MetaTagBind metaTagBind = BeanCopyUtils.copy(dto, MetaTagBind.class);
        QueryWrapper wrapper;
        // id有效性校验
        wrapper = QueryWrapper.create()
            .eq(MetaTagBind::getId, dto.getId());
        CommonResponseEnum.INVALID_ID.assertTrue(count(wrapper) <= 0);

        saveOrUpdate(metaTagBind);
    }

    @Override
    public PageResult<MetaTagBindVO> page(MetaTagBindListDTO dto){
        Page<MetaTagBindVO> page = pageAs(PageUtils.getPage(dto), buildQueryWrapper(dto), MetaTagBindVO.class);
        return PageUtils.getPageResult(page);
    }

    @Override
    public List<MetaTagBindVO> list(MetaTagBindListDTO dto){
        return listAs(buildQueryWrapper(dto), MetaTagBindVO.class);
    }

    @Override
    public void remove(SelectIdsDTO dto){
        CommonResponseEnum.INVALID_ID.assertTrue(dto.getIds().isEmpty());
        removeByIds(dto.getIds());
    }

    @Override
    public MetaTagBindVO detail(Object id){
        MetaTagBind metaTagBind = getById((Serializable) id);
        CommonResponseEnum.INVALID_ID.assertNull(metaTagBind);
        return BeanCopyUtils.copy(metaTagBind, MetaTagBindVO.class);
    }

    @SneakyThrows
    @Override
    public void importExcel(ImportExcelDTO dto) {
        ExcelResult<MetaTagBindImportDTO> excelResult = ExcelUtils.importExcel(dto.getFile().getInputStream(), MetaTagBindImportDTO.class, true);
        List<MetaTagBindImportDTO> list = excelResult.getList();
        List<String> errorList = excelResult.getErrorList();
        String analysis = excelResult.getAnalysis();
        System.out.println(" analysis : " + analysis);
        System.out.println(" isCover : " + dto.getIsCover());
    }

    @SneakyThrows
    @Override
    public void exportExcel(MetaTagBindListDTO dto, HttpServletResponse response) {
        List<MetaTagBindVO> list = list(dto);
        String fileName = "绑定标签权限模板";
        OutputStream os = FileUtils.getOutputStream(response, fileName + ".xlsx");
        ExcelUtils.exportExcel(list, "绑定标签权限", MetaTagBindVO.class, os);
    }

    private static QueryWrapper buildQueryWrapper(MetaTagBindListDTO dto) {
    QueryWrapper wrapper = QueryWrapper.create().from(MetaTagBind.class);
            wrapper.eq(MetaTagBind::getTagId, dto.getTagId(), Utils.isNotNull(dto.getTagId()));
            wrapper.eq(MetaTagBind::getBusinessId, dto.getBusinessId(), Utils.isNotNull(dto.getBusinessId()));
            wrapper.eq(MetaTagBind::getType, dto.getType(), Utils.isNotNull(dto.getType()));
    return wrapper;
    }
}
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
import com.glowxq.system.meta.mapper.MetaTextMapper;
import com.glowxq.system.meta.pojo.dto.MetaTextCreateDTO;
import com.glowxq.system.meta.pojo.dto.MetaTextImportDTO;
import com.glowxq.system.meta.pojo.dto.MetaTextListDTO;
import com.glowxq.system.meta.pojo.dto.MetaTextUpdateDTO;
import com.glowxq.system.meta.pojo.po.MetaText;
import com.glowxq.system.meta.pojo.vo.MetaTextVO;
import com.glowxq.system.meta.service.MetaTextService;
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
 * 文本 服务实现类
 * </p>
 *
 * @author glowxq
 * @since 2025-04-23
 */
@Service
@RequiredArgsConstructor
public class MetaTextServiceImpl extends ServiceImpl<MetaTextMapper, MetaText> implements MetaTextService {
    @Override
    public void create(MetaTextCreateDTO dto){
        MetaText metaText = BeanCopyUtils.copy(dto, MetaText.class);
        save(metaText);
    }

    @Override
    public void update(MetaTextUpdateDTO dto){
        MetaText metaText = BeanCopyUtils.copy(dto, MetaText.class);
        QueryWrapper wrapper;
        // id有效性校验
        wrapper = QueryWrapper.create()
            .eq(MetaText::getId, dto.getId());
        CommonResponseEnum.INVALID_ID.assertTrue(count(wrapper) <= 0);

        saveOrUpdate(metaText);
    }

    @Override
    public PageResult<MetaTextVO> page(MetaTextListDTO dto){
        Page<MetaTextVO> page = pageAs(PageUtils.getPage(dto), buildQueryWrapper(dto), MetaTextVO.class);
        return PageUtils.getPageResult(page);
    }

    @Override
    public List<MetaTextVO> list(MetaTextListDTO dto){
        return listAs(buildQueryWrapper(dto), MetaTextVO.class);
    }

    @Override
    public void remove(SelectIdsDTO dto){
        CommonResponseEnum.INVALID_ID.assertTrue(dto.getIds().isEmpty());
        removeByIds(dto.getIds());
    }

    @Override
    public MetaTextVO detail(Object id){
        MetaText metaText = getById((Serializable) id);
        CommonResponseEnum.INVALID_ID.assertNull(metaText);
        return BeanCopyUtils.copy(metaText, MetaTextVO.class);
    }

    @SneakyThrows
    @Override
    public void importExcel(ImportExcelDTO dto) {
        ExcelResult<MetaTextImportDTO> excelResult = ExcelUtils.importExcel(dto.getFile().getInputStream(), MetaTextImportDTO.class, true);
        List<MetaTextImportDTO> list = excelResult.getList();
        List<String> errorList = excelResult.getErrorList();
        String analysis = excelResult.getAnalysis();
        System.out.println(" analysis : " + analysis);
        System.out.println(" isCover : " + dto.getIsCover());
    }

    @SneakyThrows
    @Override
    public void exportExcel(MetaTextListDTO dto, HttpServletResponse response) {
        List<MetaTextVO> list = list(dto);
        String fileName = "文本模板";
        OutputStream os = FileUtils.getOutputStream(response, fileName + ".xlsx");
        ExcelUtils.exportExcel(list, "文本", MetaTextVO.class, os);
    }

    @Override
    public MetaText getByKey(String key) {
        return mapper.getByKey(key);
    }

    private static QueryWrapper buildQueryWrapper(MetaTextListDTO dto) {
    QueryWrapper wrapper = QueryWrapper.create().from(MetaText.class);
            wrapper.like(MetaText::getName, dto.getName(), Utils.isNotNull(dto.getName()));
            wrapper.eq(MetaText::getTextKey, dto.getTextKey(), Utils.isNotNull(dto.getTextKey()));
            wrapper.eq(MetaText::getTextType, dto.getTextType(), Utils.isNotNull(dto.getTextType()));
            wrapper.eq(MetaText::getIcon, dto.getIcon(), Utils.isNotNull(dto.getIcon()));
            wrapper.eq(MetaText::getBusinessType, dto.getBusinessType(), Utils.isNotNull(dto.getBusinessType()));
            wrapper.eq(MetaText::getTitle, dto.getTitle(), Utils.isNotNull(dto.getTitle()));
            wrapper.eq(MetaText::getSkipUrl, dto.getSkipUrl(), Utils.isNotNull(dto.getSkipUrl()));
            wrapper.eq(MetaText::getContent, dto.getContent(), Utils.isNotNull(dto.getContent()));
            wrapper.eq(MetaText::getSort, dto.getSort(), Utils.isNotNull(dto.getSort()));
            wrapper.eq(MetaText::getEnable, dto.getEnable(), Utils.isNotNull(dto.getEnable()));
    return wrapper;
    }
}
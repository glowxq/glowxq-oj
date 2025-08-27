package com.glowxq.oj.meta.service.impl;

import com.glowxq.core.common.entity.ImportExcelDTO;
import com.glowxq.core.common.entity.PageResult;
import com.glowxq.core.common.entity.SelectIdsDTO;
import com.glowxq.core.common.enums.CommonResponseEnum;
import com.glowxq.core.common.exception.common.BusinessException;
import com.glowxq.core.util.*;
import com.glowxq.excel.core.ExcelResult;
import com.glowxq.excel.utils.ExcelUtils;
import com.glowxq.oj.meta.mapper.MetaLanguageMapper;
import com.glowxq.oj.meta.pojo.dto.MetaLanguageCreateDTO;
import com.glowxq.oj.meta.pojo.dto.MetaLanguageImportDTO;
import com.glowxq.oj.meta.pojo.dto.MetaLanguageListDTO;
import com.glowxq.oj.meta.pojo.dto.MetaLanguageUpdateDTO;
import com.glowxq.oj.meta.pojo.po.MetaLanguage;
import com.glowxq.oj.meta.pojo.vo.MetaLanguageVO;
import com.glowxq.oj.meta.service.MetaLanguageService;
import com.glowxq.oj.problem.enums.ProblemSourceType;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.io.OutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 语言 服务实现类
 * </p>
 *
 * @author glowxq
 * @since 2025-03-17
 */
@Service
@RequiredArgsConstructor
public class MetaLanguageServiceImpl extends ServiceImpl<MetaLanguageMapper, MetaLanguage> implements MetaLanguageService {

    private static QueryWrapper buildQueryWrapper(MetaLanguageListDTO dto) {
        QueryWrapper wrapper = QueryWrapper.create().from(MetaLanguage.class);
        wrapper.eq(MetaLanguage::getContentType, dto.getContentType(), Utils.isNotNull(dto.getContentType()));
        wrapper.eq(MetaLanguage::getDescription, dto.getDescription(), Utils.isNotNull(dto.getDescription()));
        wrapper.like(MetaLanguage::getName, dto.getName(), Utils.isNotNull(dto.getName()));
        wrapper.eq(MetaLanguage::getCompileCommand, dto.getCompileCommand(), Utils.isNotNull(dto.getCompileCommand()));
        wrapper.eq(MetaLanguage::getTemplate, dto.getTemplate(), Utils.isNotNull(dto.getTemplate()));
        wrapper.eq(MetaLanguage::getCodeTemplate, dto.getCodeTemplate(), Utils.isNotNull(dto.getCodeTemplate()));
        wrapper.eq(MetaLanguage::getSpjEnable, dto.getSpjEnable(), Utils.isNotNull(dto.getSpjEnable()));
        wrapper.eq(MetaLanguage::getOj, dto.getOj(), Utils.isNotNull(dto.getOj()));
        wrapper.eq(MetaLanguage::getSeq, dto.getSeq(), Utils.isNotNull(dto.getSeq()));
        return wrapper;
    }

    @Override
    public void create(MetaLanguageCreateDTO dto) {
        MetaLanguage metaLanguage = BeanCopyUtils.copy(dto, MetaLanguage.class);
        save(metaLanguage);
    }

    @Override
    public void update(MetaLanguageUpdateDTO dto) {
        MetaLanguage metaLanguage = BeanCopyUtils.copy(dto, MetaLanguage.class);
        QueryWrapper wrapper;
        // id有效性校验
        wrapper = QueryWrapper.create()
                              .eq(MetaLanguage::getId, dto.getId());
        CommonResponseEnum.INVALID_ID.assertTrue(count(wrapper) <= 0);

        saveOrUpdate(metaLanguage);
    }

    @Override
    public PageResult<MetaLanguageVO> page(MetaLanguageListDTO dto) {
        Page<MetaLanguageVO> page = pageAs(PageUtils.getPage(dto), buildQueryWrapper(dto), MetaLanguageVO.class);
        return PageUtils.getPageResult(page);
    }

    @Override
    public List<MetaLanguageVO> list(MetaLanguageListDTO dto) {
        return listAs(buildQueryWrapper(dto), MetaLanguageVO.class);
    }

    @Override
    public Map<String, Long> mapFromNameToId(ProblemSourceType problemSourceType) {
        // 查询指定 OJ 的记录
        List<MetaLanguage> allLanguages = mapper.listByOj(problemSourceType);

        // 创建一个 Map，用于存储 name -> id 的映射关系
        Map<String, Long> map = new HashMap<>();
        for (MetaLanguage language : allLanguages) {
            Long id = language.getId();
            String name = language.getName();

            if (map.containsKey(name)) {
                throw new BusinessException("语言存在重复的名称，请检查：" + name);
            }

            // 确保 id 和 name 都不为空
            if (StringUtils.isNotBlank(name) && id != null) {
                map.put(name, id);
            }
        }

        // 返回 Map
        return map;
    }

    @Override
    public void remove(SelectIdsDTO dto) {
        CommonResponseEnum.INVALID_ID.assertTrue(dto.getIds().isEmpty());
        removeByIds(dto.getIds());
    }

    @Override
    public MetaLanguageVO detail(Object id) {
        MetaLanguage metaLanguage = getById((Serializable) id);
        CommonResponseEnum.INVALID_ID.assertNull(metaLanguage);
        return BeanCopyUtils.copy(metaLanguage, MetaLanguageVO.class);
    }

    @SneakyThrows
    @Override
    public void importExcel(ImportExcelDTO dto) {
        ExcelResult<MetaLanguageImportDTO> excelResult = ExcelUtils.importExcel(dto.getFile().getInputStream(), MetaLanguageImportDTO.class, true);
        List<MetaLanguageImportDTO> list = excelResult.getList();
        List<String> errorList = excelResult.getErrorList();
        String analysis = excelResult.getAnalysis();
        System.out.println(" analysis : " + analysis);
        System.out.println(" isCover : " + dto.getIsCover());
    }

    @SneakyThrows
    @Override
    public void exportExcel(MetaLanguageListDTO dto, HttpServletResponse response) {
        List<MetaLanguageVO> list = list(dto);
        String fileName = "语言模板";
        OutputStream os = FileUtils.getOutputStream(response, fileName + ".xlsx");
        ExcelUtils.exportExcel(list, "语言", MetaLanguageVO.class, os);
    }
}
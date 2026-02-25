package com.glowxq.system.meta.service.impl;

import com.glowxq.core.common.entity.ImportExcelDTO;
import com.glowxq.core.common.entity.PageResult;
import com.glowxq.core.common.entity.SelectIdsDTO;
import com.glowxq.core.common.enums.CommonResponseEnum;
import com.glowxq.core.common.enums.DeleteFlag;
import com.glowxq.core.common.exception.common.BusinessException;
import com.glowxq.core.util.*;
import com.glowxq.excel.core.ExcelResult;
import com.glowxq.excel.utils.ExcelUtils;
import com.glowxq.system.meta.base.TagBindEnum;
import com.glowxq.system.meta.mapper.MetaTagBindMapper;
import com.glowxq.system.meta.mapper.MetaTagMapper;
import com.glowxq.system.meta.pojo.dto.*;
import com.glowxq.system.meta.pojo.po.MetaTag;
import com.glowxq.system.meta.pojo.po.MetaTagBind;
import com.glowxq.system.meta.pojo.vo.MetaTagCountVO;
import com.glowxq.system.meta.pojo.vo.MetaTagVO;
import com.glowxq.system.meta.service.MetaTagService;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.io.OutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 标签 服务实现类
 * </p>
 *
 * @author glowxq
 * @since 2025-04-23
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MetaTagServiceImpl extends ServiceImpl<MetaTagMapper, MetaTag> implements MetaTagService {

    private final MetaTagBindMapper metaTagBindMapper;

    private static QueryWrapper buildQueryWrapper(MetaTagListDTO dto) {
        QueryWrapper wrapper = QueryWrapper.create().from(MetaTag.class);
        wrapper.like(MetaTag::getName, dto.getName(), Utils.isNotNull(dto.getName()));
        wrapper.eq(MetaTag::getBackgroundColor, dto.getBackgroundColor(), Utils.isNotNull(dto.getBackgroundColor()));
        wrapper.eq(MetaTag::getCategoryId, dto.getCategoryId(), Utils.isNotNull(dto.getCategoryId()));
        wrapper.eq(MetaTag::getTextColor, dto.getTextColor(), Utils.isNotNull(dto.getTextColor()));
        wrapper.eq(MetaTag::getEnable, dto.getEnable(), Utils.isNotNull(dto.getEnable()));
        return wrapper;
    }

    @Override
    public void create(MetaTagCreateDTO dto) {
        MetaTag metaTag = BeanCopyUtils.copy(dto, MetaTag.class);
        save(metaTag);
    }

    @Override
    public void create(List<MetaTagCreateDTO> dtoList) {
        if (CollectionUtils.isEmpty(dtoList)) {
            return;
        }

        List<MetaTag> metaTagList = dtoList.stream().map(dto -> BeanCopyUtils.copy(dto, MetaTag.class)).toList();
        saveBatch(metaTagList);
    }

    @Override
    public void update(MetaTagUpdateDTO dto) {
        MetaTag metaTag = BeanCopyUtils.copy(dto, MetaTag.class);
        QueryWrapper wrapper;
        // id有效性校验
        wrapper = QueryWrapper.create()
                              .eq(MetaTag::getId, dto.getId());
        CommonResponseEnum.INVALID_ID.assertTrue(count(wrapper) <= 0);

        saveOrUpdate(metaTag);
    }

    @Override
    public PageResult<MetaTagVO> page(MetaTagListDTO dto) {
        Page<MetaTagVO> page = pageAs(PageUtils.getPage(dto), buildQueryWrapper(dto), MetaTagVO.class);
        return PageUtils.getPageResult(page);
    }

    @Override
    public List<MetaTagVO> list(MetaTagListDTO dto) {
        return listAs(buildQueryWrapper(dto), MetaTagVO.class);
    }

    @Override
    public Map<String, Long> mapFromNameToId() {

        // 查询启用的标签
        List<MetaTag> allTags = mapper.listByEnable(true);

        // 创建一个 Map，用于存储 name -> id 的映射关系
        Map<String, Long> map = new HashMap<>();
        for (MetaTag tag : allTags) {
            Long id = tag.getId();
            String name = tag.getName();

            if (map.containsKey(name)) {
                throw new BusinessException("标签存在重复的名称，请检查：" + name);
            }

            // 确保 id 和 name 都不为空
            if (id != null && StringUtils.isNotBlank(name)) {
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
    public MetaTagVO detail(Object id) {
        MetaTag metaTag = getById((Serializable) id);
        CommonResponseEnum.INVALID_ID.assertNull(metaTag);
        return BeanCopyUtils.copy(metaTag, MetaTagVO.class);
    }

    @SneakyThrows
    @Override
    public void importExcel(ImportExcelDTO dto) {
        ExcelResult<MetaTagImportDTO> excelResult = ExcelUtils.importExcel(dto.getFile().getInputStream(), MetaTagImportDTO.class, true);
        List<MetaTagImportDTO> list = excelResult.getList();
        List<String> errorList = excelResult.getErrorList();
        String analysis = excelResult.getAnalysis();
        System.out.println(" analysis : " + analysis);
        System.out.println(" isCover : " + dto.getIsCover());
    }

    @SneakyThrows
    @Override
    public void exportExcel(MetaTagListDTO dto, HttpServletResponse response) {
        List<MetaTagVO> list = list(dto);
        String fileName = "标签模板";
        OutputStream os = FileUtils.getOutputStream(response, fileName + ".xlsx");
        ExcelUtils.exportExcel(list, "标签", MetaTagVO.class, os);
    }

    @Override
    public void bindTags(Long bindId, List<Long> tagIds, TagBindEnum tagBindType) {
        if (CollectionUtils.isEmpty(tagIds) || bindId == null || tagBindType == null) {
            log.warn("标签绑定失败，参数错误");
            return;
        }
        List<MetaTagBind> tagBindList = tagIds.stream().map(tagId -> {
            MetaTagBind metaTagBind = new MetaTagBind();
            metaTagBind.setId(null);
            metaTagBind.setTagId(tagId);
            metaTagBind.setBusinessId(bindId);
            metaTagBind.setDelFlag(DeleteFlag.F.getCode());
            metaTagBind.setType(tagBindType.getCode());
            return metaTagBind;
        }).collect(Collectors.toList());
        metaTagBindMapper.insertBatch(tagBindList);
    }

    @Override
    public void deleteByBusinessId(Long businessId, TagBindEnum bindType) {
        metaTagBindMapper.deleteByBusinessId(businessId, bindType);
    }

    @Override
    public void unBindAll(Long businessId, TagBindEnum tagBindType) {
        metaTagBindMapper.deleteByBusinessId(businessId, tagBindType);
    }

    @Override
    public List<MetaTag> listByBusinessId(Long businessId, TagBindEnum tagBindType) {
        List<MetaTagBind> metaTagBinds = metaTagBindMapper.listByBusinessId(businessId, tagBindType);
        if (CollectionUtils.isEmpty(metaTagBinds)) {
            return List.of();
        }

        List<Long> tagIds = metaTagBinds.stream().map(MetaTagBind::getTagId).toList();
        return mapper.selectListByIds(tagIds);
    }

    @Override
    public List<MetaTag> listByBusinessIds(List<Long> businessIds, TagBindEnum tagBindType) {
        List<MetaTagBind> metaTagBinds = metaTagBindMapper.listByBusinessIds(businessIds, tagBindType);
        if (CollectionUtils.isEmpty(metaTagBinds)) {
            return List.of();
        }

        List<Long> tagIds = metaTagBinds.stream().map(MetaTagBind::getTagId).toList();
        return mapper.selectListByIds(tagIds);
    }

    @Override
    public List<Long> listBusinessIdByTagIds(List<Long> tagIds, TagBindEnum tagBindType) {
        if (CollectionUtils.isEmpty(tagIds) || tagBindType == null) {
            return List.of();
        }
        List<MetaTagBind> metaTagBinds = metaTagBindMapper.listByTagIds(tagIds, tagBindType);
        return metaTagBinds.stream().map(MetaTagBind::getBusinessId).toList();
    }

    @Override
    public void unBindsAll(List<Long> businessIds, TagBindEnum tagBindType) {
        metaTagBindMapper.deleteByBusinessIds(businessIds, tagBindType);
    }

    @Override
    public List<MetaTagCountVO> categoryCount() {
        return mapper.categoryCount();
    }

    @Override
    public void batchUpdateCategory(MetaTagBatchUpdateCategoryDTO dto) {
        List<Long> tagIds = dto.getTagIds();
        List<MetaTag> metaTags = mapper.selectListByIds(tagIds);
        metaTags.forEach(metaTag -> metaTag.setCategoryId(dto.getCategoryId()));
        this.updateBatch(metaTags);
    }
}
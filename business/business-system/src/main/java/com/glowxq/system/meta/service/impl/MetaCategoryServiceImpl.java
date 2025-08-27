package com.glowxq.system.meta.service.impl;

import com.glowxq.core.common.entity.ImportExcelDTO;
import com.glowxq.core.common.entity.PageResult;
import com.glowxq.core.common.entity.SelectIdsDTO;
import com.glowxq.core.common.enums.CommonResponseEnum;
import com.glowxq.core.util.*;
import com.glowxq.excel.core.ExcelResult;
import com.glowxq.excel.utils.ExcelUtils;
import com.glowxq.system.meta.mapper.MetaCategoryMapper;
import com.glowxq.system.meta.pojo.dto.MetaCategoryCreateDTO;
import com.glowxq.system.meta.pojo.dto.MetaCategoryImportDTO;
import com.glowxq.system.meta.pojo.dto.MetaCategoryListDTO;
import com.glowxq.system.meta.pojo.dto.MetaCategoryUpdateDTO;
import com.glowxq.system.meta.pojo.po.MetaCategory;
import com.glowxq.system.meta.pojo.vo.MetaCategoryTreeVO;
import com.glowxq.system.meta.pojo.vo.MetaCategoryVO;
import com.glowxq.system.meta.service.MetaCategoryClosureService;
import com.glowxq.system.meta.service.MetaCategoryService;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 分类 服务实现类
 * </p>
 *
 * @author glowxq
 * @since 2025-04-23
 */
@Service
@RequiredArgsConstructor
public class MetaCategoryServiceImpl extends ServiceImpl<MetaCategoryMapper, MetaCategory> implements MetaCategoryService {

    private final MetaCategoryClosureService metaCategoryClosureService;

    private static QueryWrapper buildQueryWrapper(MetaCategoryListDTO dto) {
        QueryWrapper wrapper = QueryWrapper.create().from(MetaCategory.class);
        wrapper.like(MetaCategory::getName, dto.getName(), Utils.isNotNull(dto.getName()));
        wrapper.eq(MetaCategory::getPid, dto.getPid(), Utils.isNotNull(dto.getPid()));
        wrapper.eq(MetaCategory::getDeep, dto.getDeep(), Utils.isNotNull(dto.getDeep()));
        wrapper.eq(MetaCategory::getSort, dto.getSort(), Utils.isNotNull(dto.getSort()));
        wrapper.eq(MetaCategory::getEnable, dto.getEnable(), Utils.isNotNull(dto.getEnable()));
        wrapper.eq(MetaCategory::getHasChildren, dto.getHasChildren(), Utils.isNotNull(dto.getHasChildren()));
        wrapper.eq(MetaCategory::getLock, dto.getLock(), Utils.isNotNull(dto.getLock()));
        return wrapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(MetaCategoryCreateDTO dto) {
        MetaCategory metaCategory = BeanCopyUtils.copy(dto, MetaCategory.class);
        if (dto.getPid() == 0) {
            metaCategory.setDeep(1L);
        }
        else {
            QueryWrapper wrapper = QueryWrapper.create().eq(MetaCategory::getId, dto.getPid());
            CommonResponseEnum.INVALID_ID.assertTrue(count(wrapper) <= 0);
            MetaCategory parentCategory = getOne(wrapper);
            parentCategory.setHasChildren(true);
            saveOrUpdate(parentCategory);
            metaCategory.setDeep(parentCategory.getDeep() + 1);
        }
        save(metaCategory);
        Long categoryId = metaCategory.getId();
        Long pid = dto.getPid();
        metaCategoryClosureService.create(categoryId, pid);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(MetaCategoryUpdateDTO dto) {
        MetaCategory metaCategory = BeanCopyUtils.copy(dto, MetaCategory.class);
        QueryWrapper wrapper;
        // id有效性校验
        wrapper = QueryWrapper.create()
                              .eq(MetaCategory::getId, dto.getId());

        MetaCategory one = getOne(wrapper);
        Long oldPid = one.getPid();
        CommonResponseEnum.INVALID_ID.assertTrue(count(wrapper) <= 0);
        saveOrUpdate(metaCategory);
        if (dto.getPid() ==0){
            metaCategory.setDeep(1L);
        }else {
            wrapper = QueryWrapper.create().eq(MetaCategory::getId, dto.getPid());
            MetaCategory parent = getOne(wrapper);
            one.setHasChildren(true);
            saveOrUpdate(one);
            metaCategory.setDeep(parent.getDeep() + 1);
        }
        saveOrUpdate(metaCategory);
        if (!Objects.equals(oldPid, dto.getPid())) {
            metaCategoryClosureService.move(dto.getId(), dto.getPid());
        }

    }

    @Override
    public PageResult<MetaCategoryVO> page(MetaCategoryListDTO dto) {
        Page<MetaCategoryVO> page = pageAs(PageUtils.getPage(dto), buildQueryWrapper(dto), MetaCategoryVO.class);
        return PageUtils.getPageResult(page);
    }

    @Override
    public List<MetaCategoryVO> list(MetaCategoryListDTO dto) {
        return listAs(buildQueryWrapper(dto), MetaCategoryVO.class);
    }

    @Override
    public void remove(SelectIdsDTO dto) {
        CommonResponseEnum.INVALID_ID.assertTrue(dto.getIds().isEmpty());
        removeByIds(dto.getIds());
    }

    @Override
    public MetaCategoryVO detail(Object id) {
        MetaCategory metaCategory = getById((Serializable) id);
        CommonResponseEnum.INVALID_ID.assertNull(metaCategory);
        return BeanCopyUtils.copy(metaCategory, MetaCategoryVO.class);
    }

    @SneakyThrows
    @Override
    public void importExcel(ImportExcelDTO dto) {
        ExcelResult<MetaCategoryImportDTO> excelResult = ExcelUtils.importExcel(dto.getFile().getInputStream(), MetaCategoryImportDTO.class, true);
        List<MetaCategoryImportDTO> list = excelResult.getList();
        List<String> errorList = excelResult.getErrorList();
        String analysis = excelResult.getAnalysis();
        System.out.println(" analysis : " + analysis);
        System.out.println(" isCover : " + dto.getIsCover());
    }

    @SneakyThrows
    @Override
    public void exportExcel(MetaCategoryListDTO dto, HttpServletResponse response) {
        List<MetaCategoryVO> list = list(dto);
        String fileName = "分类模板";
        OutputStream os = FileUtils.getOutputStream(response, fileName + ".xlsx");
        ExcelUtils.exportExcel(list, "分类", MetaCategoryVO.class, os);
    }

    @Override
    public List<MetaCategoryTreeVO> getTree(Integer excludeNodeId, Boolean appendRoot, Boolean needSetTotal) {
        QueryWrapper wrapper = QueryWrapper.create()
                                           // .orderBy(SysDept::getDeep).asc()
                                           .orderBy(MetaCategory::getSort).asc();
        // 获取所有部门信息
        List<MetaCategory> list = list(wrapper);
        List<MetaCategoryTreeVO> metaCategoryTreeVoList = BeanCopyUtils.copyList(list, MetaCategoryTreeVO.class);

        MetaCategoryTreeVO root = TreeUtils.getRoot(MetaCategoryTreeVO.class);
        assert root != null;
        root.setName("根分类");
        List<MetaCategoryTreeVO> trees = TreeUtils.buildTree(metaCategoryTreeVoList, root, excludeNodeId);
        if (appendRoot != null && !appendRoot) {
            if (trees.getFirst().getChildren() == null) {
                trees = new ArrayList<>();
            }
            else {
                trees = trees.getFirst().getChildren();
            }
        }
        return trees;
    }
}
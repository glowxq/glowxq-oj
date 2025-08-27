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
import com.glowxq.system.meta.mapper.MetaCategoryClosureMapper;
import com.glowxq.system.meta.pojo.dto.MetaCategoryClosureCreateDTO;
import com.glowxq.system.meta.pojo.dto.MetaCategoryClosureImportDTO;
import com.glowxq.system.meta.pojo.dto.MetaCategoryClosureListDTO;
import com.glowxq.system.meta.pojo.dto.MetaCategoryClosureUpdateDTO;
import com.glowxq.system.meta.pojo.po.MetaCategoryClosure;
import com.glowxq.system.meta.pojo.vo.MetaCategoryClosureVO;
import com.glowxq.system.meta.service.MetaCategoryClosureService;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryMethods;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 分类祖籍关系表 服务实现类
 * </p>
 *
 * @author glowxq
 * @since 2025-04-24
 */
@Service
@RequiredArgsConstructor
public class MetaCategoryClosureServiceImpl extends ServiceImpl<MetaCategoryClosureMapper, MetaCategoryClosure> implements MetaCategoryClosureService {

    private static QueryWrapper buildQueryWrapper(MetaCategoryClosureListDTO dto) {
        QueryWrapper wrapper = QueryWrapper.create().from(MetaCategoryClosure.class);
        wrapper.eq(MetaCategoryClosure::getAncestorId, dto.getAncestorId(), Utils.isNotNull(dto.getAncestorId()));
        wrapper.eq(MetaCategoryClosure::getDescendantId, dto.getDescendantId(), Utils.isNotNull(dto.getDescendantId()));
        wrapper.eq(MetaCategoryClosure::getDepth, dto.getDepth(), Utils.isNotNull(dto.getDepth()));
        return wrapper;
    }

    @Override
    public void create(MetaCategoryClosureCreateDTO dto) {
        MetaCategoryClosure metaCategoryClosure = BeanCopyUtils.copy(dto, MetaCategoryClosure.class);
        save(metaCategoryClosure);
    }

    @Override
    public void update(MetaCategoryClosureUpdateDTO dto) {
        MetaCategoryClosure metaCategoryClosure = BeanCopyUtils.copy(dto, MetaCategoryClosure.class);
        QueryWrapper wrapper;

        saveOrUpdate(metaCategoryClosure);
    }

    @Override
    public PageResult<MetaCategoryClosureVO> page(MetaCategoryClosureListDTO dto) {
        Page<MetaCategoryClosureVO> page = pageAs(PageUtils.getPage(dto), buildQueryWrapper(dto), MetaCategoryClosureVO.class);
        return PageUtils.getPageResult(page);
    }

    @Override
    public List<MetaCategoryClosureVO> list(MetaCategoryClosureListDTO dto) {
        return listAs(buildQueryWrapper(dto), MetaCategoryClosureVO.class);
    }

    @Override
    public void remove(SelectIdsDTO dto) {
        CommonResponseEnum.INVALID_ID.assertTrue(dto.getIds().isEmpty());
        removeByIds(dto.getIds());
    }

    @Override
    public MetaCategoryClosureVO detail(Object id) {
        MetaCategoryClosure metaCategoryClosure = getById((Serializable) id);
        CommonResponseEnum.INVALID_ID.assertNull(metaCategoryClosure);
        return BeanCopyUtils.copy(metaCategoryClosure, MetaCategoryClosureVO.class);
    }

    @SneakyThrows
    @Override
    public void importExcel(ImportExcelDTO dto) {
        ExcelResult<MetaCategoryClosureImportDTO> excelResult = ExcelUtils.importExcel(dto.getFile().getInputStream(), MetaCategoryClosureImportDTO.class, true);
        List<MetaCategoryClosureImportDTO> list = excelResult.getList();
        List<String> errorList = excelResult.getErrorList();
        String analysis = excelResult.getAnalysis();
        System.out.println(" analysis : " + analysis);
        System.out.println(" isCover : " + dto.getIsCover());
    }

    @SneakyThrows
    @Override
    public void exportExcel(MetaCategoryClosureListDTO dto, HttpServletResponse response) {
        List<MetaCategoryClosureVO> list = list(dto);
        String fileName = "分类祖籍关系表模板";
        OutputStream os = FileUtils.getOutputStream(response, fileName + ".xlsx");
        ExcelUtils.exportExcel(list, "分类祖籍关系表", MetaCategoryClosureVO.class, os);
    }

    @Override
    public List<MetaCategoryClosure> ancestorsPath(Long nodeId) {
        QueryWrapper wrapper = QueryWrapper.create().eq(MetaCategoryClosure::getDescendantId, nodeId)
                                           .ne(MetaCategoryClosure::getAncestorId, (nodeId))
                                           .gt(MetaCategoryClosure::getDepth, 0);
        return list(wrapper);
    }

    @Override
    public void create(Long nodeId, Long pid) {
        List<MetaCategoryClosure> closures = new ArrayList<>();
        List<MetaCategoryClosure> pathList = ancestorsPath(pid);
        for (MetaCategoryClosure path : pathList) {
            MetaCategoryClosure closure = buildClosure(path.getAncestorId(), nodeId, path.getDepth() + 1);
            closures.add(closure);
        }
        // self
        MetaCategoryClosure self = buildClosure(nodeId, nodeId, 0);
        // parent
        MetaCategoryClosure parent = buildClosure(pid, nodeId, 1);
        closures.add(self);
        closures.add(parent);
        saveBatch(closures);
    }

    @Override
    public void remove(Long nodeId) {
        MetaCategoryClosure one = getById(nodeId);
        CommonResponseEnum.INVALID_ID.assertNull(one);
        List<MetaCategoryClosure> desList = descendants(nodeId);
        for (MetaCategoryClosure closure : desList) {
            QueryWrapper removeWrapper = QueryWrapper.create().eq(MetaCategoryClosure::getAncestorId, closure.getAncestorId())
                                                     .eq(MetaCategoryClosure::getDescendantId,
                                                             closure.getDescendantId());
            remove(removeWrapper);
        }
        removeById(nodeId);
    }

    @Override
    public List<Long> descendants(List<Long> ancestorIds) {
        if (ancestorIds.isEmpty()) {
            return new ArrayList<>();
        }
        QueryWrapper wrapper = QueryWrapper.create()
                                           .select(QueryMethods.distinct(MetaCategoryClosure::getDescendantId))
                                           .in(MetaCategoryClosure::getAncestorId, ancestorIds);
        return listAs(wrapper, Long.class);
    }

    @Override
    public void move(Long nodeId, Long newNodeId) {
        if (Objects.equals(nodeId, newNodeId)) {
            return;
        }
        List<MetaCategoryClosure> closures = this.mapper.selectDetachTree(nodeId);
        for (MetaCategoryClosure closure : closures) {
            QueryWrapper removeWrapper = QueryWrapper.create().eq(MetaCategoryClosure::getAncestorId, closure.getAncestorId())
                                                     .eq(MetaCategoryClosure::getDescendantId,
                                                             closure.getDescendantId());
            remove(removeWrapper);
        }
        this.mapper.graft(nodeId, newNodeId);
    }

    /**
     * 查询所有子孙节点
     *
     * @param nodeId 节点ID
     * @return 子孙节点
     */
    public List<MetaCategoryClosure> descendants(Long nodeId) {
        QueryWrapper wrapper = QueryWrapper.create().eq(MetaCategoryClosure::getAncestorId, nodeId)
                                           .ne(MetaCategoryClosure::getDescendantId, nodeId);
        return list(wrapper);
    }

    private MetaCategoryClosure buildClosure(Long ancestorId, Long descendantId, Integer depth) {
        MetaCategoryClosure closure = new MetaCategoryClosure();
        closure.setAncestorId(ancestorId);
        closure.setDescendantId(descendantId);
        closure.setDepth(depth);
        return closure;
    }
}
package com.glowxq.oj.group.service.impl;

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
import com.glowxq.oj.group.mapper.GroupBindMapper;
import com.glowxq.oj.group.pojo.dto.GroupBindCreateDTO;
import com.glowxq.oj.group.pojo.dto.GroupBindImportDTO;
import com.glowxq.oj.group.pojo.dto.GroupBindListDTO;
import com.glowxq.oj.group.pojo.dto.GroupBindUpdateDTO;
import com.glowxq.oj.group.pojo.po.GroupBind;
import com.glowxq.oj.group.pojo.vo.GroupBindVO;
import com.glowxq.oj.group.service.GroupBindService;
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
 * 班级绑定数据 服务实现类
 * </p>
 *
 * @author glowxq
 * @since 2025-03-28
 */
@Service
@RequiredArgsConstructor
public class GroupBindServiceImpl extends ServiceImpl<GroupBindMapper, GroupBind> implements GroupBindService {
    @Override
    public void create(GroupBindCreateDTO dto){
        GroupBind groupBind = BeanCopyUtils.copy(dto, GroupBind.class);
        save(groupBind);
    }

    @Override
    public void update(GroupBindUpdateDTO dto){
        GroupBind groupBind = BeanCopyUtils.copy(dto, GroupBind.class);
        QueryWrapper wrapper;
        // id有效性校验
        wrapper = QueryWrapper.create()
            .eq(GroupBind::getId, dto.getId());
        CommonResponseEnum.INVALID_ID.assertTrue(count(wrapper) <= 0);

        saveOrUpdate(groupBind);
    }

    @Override
    public PageResult<GroupBindVO> page(GroupBindListDTO dto){
        Page<GroupBindVO> page = pageAs(PageUtils.getPage(dto), buildQueryWrapper(dto), GroupBindVO.class);
        return PageUtils.getPageResult(page);
    }

    @Override
    public List<GroupBindVO> list(GroupBindListDTO dto){
        return listAs(buildQueryWrapper(dto), GroupBindVO.class);
    }

    @Override
    public void remove(SelectIdsDTO dto){
        CommonResponseEnum.INVALID_ID.assertTrue(dto.getIds().isEmpty());
        removeByIds(dto.getIds());
    }

    @Override
    public GroupBindVO detail(Object id){
        GroupBind groupBind = getById((Serializable) id);
        CommonResponseEnum.INVALID_ID.assertNull(groupBind);
        return BeanCopyUtils.copy(groupBind, GroupBindVO.class);
    }

    @SneakyThrows
    @Override
    public void importExcel(ImportExcelDTO dto) {
        ExcelResult<GroupBindImportDTO> excelResult = ExcelUtils.importExcel(dto.getFile().getInputStream(), GroupBindImportDTO.class, true);
        List<GroupBindImportDTO> list = excelResult.getList();
        List<String> errorList = excelResult.getErrorList();
        String analysis = excelResult.getAnalysis();
        System.out.println(" analysis : " + analysis);
        System.out.println(" isCover : " + dto.getIsCover());
    }

    @SneakyThrows
    @Override
    public void exportExcel(GroupBindListDTO dto, HttpServletResponse response) {
        List<GroupBindVO> list = list(dto);
        String fileName = "班级绑定数据模板";
        OutputStream os = FileUtils.getOutputStream(response, fileName + ".xlsx");
        ExcelUtils.exportExcel(list, "班级绑定数据", GroupBindVO.class, os);
    }

    private static QueryWrapper buildQueryWrapper(GroupBindListDTO dto) {
        QueryWrapper wrapper = QueryWrapper.create().from(GroupBind.class);
            wrapper.eq(GroupBind::getGroupId, dto.getGroupId(), Utils.isNotNull(dto.getGroupId()));
            wrapper.eq(GroupBind::getBusinessId, dto.getBusinessId(), Utils.isNotNull(dto.getBusinessId()));
            wrapper.eq(GroupBind::getType, dto.getType(), Utils.isNotNull(dto.getType()));
    return wrapper;
    }
}
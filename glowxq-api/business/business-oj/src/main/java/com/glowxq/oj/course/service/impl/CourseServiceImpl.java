package com.glowxq.oj.course.service.impl;

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
import com.glowxq.oj.common.DataScopeUtils;
import com.glowxq.oj.common.enums.OjTagBind;
import com.glowxq.oj.course.mapper.CourseMapper;
import com.glowxq.oj.course.pojo.dto.CourseCreateDTO;
import com.glowxq.oj.course.pojo.dto.CourseImportDTO;
import com.glowxq.oj.course.pojo.dto.CourseListDTO;
import com.glowxq.oj.course.pojo.dto.CourseUpdateDTO;
import com.glowxq.oj.course.pojo.po.Course;
import com.glowxq.oj.course.pojo.vo.CourseVO;
import com.glowxq.oj.course.service.CourseService;
import com.glowxq.oj.group.pojo.po.Group;
import com.glowxq.oj.group.service.GroupService;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.OutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 课程 服务实现类
 * </p>
 *
 * @author glowxq
 * @since 2025-03-30
 */
@Service
@RequiredArgsConstructor
public class CourseServiceImpl extends ServiceImpl<CourseMapper, Course> implements CourseService {

    private final GroupService groupService;

    private static QueryWrapper buildQueryWrapper(CourseListDTO dto) {
        QueryWrapper wrapper = QueryWrapper.create().from(Course.class);
        wrapper.like(Course::getName, dto.getName(), Utils.isNotNull(dto.getName()));
        wrapper.eq(Course::getContent, dto.getContent(), Utils.isNotNull(dto.getContent()));
        wrapper.eq(Course::getUrl, dto.getUrl(), Utils.isNotNull(dto.getUrl()));
        wrapper.eq(Course::getTeacherUserId, dto.getTeacherUserId(), Utils.isNotNull(dto.getTeacherUserId()));
        wrapper.like(Course::getTeacherName, dto.getTeacherName(), Utils.isNotNull(dto.getTeacherName()));
        wrapper.eq(Course::getTeacherPhone, dto.getTeacherPhone(), Utils.isNotNull(dto.getTeacherPhone()));
        wrapper.eq(Course::getStatus, dto.getStatus(), Utils.isNotNull(dto.getStatus()));
        wrapper.eq(Course::getEnable, dto.getEnable(), Utils.isNotNull(dto.getEnable()));
        wrapper.between(Course::getStartTime,
                dto.getStartTimeStart(),
                dto.getStartTimeEnd(),
                Utils.isNotNull(dto.getStartTimeStart()) && Utils.isNotNull(dto.getStartTimeEnd()));
        wrapper.between(Course::getEndTime,
                dto.getEndTimeStart(),
                dto.getEndTimeEnd(),
                Utils.isNotNull(dto.getEndTimeStart()) && Utils.isNotNull(dto.getEndTimeEnd()));
        wrapper.orderBy(Course::getId).desc();
        return wrapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(CourseCreateDTO dto) {
        Course course = BeanCopyUtils.copy(dto, Course.class);
        save(course);
        groupService.bindGroups(course.getId(), dto.getGroupIds(), OjTagBind.Course);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(CourseUpdateDTO dto) {
        Course course = BeanCopyUtils.copy(dto, Course.class);
        QueryWrapper wrapper;
        // id有效性校验
        wrapper = QueryWrapper.create()
                              .eq(Course::getId, dto.getId());
        CommonResponseEnum.INVALID_ID.assertTrue(count(wrapper) <= 0);

        saveOrUpdate(course);

        groupService.unBindAll(course.getId(), OjTagBind.Course);
        groupService.bindGroups(course.getId(), dto.getGroupIds(), OjTagBind.Course);
    }

    @Override
    public PageResult<CourseVO> page(CourseListDTO dto) {
        List<Long> groupIds = dto.getGroupIds();
        List<Long> businessIds = groupService.listBusinessIdByGroupIds(groupIds, OjTagBind.Course);
        QueryWrapper queryWrapper = buildQueryWrapper(dto);
        queryWrapper.in(Course::getId, businessIds, CollectionUtils.isNotEmpty(businessIds));
        Page<CourseVO> page = pageAs(PageUtils.getPage(dto), queryWrapper, CourseVO.class);
        List<CourseVO> records = page.getRecords();
        List<Long> courseGroupIds = records.stream().map(CourseVO::getId).toList();
        Map<Long, List<Group>> courseBindGroup = groupService.mapByBusinessIds(courseGroupIds, OjTagBind.Course);
        records.forEach(item -> {
            item.setGroups(courseBindGroup.get(item.getId()));
        });
        page.setRecords(records);
        return PageUtils.getPageResult(page);
    }

    @Override
    public List<CourseVO> list(CourseListDTO dto) {
        return listAs(buildQueryWrapper(dto), CourseVO.class);
    }

    @Override
    public void remove(SelectIdsDTO dto) {
        CommonResponseEnum.INVALID_ID.assertTrue(dto.getIds().isEmpty());
        removeByIds(dto.getIds());
        groupService.unBindsAll(dto.getIds(), OjTagBind.Course);
    }

    @Override
    public CourseVO detail(Object id) {
        Course course = getById((Serializable) id);
        CommonResponseEnum.INVALID_ID.assertNull(course);
        List<Group> groups = groupService.listByBusinessId(course.getId(), OjTagBind.Course);
        CourseVO courseVO = BeanCopyUtils.copy(course, CourseVO.class);
        courseVO.setGroups(groups);
        return courseVO;
    }

    @SneakyThrows
    @Override
    public void importExcel(ImportExcelDTO dto) {
        ExcelResult<CourseImportDTO> excelResult = ExcelUtils.importExcel(dto.getFile().getInputStream(), CourseImportDTO.class, true);
        List<CourseImportDTO> list = excelResult.getList();
        List<String> errorList = excelResult.getErrorList();
        String analysis = excelResult.getAnalysis();
        System.out.println(" analysis : " + analysis);
        System.out.println(" isCover : " + dto.getIsCover());
    }

    @SneakyThrows
    @Override
    public void exportExcel(CourseListDTO dto, HttpServletResponse response) {
        List<CourseVO> list = list(dto);
        String fileName = "课程模板";
        OutputStream os = FileUtils.getOutputStream(response, fileName + ".xlsx");
        ExcelUtils.exportExcel(list, "课程", CourseVO.class, os);
    }

    @Override
    public List<Course> listByIds(Collection<? extends Serializable> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return List.of();
        }
        return super.listByIds(ids);
    }
}
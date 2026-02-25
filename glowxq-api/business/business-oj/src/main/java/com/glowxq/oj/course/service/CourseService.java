package com.glowxq.oj.course.service;

import com.glowxq.core.common.entity.ImportExcelDTO;
import com.glowxq.core.common.entity.PageResult;
import com.glowxq.core.common.entity.SelectIdsDTO;
import com.glowxq.oj.course.pojo.dto.CourseCreateDTO;
import com.glowxq.oj.course.pojo.dto.CourseListDTO;
import com.glowxq.oj.course.pojo.dto.CourseUpdateDTO;
import com.glowxq.oj.course.pojo.po.Course;
import com.glowxq.oj.course.pojo.vo.CourseVO;
import com.mybatisflex.core.service.IService;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

/**
 * <p>
 * 课程 Service
 * </p>
 *
 * @author glowxq
 * @since 2025-03-30
 */
public interface CourseService extends IService<Course> {

    void create(CourseCreateDTO dto);

    void update(CourseUpdateDTO dto);

    PageResult<CourseVO> page(CourseListDTO dto);

    List<CourseVO> list(CourseListDTO dto);

    void remove(SelectIdsDTO dto);

    CourseVO detail(Object id);

    void importExcel(ImportExcelDTO dto);

    void exportExcel(CourseListDTO dto, HttpServletResponse response);
}
package com.glowxq.system.teacher.service;

import com.glowxq.core.common.entity.ImportExcelDTO;
import com.glowxq.core.common.entity.PageResult;
import com.glowxq.core.common.entity.SelectIdsDTO;
import com.glowxq.system.teacher.pojo.dto.TeacherStatisticsCreateDTO;
import com.glowxq.system.teacher.pojo.dto.TeacherStatisticsListDTO;
import com.glowxq.system.teacher.pojo.dto.TeacherStatisticsUpdateDTO;
import com.glowxq.system.teacher.pojo.po.TeacherStatistics;
import com.glowxq.system.teacher.pojo.vo.TeacherStatisticsVO;
import com.mybatisflex.core.service.IService;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

/**
 * <p>
 * 教师统计总览表 Service
 * </p>
 *
 * @author glowxq
 * @since 2024-02-19
 */
public interface TeacherStatisticsService extends IService<TeacherStatistics> {

    void create(TeacherStatisticsCreateDTO dto);

    void update(TeacherStatisticsUpdateDTO dto);

    PageResult<TeacherStatisticsVO> page(TeacherStatisticsListDTO dto);

    List<TeacherStatisticsVO> list(TeacherStatisticsListDTO dto);

    void remove(SelectIdsDTO dto);

    TeacherStatisticsVO detail(Long id);

    void importExcel(ImportExcelDTO dto);

    void exportExcel(TeacherStatisticsListDTO dto, HttpServletResponse response);

    List<TeacherStatisticsVO.TeacherTypeEnum> remoteSearch(String keyword);
}
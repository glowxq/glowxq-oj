package com.glowxq.oj.course.mapper;

import com.glowxq.oj.course.pojo.po.Course;
import com.mybatisflex.core.BaseMapper;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

/**
* <p>
* 课程 Mapper 接口
* </p>
*
* @author glowxq
* @since 2025-03-30
*/
public interface CourseMapper extends BaseMapper<Course> {

    default List<Course> listByIds(List<Long> courseIds){
        if (CollectionUtils.isEmpty(courseIds)){
            return List.of();
        }
        return this.selectListByIds(courseIds);
    }
}
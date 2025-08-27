package com.glowxq.oj.course.pojo.dto;

import com.glowxq.core.common.entity.PageQuery;
import com.glowxq.core.common.entity.base.BaseDTO;
import com.glowxq.core.util.BeanCopyUtils;
import com.glowxq.oj.course.pojo.po.Course;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * Course查询DTO
 * </p>
 *
 * @author glowxq
 * @since 2025-03-30
 */
@Data
@Schema(description = "Course查询DTO")
public class CourseListDTO extends PageQuery implements BaseDTO {

    @Schema(description = "课程名")
    private String name;

    @Schema(description = "课程内容")
    private String content;

    @Schema(description = "课程连接")
    private String url;

    @Schema(description = "老师用户id")
    private Long teacherUserId;

    @Schema(description = "老师姓名")
    private String teacherName;

    @Schema(description = "老师电话")
    private String teacherPhone;

    @Schema(description = "课程状态")
    private String status;

    @Schema(description = "启用状态")
    private Boolean enable;

    @Schema(description = "上课时间开始")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTimeStart;

    @Schema(description = "上课时间结束")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTimeEnd;

    @Schema(description = "下课时间开始")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTimeStart;

    @Schema(description = "下课时间结束")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTimeEnd;



    @Override
    public Course buildEntity() {
        return BeanCopyUtils.copy(this, Course.class);
    }
}
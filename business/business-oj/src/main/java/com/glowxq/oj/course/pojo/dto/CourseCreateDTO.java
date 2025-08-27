package com.glowxq.oj.course.pojo.dto;

import com.glowxq.core.common.entity.base.BaseDTO;
import com.glowxq.core.util.BeanCopyUtils;
import com.glowxq.oj.course.pojo.po.Course;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * Course添加DTO
 * </p>
 *
 * @author glowxq
 * @since 2025-03-30
 */
@Data
@Schema(description = "Course添加DTO")
public class CourseCreateDTO implements BaseDTO {

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

    @Schema(description = "上课时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    @Schema(description = "下课时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;



    @Override
    public Course buildEntity() {
        return BeanCopyUtils.copy(this, Course.class);
    }
}
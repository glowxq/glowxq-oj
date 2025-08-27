package com.glowxq.oj.course.pojo.vo;

import cn.idev.excel.annotation.ExcelIgnore;
import cn.idev.excel.annotation.ExcelProperty;
import com.glowxq.core.common.entity.base.BaseVO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * Course返回vo
 * </p>
 *
 * @author glowxq
 * @since 2025-03-30
 */
@Data
@Schema(description = "Course返回vo")
public class CourseVO implements BaseVO{

    @ExcelIgnore
    @Schema(description =  "")
    private Long id;

    @ExcelProperty(value = "课程名")
    @Schema(description =  "课程名")
    private String name;

    @ExcelProperty(value = "课程内容")
    @Schema(description =  "课程内容")
    private String content;

    @ExcelProperty(value = "课程连接")
    @Schema(description =  "课程连接")
    private String url;

    @ExcelProperty(value = "老师用户id")
    @Schema(description =  "老师用户id")
    private Long teacherUserId;

    @ExcelProperty(value = "老师姓名")
    @Schema(description =  "老师姓名")
    private String teacherName;

    @ExcelProperty(value = "老师电话")
    @Schema(description =  "老师电话")
    private String teacherPhone;

    @ExcelProperty(value = "课程状态")
    @Schema(description =  "课程状态")
    private String status;

    @ExcelProperty(value = "启用状态")
    @Schema(description =  "启用状态")
    private Boolean enable;

    @ExcelProperty(value = "上课时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description =  "上课时间")
    private LocalDateTime startTime;

    @ExcelProperty(value = "下课时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description =  "下课时间")
    private LocalDateTime endTime;



}
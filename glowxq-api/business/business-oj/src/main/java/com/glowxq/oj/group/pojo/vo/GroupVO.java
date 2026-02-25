package com.glowxq.oj.group.pojo.vo;

import cn.idev.excel.annotation.ExcelIgnore;
import cn.idev.excel.annotation.ExcelProperty;
import com.glowxq.core.common.entity.base.BaseVO;
import com.glowxq.oj.course.pojo.po.Course;
import com.glowxq.oj.problem.pojo.po.Problem;
import com.glowxq.oj.topic.pojo.po.Topic;
import com.glowxq.oj.user.pojo.po.UserInfo;
import com.glowxq.system.meta.pojo.po.MetaTag;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * <p>
 * Group返回vo
 * </p>
 *
 * @author glowxq
 * @since 2025-03-27
 */
@Data
@Schema(description = "Group返回vo")
public class GroupVO implements BaseVO {

    @ExcelIgnore
    @Schema(description = "")
    private Long id;

    @ExcelProperty(value = "班级名")
    @Schema(description = "班级名")
    private String name;

    @ExcelProperty(value = "班级代码")
    @Schema(description = "班级代码")
    private String code;

    @ExcelProperty(value = "负责人ID")
    @Schema(description = "负责人ID")
    private String principalUserId;

    @ExcelProperty(value = "负责人姓名")
    @Schema(description = "负责人姓名")
    private String principalName;

    @ExcelProperty(value = "班级描述")
    @Schema(description = "班级描述")
    private String description;

    @ExcelProperty(value = "班级颜色")
    @Schema(description = "班级颜色")
    private String color;

    @ExcelProperty(value = "字体颜色")
    @Schema(description = "字体颜色")
    private String textColor;

    @ExcelProperty(value = "启用")
    @Schema(description = "启用")
    private Boolean enable;

    @Schema(description = "标签列表")
    private List<MetaTag> tagList;

    @Schema(description = "课程列表")
    private List<Course> courseList;

    @Schema(description = "主题列表")
    private List<Topic> topicList;

    @Schema(description = "题目列表")
    private List<Problem> problemsList;

    @Schema(description = "用户列表")
    private List<UserInfo> userList;
}
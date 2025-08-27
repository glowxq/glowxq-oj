package com.glowxq.oj.topic.pojo.dto;

import cn.idev.excel.annotation.ExcelProperty;
import com.glowxq.core.common.entity.base.BaseDTO;
import com.glowxq.core.util.BeanCopyUtils;
import com.glowxq.oj.topic.pojo.po.Topic;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
/**
 * <p>
 * Topic导入DTO
 * </p>
 *
 * @author glowxq
 * @since 2025-04-12
 */
@Data
@Schema(description = "Topic导入DTO")
public class TopicImportDTO implements BaseDTO {

    @ExcelProperty(value = "主题名")
    @Schema(description =  "主题名")
    private String name;

    @ExcelProperty(value = "主题代码")
    @Schema(description =  "主题代码")
    private String code;

    @ExcelProperty(value = "负责人ID")
    @Schema(description =  "负责人ID")
    private Long principalUserId;

    @ExcelProperty(value = "负责人姓名")
    @Schema(description =  "负责人姓名")
    private String principalName;

    @ExcelProperty(value = "主题密码(密码不为空,则打开主题时必须输入正确密码)")
    @Schema(description =  "主题密码(密码不为空,则打开主题时必须输入正确密码)")
    private String password;

    @ExcelProperty(value = "主题描述")
    @Schema(description =  "主题描述")
    private String description;

    @ExcelProperty(value = "时间范围策略")
    @Schema(description =  "时间范围策略")
    private String timeRangeType;

    @ExcelProperty(value = "主题类型")
    @Schema(description =  "主题类型")
    private String topicType;

    @ExcelProperty(value = "主题测评类型 ACM/OI")
    @Schema(description =  "主题测评类型 ACM/OI")
    private Integer topicJudgeType;

    @ExcelProperty(value = "提前封榜(时) -1为全程封榜 0为不封榜")
    @Schema(description =  "提前封榜(时) -1为全程封榜 0为不封榜")
    private Integer sealedTime;

    @ExcelProperty(value = "时间限制(分) 超时自动提交")
    @Schema(description =  "时间限制(分) 超时自动提交")
    private Integer timeLimit;

    @ExcelProperty(value = "罚时(分)")
    @Schema(description =  "罚时(分)")
    private Integer punishmentTime;

    @ExcelProperty(value = "OI得分类型 最近得分/最高得分")
    @Schema(description =  "OI得分类型 最近得分/最高得分")
    private String oiScoreType;

    @ExcelProperty(value = "主题颜色")
    @Schema(description =  "主题颜色")
    private String color;

    @ExcelProperty(value = "公共主题")
    @Schema(description =  "公共主题")
    private Boolean common;

    @ExcelProperty(value = "启用")
    @Schema(description =  "启用")
    private Boolean enable;

    @Schema(description =  "开始时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    @Schema(description =  "截止时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    @Override
    public Topic buildEntity() {
        return BeanCopyUtils.copy(this, Topic.class);
    }
}
package com.glowxq.oj.topic.pojo.vo;

import cn.idev.excel.annotation.ExcelIgnore;
import cn.idev.excel.annotation.ExcelProperty;
import com.glowxq.core.common.entity.base.BaseVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * <p>
 * TopicInfo返回vo
 * </p>
 *
 * @author glowxq
 * @since 2025-04-12
 */
@Data
@Schema(description = "TopicInfo返回vo")
public class TopicInfoVO implements BaseVO{

    @ExcelIgnore
    @Schema(description =  "")
    private Long id;

    @ExcelProperty(value = "主题ID")
    @Schema(description =  "主题ID")
    private Long topicId;

    @ExcelProperty(value = "用户ID")
    @Schema(description =  "用户ID")
    private Long userId;

    @ExcelProperty(value = "头像")
    @Schema(description =  "头像")
    private String avatar;

    @ExcelProperty(value = "姓名")
    @Schema(description =  "姓名")
    private String name;

    @ExcelProperty(value = "昵称")
    @Schema(description =  "昵称")
    private String nickName;

    @ExcelProperty(value = "总得分")
    @Schema(description =  "总得分")
    private Integer score;

    @ExcelProperty(value = "总AC数量")
    @Schema(description =  "总AC数量")
    private Integer acNum;

    @ExcelProperty(value = "提交次数")
    @Schema(description =  "提交次数")
    private Integer submitNum;

    @ExcelProperty(value = "总做题用时(分)")
    @Schema(description =  "总做题用时(分)")
    private Integer useTime;

    @ExcelProperty(value = "总罚时(分)")
    @Schema(description =  "总罚时(分)")
    private Integer punishmentTime;

    @ExcelProperty(value = "主题状态")
    @Schema(description =  "主题状态")
    private String status;

    @ExcelProperty(value = "开始时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description =  "开始时间")
    private LocalDateTime startTime;

    @ExcelProperty(value = "完成时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description =  "完成时间")
    private LocalDateTime endTime;

    @ExcelProperty(value = "自动提交")
    @Schema(description =  "自动提交")
    private Boolean autoSubmit;

}
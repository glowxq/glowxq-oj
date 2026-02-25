package com.glowxq.oj.topic.pojo.dto;

import cn.idev.excel.annotation.ExcelProperty;
import com.glowxq.core.common.entity.base.BaseDTO;
import com.glowxq.core.util.BeanCopyUtils;
import com.glowxq.oj.topic.pojo.po.TopicSubmit;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * <p>
 * TopicSubmit导入DTO
 * </p>
 *
 * @author glowxq
 * @since 2025-04-12
 */
@Data
@Schema(description = "TopicSubmit导入DTO")
public class TopicSubmitImportDTO implements BaseDTO {

    @ExcelProperty(value = "主题ID")
    @Schema(description =  "主题ID")
    private Long topicId;

    @ExcelProperty(value = "题目ID")
    @Schema(description =  "题目ID")
    private Long problemId;

    @ExcelProperty(value = "题目Key")
    @Schema(description =  "题目Key")
    private String problemKey;

    @ExcelProperty(value = "题目标题ID")
    @Schema(description =  "题目标题ID")
    private String problemTitle;

    @ExcelProperty(value = "题目类型")
    @Schema(description =  "题目类型")
    private String problemType;

    @ExcelProperty(value = "主题测评类型 ACM/OI")
    @Schema(description =  "主题测评类型 ACM/OI")
    private Integer topicJudgeType;

    @ExcelProperty(value = "用户ID")
    @Schema(description =  "用户ID")
    private Long userId;

    @ExcelProperty(value = "姓名")
    @Schema(description =  "姓名")
    private String name;

    @ExcelProperty(value = "昵称")
    @Schema(description =  "昵称")
    private String nickName;

    @ExcelProperty(value = "测评状态")
    @Schema(description =  "测评状态")
    private Integer judgeStatus;

    @ExcelProperty(value = "主题分数")
    @Schema(description =  "主题分数")
    private Integer score;

    @ExcelProperty(value = "做题用时(分)")
    @Schema(description =  "做题用时(分)")
    private Integer useTime;

    @ExcelProperty(value = "罚时(分)")
    @Schema(description =  "罚时(分)")
    private Integer punishmentTime;

    @Override
    public TopicSubmit buildEntity() {
        return BeanCopyUtils.copy(this, TopicSubmit.class);
    }
}
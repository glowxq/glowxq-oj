package com.glowxq.oj.user.pojo.dto;

import cn.idev.excel.annotation.ExcelProperty;
import com.glowxq.core.common.entity.base.BaseDTO;
import com.glowxq.core.util.BeanCopyUtils;
import com.glowxq.oj.user.pojo.po.UserProblem;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * <p>
 * UserProblem导入DTO
 * </p>
 *
 * @author glowxq
 * @since 2025-04-03
 */
@Data
@Schema(description = "UserProblem导入DTO")
public class UserProblemImportDTO implements BaseDTO {

    @ExcelProperty(value = "用户ID")
    @Schema(description =  "用户ID")
    private Long userId;

    @ExcelProperty(value = "题目ID")
    @Schema(description =  "题目ID")
    private Long problemId;

    @ExcelProperty(value = "题目Key")
    @Schema(description =  "题目Key")
    private String problemKey;

    @ExcelProperty(value = "题目标题ID")
    @Schema(description =  "题目标题ID")
    private String problemTitle;

    @ExcelProperty(value = "测评ID")
    @Schema(description =  "测评ID")
    private Long judgeId;

    @ExcelProperty(value = "测评状态")
    @Schema(description =  "测评状态")
    private Integer judgeStatus;

    @ExcelProperty(value = "作业分数")
    @Schema(description =  "作业分数")
    private Integer score;

    @ExcelProperty(value = "AC的代码")
    @Schema(description =  "AC的代码")
    private String code;

    @ExcelProperty(value = "非编程题作答内容")
    @Schema(description =  "非编程题作答内容")
    private String options;

    @ExcelProperty(value = "流程图URL")
    @Schema(description =  "流程图URL")
    private String flowImage;

    @ExcelProperty(value = "题目类型")
    @Schema(description =  "题目类型")
    private String problemType;

    @Override
    public UserProblem buildEntity() {
        return BeanCopyUtils.copy(this, UserProblem.class);
    }
}
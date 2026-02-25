package com.glowxq.oj.problem.pojo.dto;

import com.glowxq.core.common.valid.annotation.InEnum;
import com.glowxq.oj.problem.enums.ProblemSourceType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 题目批量导入DTO
 * 用于处理包含多个压缩包的嵌套压缩包导入
 *
 * @author glowxq
 * @since 2025-03-17
 */
@Data
@Schema(description = "题目批量导入DTO")
public class ProblemBatchImportProgramDTO {

    /**
     * 批量压缩包URL - 包含多个题目压缩包的压缩包
     */
    @NotBlank(message = "批量压缩包URL不能为空")
    @Schema(description = "批量压缩包URL - 包含多个题目压缩包的压缩包", example = "https://example.com/batch-problems.zip")
    private String batchFileUrl;

    /**
     * 题目来源类型
     */
    @InEnum(enumClass = ProblemSourceType.class)
    @NotNull(message = "题目来源必填")
    @Schema(description = "题目来源类型", example = "Hoj")
    private ProblemSourceType problemSourceType;

    /**
     * 是否需要插入不存在的标签
     */
    @NotNull(message = "是否需要插入不存在的标签必填")
    @Schema(description = "是否需要插入不存在的标签", example = "true")
    private Boolean needInsertUnExistTag;
}

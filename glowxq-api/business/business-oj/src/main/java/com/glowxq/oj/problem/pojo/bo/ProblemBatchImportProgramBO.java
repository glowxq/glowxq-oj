package com.glowxq.oj.problem.pojo.bo;

import com.glowxq.core.common.valid.annotation.InEnum;
import com.glowxq.oj.problem.enums.ProblemSourceType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.File;

/**
 * 题目批量导入BO
 * 用于处理包含多个压缩包的嵌套压缩包导入
 *
 * @author glowxq
 * @since 2025-03-17
 */
@Data
@Schema(description = "题目批量导入BO")
public class ProblemBatchImportProgramBO {

    /**
     * 批量压缩包URL - 包含多个题目压缩包的压缩包
     */
    @NotBlank(message = "批量压缩包URL不能为空")
    @Schema(description = "批量压缩包URL - 包含多个题目压缩包的压缩包")
    private String batchFileUrl;

    /**
     * 题目来源类型
     */
    @InEnum(enumClass = ProblemSourceType.class)
    @NotNull(message = "题目来源必填")
    @Schema(description = "题目来源类型")
    private ProblemSourceType problemSourceType;

    /**
     * 是否需要插入不存在的标签
     */
    @NotNull(message = "是否需要插入不存在的标签必填")
    @Schema(description = "是否需要插入不存在的标签")
    private Boolean needInsertUnExistTag;

    /**
     * 批量压缩包的解压目录
     */
    @Schema(description = "批量压缩包的解压目录")
    private File batchUnZipDir;
}

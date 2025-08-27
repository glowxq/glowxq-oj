package com.glowxq.oj.problem.handler.upload.batch;

import com.glowxq.oj.problem.pojo.bo.ProblemBatchImportProgramBO;

import java.util.List;

/**
 * 题目批量上传接口
 * 用于处理包含多个压缩包的嵌套压缩包导入
 *
 * @author glowxq
 * @since 2025-03-17
 */
public interface ProblemBatchUploadInterface {

    /**
     * 批量导入题目
     * 处理包含多个题目压缩包的嵌套压缩包
     *
     * @param problemBatchImportProgramBO 批量导入参数
     * @return 成功导入的题目标题列表
     */
    List<String> batchUpload(ProblemBatchImportProgramBO problemBatchImportProgramBO);
}

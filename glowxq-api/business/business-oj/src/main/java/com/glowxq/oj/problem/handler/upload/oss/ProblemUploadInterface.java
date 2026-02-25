package com.glowxq.oj.problem.handler.upload.oss;

import com.glowxq.oj.problem.pojo.bo.ProblemImportProgramBO;

import java.util.List;

public interface ProblemUploadInterface {
    /**
     * 从OSS导入题目
     *
     * @param problemImportProgramBO
     * @return
     */
    List<String> upload(ProblemImportProgramBO problemImportProgramBO);
}

package com.glowxq.oj.problem.pojo.vo;

import com.glowxq.core.common.entity.base.BaseVO;
import lombok.Data;

import java.util.List;

/**
 * 测试用例上传结果封装类
 */
@Data
public class TestCaseUploadResult implements BaseVO {

    /**
     * 测试用例文件列表（已按自然顺序排序）
     */
    private List<TestCaseInfo> fileList;

    /**
     * 测试用例文件存储目录路径
     */
    private String fileListDir;

    public TestCaseUploadResult(List<TestCaseInfo> fileList, String fileListDir) {
        this.fileList = fileList;
        this.fileListDir = fileListDir;
    }
}

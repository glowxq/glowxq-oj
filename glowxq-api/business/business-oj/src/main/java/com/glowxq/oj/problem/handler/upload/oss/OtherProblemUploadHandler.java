package com.glowxq.oj.problem.handler.upload.oss;

import com.glowxq.core.common.exception.common.BusinessException;
import com.glowxq.oj.problem.pojo.bo.ProblemImportProgramBO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OtherProblemUploadHandler extends BaseProblemUploadHandler{
    @Override
    protected void validateFile(File tempFile, ProblemImportProgramBO dto) throws BusinessException {

    }

    @Override
    protected List<String> processUpload(File tempFile, ProblemImportProgramBO problemImportProgramBO) {

        return new ArrayList<>();
    }
}

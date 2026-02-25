package com.glowxq.oj.submit;

import com.glowxq.oj.judge.pojo.po.Judge;
import com.glowxq.oj.submit.compile.CompileSubmitFactory;
import com.glowxq.oj.submit.compile.CompileSubmitInterface;
import com.glowxq.oj.submit.compile.bo.CompileSubmitDTO;
import com.glowxq.oj.submit.normal.NormalSubmitFactory;
import com.glowxq.oj.submit.normal.NormalSubmitInterface;
import com.glowxq.oj.submit.normal.dto.GlobalNormalSubmitDTO;
import com.glowxq.oj.submit.test.TestSubmitDTO;
import com.glowxq.oj.submit.test.TestSubmitInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author glowxq
 * @version 1.0
 * @date 2025/3/20
 */

@Slf4j
@Component
@RequiredArgsConstructor
public class SubmitInterfaceImpl implements SubmitInterface {

    private final NormalSubmitFactory normalSubmitFactory;

    private final TestSubmitInterface testSubmitInterface;

    private final CompileSubmitFactory compileSubmitFactory;

    @Override
    public Judge submitNormal(GlobalNormalSubmitDTO judgeDto) {
        NormalSubmitInterface submitHandler = normalSubmitFactory.getSubmitHandler(judgeDto);
        return submitHandler.submit(judgeDto);
    }

    @Override
    public void submitCompile(CompileSubmitDTO judgeDto) {
        CompileSubmitInterface compileSubmitHandler = compileSubmitFactory.getCompileSubmitHandler(judgeDto);
        compileSubmitHandler.submit(judgeDto);
    }

    @Override
    public void submitTest(TestSubmitDTO judgeDto) {
        testSubmitInterface.submitTest(judgeDto);
    }
}

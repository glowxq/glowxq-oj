package com.glowxq.oj.submit;

import com.glowxq.oj.judge.pojo.po.Judge;
import com.glowxq.oj.submit.compile.bo.CompileSubmitDTO;
import com.glowxq.oj.submit.normal.dto.GlobalNormalSubmitDTO;
import com.glowxq.oj.submit.test.TestSubmitDTO;

/**
 * @author glowxq
 * @version 1.0
 * @date 2025/3/19
 */
public interface SubmitInterface {

    Judge submitNormal(GlobalNormalSubmitDTO judgeDto);

    void submitCompile(CompileSubmitDTO judgeDto);

    void submitTest(TestSubmitDTO judgeDto);
}

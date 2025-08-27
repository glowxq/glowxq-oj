package com.glowxq.oj.submit.normal;

import com.glowxq.oj.judge.pojo.po.Judge;
import com.glowxq.oj.submit.normal.dto.GlobalNormalSubmitDTO;

/**
 * @author glowxq
 * @version 1.0
 * @date 2025/3/19
 */
public interface NormalSubmitInterface {

    Judge submit(GlobalNormalSubmitDTO judgeDto);
}

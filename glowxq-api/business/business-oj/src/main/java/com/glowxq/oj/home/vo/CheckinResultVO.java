package com.glowxq.oj.home.vo;

import com.glowxq.core.common.entity.base.BaseVO;
import lombok.Data;

/**
 * @author glowxq
 * @version 1.0
 * @date 2025/4/10
 */
@Data
public class CheckinResultVO implements BaseVO {

    // 本次获得积分
    private Integer signDayIntegral;

    // 总积分
    private Integer totalIntegral;

    // 连续签到天数
    private Integer continuousSignDay;

    // 今天是否已经签到
    private Boolean todayChecked;
}

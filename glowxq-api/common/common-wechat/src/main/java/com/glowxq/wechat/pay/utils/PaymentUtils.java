package com.glowxq.wechat.pay.utils;

import com.glowxq.wechat.pay.constants.WechatPayConstants;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * 数学utils
 *
 * @author glowxq
 * @version 1.0
 * @date 2023/10/13
 */
public class PaymentUtils {

    /**
     * 将分转为元的维度
     *
     * @param amount 金额
     * @return {@link BigDecimal}
     */
    public static BigDecimal formatFightsToAmount(Integer amount) {
        if (Objects.isNull(amount) || amount == 0) {
            return BigDecimal.ZERO;
        }
        return new BigDecimal(String.valueOf(amount)).divide(WechatPayConstants.HUNDRED, 2, RoundingMode.FLOOR);
    }

    /**
     * 格式金额 成分的维度
     *
     * @param amount 金额
     * @return int
     */
    public static int formatAmountToFights(BigDecimal amount) {
        if (Objects.isNull(amount) || BigDecimal.ZERO.equals(amount)) {
            return 0;
        }
        return amount.multiply(WechatPayConstants.HUNDRED).intValue();
    }
}

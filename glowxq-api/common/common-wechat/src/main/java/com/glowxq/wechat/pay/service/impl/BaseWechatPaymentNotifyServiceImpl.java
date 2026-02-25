package com.glowxq.wechat.pay.service.impl;

import com.github.binarywang.wxpay.bean.notify.WxPayNotifyV3Result;
import com.glowxq.core.common.exception.common.AlertsException;
import com.glowxq.wechat.pay.service.WechatPaymentNotifyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public abstract class BaseWechatPaymentNotifyServiceImpl implements WechatPaymentNotifyService {

    @Override
    public void paymentNotify(WxPayNotifyV3Result wxPayNotifyV3Result) {
        log.info("微信支付回调:{}", wxPayNotifyV3Result);
        try {
            doPaymentNotify(wxPayNotifyV3Result);
        } catch (Exception e) {
            log.error("微信支付回调异常", e);
            throw new AlertsException("微信支付回调异常" + e.getMessage());
        }
    }

    /**
     * 微信支付回调具体执行
     */
    protected abstract void doPaymentNotify(WxPayNotifyV3Result wxPayNotifyV3Result);
}

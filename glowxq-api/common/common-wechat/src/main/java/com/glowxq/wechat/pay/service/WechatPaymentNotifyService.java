package com.glowxq.wechat.pay.service;

import com.github.binarywang.wxpay.bean.notify.WxPayNotifyV3Result;

public interface WechatPaymentNotifyService {

    /**
     * 支付结果通知
     *
     * @param wxPayNotifyV3Result
     */
    void paymentNotify(WxPayNotifyV3Result wxPayNotifyV3Result);
}

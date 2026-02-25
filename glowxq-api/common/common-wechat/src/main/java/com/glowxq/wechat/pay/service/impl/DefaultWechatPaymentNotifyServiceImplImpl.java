package com.glowxq.wechat.pay.service.impl;

import com.github.binarywang.wxpay.bean.notify.WxPayNotifyV3Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class DefaultWechatPaymentNotifyServiceImplImpl extends BaseWechatPaymentNotifyServiceImpl {

    @Override
    protected void doPaymentNotify(WxPayNotifyV3Result wxPayNotifyV3Result) {

    }
}

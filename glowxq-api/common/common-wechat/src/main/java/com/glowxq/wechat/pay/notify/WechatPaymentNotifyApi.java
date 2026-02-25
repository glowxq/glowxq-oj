package com.glowxq.wechat.pay.notify;

import com.github.binarywang.wxpay.bean.notify.SignatureHeader;
import com.github.binarywang.wxpay.bean.notify.WxPayNotifyV3Result;
import com.github.binarywang.wxpay.service.WxPayService;
import com.glowxq.core.common.exception.common.AlertsException;
import com.glowxq.core.common.feishu.utils.FeishuMessageUtils;
import com.glowxq.wechat.pay.pojo.WechatNotifyResult;
import com.glowxq.wechat.pay.service.WechatPaymentNotifyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

/**
 * 任务通知api
 *
 * @author glowxq
 * @date 2023/10/27
 */
@Slf4j
@Component
@RequiredArgsConstructor
@RestController
public class WechatPaymentNotifyApi {

    private final WxPayService wxPayService;

    private final WechatPaymentNotifyService wechatPaymentNotifyService;

    /**
     * 微信jsapi 支付回调
     *
     * @param notifyData 通知数据
     * @param nonce      随机数
     * @param timestamp  时间戳
     * @param serial     串行
     * @param signature  签名
     * @return {@link WechatNotifyResult}
     */
    @PostMapping(value = "/notify/wechat/payment")
    public WechatNotifyResult paymentNotify(@RequestHeader("Wechatpay-Nonce") String nonce,
                                            @RequestHeader("Wechatpay-Timestamp") String timestamp,
                                            @RequestHeader("Wechatpay-Serial") String serial,
                                            @RequestHeader("Wechatpay-Signature") String signature,
                                            @RequestBody String notifyData) {
        String notify = String.format("nonce:%s, timestamp:%s, serial:%s, signature:%s, notifyData:%s", nonce, timestamp, serial, signature, notifyData);
        FeishuMessageUtils.sendBusinessMessage(notify);
        try {
            SignatureHeader signatureHeader = new SignatureHeader(timestamp, nonce, signature, serial);
            WxPayNotifyV3Result wxPayOrderNotifyV3Result = wxPayService.parseOrderNotifyV3Result(notifyData, signatureHeader);
            wechatPaymentNotifyService.paymentNotify(wxPayOrderNotifyV3Result);
            return new WechatNotifyResult("成功", "SUCCESS");
        } catch (Exception e) {
            log.error("微信支付回调异常", e);
            throw new AlertsException("微信支付回调异常" + e.getMessage());
        }
    }
}

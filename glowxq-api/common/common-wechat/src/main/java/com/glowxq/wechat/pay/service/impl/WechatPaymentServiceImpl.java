package com.glowxq.wechat.pay.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.github.binarywang.wxpay.bean.request.WxPayRefundV3Request;
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderV3Request;
import com.github.binarywang.wxpay.bean.result.WxPayUnifiedOrderV3Result;
import com.github.binarywang.wxpay.bean.result.enums.TradeTypeEnum;
import com.github.binarywang.wxpay.config.WxPayConfig;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.service.WxPayService;
import com.github.binarywang.wxpay.v3.util.PemUtils;
import com.glowxq.core.common.exception.common.AlertsException;
import com.glowxq.core.common.exception.common.BusinessException;
import com.glowxq.wechat.pay.config.WechatPaymentConfiguration;
import com.glowxq.wechat.pay.constants.WechatPayConstants;
import com.glowxq.wechat.pay.pojo.WechatPaymentData;
import com.glowxq.wechat.pay.service.WechatPaymentService;
import com.glowxq.wechat.pay.utils.PaymentUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.security.PrivateKey;

/**
 * @author glowxq
 * @version 1.0
 * @date 2023/10/13
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WechatPaymentServiceImpl implements WechatPaymentService {

    private static final TradeTypeEnum TRADE_TYPE_ENUM = TradeTypeEnum.JSAPI;

    private final WxPayService wxPayService;

    private final ResourceLoader resourceLoader;

    private final WechatPaymentConfiguration wechatPaymentConfiguration;

    /**
     * 建立支付
     *
     * @param orderNumber 订单编号
     * @param totalAmount 总金额
     * @param openid      微信openid
     * @param desc        描述信息
     * @return {@link WechatPaymentData} 支付信息结果
     */
    @Override
    public WechatPaymentData payment(String orderNumber, BigDecimal totalAmount, String openid, String desc) {
        WxPayUnifiedOrderV3Result wxUnifiedV3Order = createWxUnifiedV3Order(orderNumber, totalAmount, openid, desc);
        WxPayUnifiedOrderV3Result.JsapiResult jsapiResult = buildPayBeforeInfo(wxUnifiedV3Order);
        return WechatPaymentData.build(jsapiResult);
    }

    @Override
    public void closeReturnPay(String orderNumber, String transactionId) {
        try {
            wxPayService.closeOrderV3(orderNumber);
            WxPayRefundV3Request wxPayRefundV3Request = buildRefundRequest("", "", "", "", BigDecimal.ZERO, BigDecimal.ZERO, "");
            wxPayService.refundV3(wxPayRefundV3Request);
        } catch (WxPayException e) {
            log.error("closeReturnPay 失败:{}", e.getMessage(), e);
            throw new BusinessException(e.getMessage());
        }
    }

    /**
     * 退款
     *
     * @param orderNumber  订单编号
     * @param totalAmount  总金额
     * @param refundAmount 退款金额
     * @return {@link String} 退款编号
     */
    @Override
    public String refund(String orderNumber, BigDecimal totalAmount, BigDecimal refundAmount) {
        String refundNumber = IdUtil.fastSimpleUUID();
        try {
            refund(orderNumber, refundNumber, totalAmount, refundAmount, "用户主动退款");
            return refundNumber;
        } catch (Exception e) {
            log.error("微信支付退款失败", e);
            throw new AlertsException("退款失败" + e.getMessage());
        }
    }

    /**
     * 退款
     *
     * @param orderNumber  订单编号
     * @param refundNumber 退款编号
     * @param totalAmount  总金额
     * @param refundAmount 退款金额
     * @param reason       退款原因
     * @throws WxPayException 微信支付异常
     */
    @Override
    public void refund(String orderNumber, String refundNumber, BigDecimal totalAmount, BigDecimal refundAmount, String reason) throws WxPayException {
        // TODO 配置接口路径
        String notifyUrl = wechatPaymentConfiguration.getNotifyUrl();
        notifyUrl = notifyUrl + "/notify/wechat/refund";
        WxPayRefundV3Request wxPayRefundV3Request = buildRefundRequest(orderNumber,
                refundNumber,
                reason,
                wechatPaymentConfiguration.getNotifyUrl(),
                totalAmount,
                refundAmount,
                WechatPayConstants.Currency);
        wxPayService.refundV3(wxPayRefundV3Request);
    }

    @Override
    public WxPayUnifiedOrderV3Result createWxUnifiedV3Order(String orderNumber, BigDecimal totalAmount, String openid, String desc) {
        desc = StrUtil.maxLength(desc, 7);
        WxPayUnifiedOrderV3Request wxPayUnifiedOrderV3Request = buildOrderRequest(orderNumber, totalAmount, openid, desc, desc);
        try {
            return wxPayService.unifiedOrderV3(TRADE_TYPE_ENUM, wxPayUnifiedOrderV3Request);
        } catch (Exception e) {
            log.error("微信支付-创建订单 unifiedOrderV3失败 message:{}", e.getMessage(), e);
            throw new AlertsException("微信支付-创建订单 unifiedOrderV3失败 message: %s".formatted(e.getMessage()));
        }
    }

    /**
     * 构建支付前置信息
     *
     * @param wxPayUnifiedOrderV3Result 微信支付统一订单V3结果
     * @return {@link WxPayUnifiedOrderV3Result.JsapiResult} Jsapi结果
     */
    @Override
    public WxPayUnifiedOrderV3Result.JsapiResult buildPayBeforeInfo(WxPayUnifiedOrderV3Result wxPayUnifiedOrderV3Result) {

        try (InputStream inputStream = resourceLoader.getResource(wechatPaymentConfiguration.getPrivateKeyPath()).getInputStream()) {
            PrivateKey privateKey = PemUtils.loadPrivateKey(inputStream);
            return wxPayUnifiedOrderV3Result.getPayInfo(TRADE_TYPE_ENUM, wechatPaymentConfiguration.getAppId(), wechatPaymentConfiguration.getMchId(), privateKey);
        } catch (IOException e) {
            log.error("微信支付-获取证书错误 message:{}", e.getMessage(), e);
            throw new AlertsException("微信支付-获取证书错误" + e.getMessage());
        }
    }

    /**
     * 构建订单请求
     *
     * @param orderNumber 订单编号
     * @param totalAmount 总金额
     * @param openid      微信openid
     * @param desc        描述信息
     * @param tag         标签
     * @return {@link WxPayUnifiedOrderV3Request} 微信支付统一订单V3请求
     */
    @Override
    public WxPayUnifiedOrderV3Request buildOrderRequest(String orderNumber, BigDecimal totalAmount, String openid, String desc, String tag) {
        WxPayConfig config = wxPayService.getConfig();

        WxPayUnifiedOrderV3Request.Amount amount = new WxPayUnifiedOrderV3Request.Amount();
        amount.setCurrency(WechatPayConstants.Currency);
        amount.setTotal(PaymentUtils.formatAmountToFights(totalAmount));

        WxPayUnifiedOrderV3Request.Payer payer = new WxPayUnifiedOrderV3Request.Payer();
        payer.setOpenid(openid);
        String notifyUrl = config.getNotifyUrl();
        return new WxPayUnifiedOrderV3Request()
                .setAppid(config.getAppId())
                .setMchid(config.getMchId())
                .setNotifyUrl(notifyUrl)
                .setOutTradeNo(orderNumber)
                .setDescription(desc)
                .setGoodsTag(tag)
                .setAmount(amount)
                .setPayer(payer);
    }

    /**
     * 构建退款请求
     *
     * @param orderNumber  订单编号
     * @param refundNumber 退款编号
     * @param reason       退款原因
     * @param notifyUrl    通知URL
     * @param totalAmount  总金额
     * @param refundAmount 退款金额
     * @param currency     货币
     * @return {@link WxPayRefundV3Request} 微信支付退款V3请求
     */
    @Override
    public WxPayRefundV3Request buildRefundRequest(String orderNumber, String refundNumber, String reason, String notifyUrl, BigDecimal totalAmount,
                                                   BigDecimal refundAmount, String currency) {
        WxPayRefundV3Request.Amount amount = new WxPayRefundV3Request.Amount();
        amount.setRefund(PaymentUtils.formatAmountToFights(refundAmount));
        amount.setTotal(PaymentUtils.formatAmountToFights(totalAmount));
        amount.setCurrency(currency);

        return new WxPayRefundV3Request()
                .setOutTradeNo(orderNumber)
                .setOutRefundNo(refundNumber)
                .setReason(reason)
                .setNotifyUrl(notifyUrl)
                .setAmount(amount);
    }
}

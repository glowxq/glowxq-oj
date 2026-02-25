package com.glowxq.wechat.pay.service;

import com.github.binarywang.wxpay.bean.request.WxPayRefundV3Request;
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderV3Request;
import com.github.binarywang.wxpay.bean.result.WxPayUnifiedOrderV3Result;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.glowxq.wechat.pay.pojo.WechatPaymentData;

import java.math.BigDecimal;

/**
 * @author glowxq
 * @version 1.0
 * @date 2023/10/13
 */

public interface WechatPaymentService {

    /**
     * 建立支付
     *
     * @param orderNumber 订单编号
     * @param totalAmount 总金额
     * @param openid      微信openid
     * @param desc        描述信息
     * @return {@link WechatPaymentData} 支付信息结果
     */
    WechatPaymentData payment(String orderNumber, BigDecimal totalAmount, String openid, String desc);

    /**
     * 关闭订单并进行退款
     *
     * @param orderNumber   订单编号
     * @param transactionId 交易编号
     */
    void closeReturnPay(String orderNumber, String transactionId);

    /**
     * 退款
     *
     * @param orderNumber  订单编号
     * @param totalAmount  总金额
     * @param refundAmount 退款金额
     * @return {@link String} 退款编号
     */
    String refund(String orderNumber, BigDecimal totalAmount, BigDecimal refundAmount);

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
    void refund(String orderNumber, String refundNumber, BigDecimal totalAmount, BigDecimal refundAmount, String reason) throws WxPayException;

    /**
     * 创建微信统一V3订单
     *
     * @param orderNumber 订单编号
     * @param totalAmount 总金额
     * @param openid      微信openid
     * @param desc        描述信息
     * @return {@link WxPayUnifiedOrderV3Result} 微信支付统一订单V3结果
     */
    WxPayUnifiedOrderV3Result createWxUnifiedV3Order(String orderNumber, BigDecimal totalAmount, String openid, String desc);

    /**
     * 构建支付前置信息
     *
     * @param wxPayUnifiedOrderV3Result 微信支付统一订单V3结果
     * @return {@link WxPayUnifiedOrderV3Result.JsapiResult} Jsapi结果
     */
    WxPayUnifiedOrderV3Result.JsapiResult buildPayBeforeInfo(WxPayUnifiedOrderV3Result wxPayUnifiedOrderV3Result);

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
    WxPayUnifiedOrderV3Request buildOrderRequest(String orderNumber, BigDecimal totalAmount, String openid, String desc, String tag);

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
    WxPayRefundV3Request buildRefundRequest(String orderNumber,
                                            String refundNumber,
                                            String reason,
                                            String notifyUrl,
                                            BigDecimal totalAmount,
                                            BigDecimal refundAmount,
                                            String currency);
}

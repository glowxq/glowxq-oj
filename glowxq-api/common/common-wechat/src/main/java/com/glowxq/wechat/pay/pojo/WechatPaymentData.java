package com.glowxq.wechat.pay.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.binarywang.wxpay.bean.result.WxPayUnifiedOrderV3Result;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;

/**
 * @author glowxq
 * @version 1.0
 * @date 2023/10/13
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WechatPaymentData implements Serializable {

    /**
     * 应用程序id
     */
    private String appId;

    /**
     * 时间戳
     */
    private String timeStamp;

    /**
     * 随机文本
     */
    private String nonceStr;

    /**
     * package
     */
    @JsonProperty("package")
    private String packageValue;

    /**
     * 签名类型
     */
    private String signType;

    /**
     * 支付签名
     */
    private String paySign;

    /**
     * 构建
     *
     * @param jsapiResult JSAPI 结果
     * @return {@link WechatPaymentData }
     */
    public static WechatPaymentData build(WxPayUnifiedOrderV3Result.JsapiResult jsapiResult) {
        WechatPaymentData wechatPaymentData = new WechatPaymentData();
        BeanUtils.copyProperties(jsapiResult, wechatPaymentData);
        return wechatPaymentData;
    }

    /**
     * 签名字符串
     *
     * @return {@link String}
     */
    private String getSignStr() {
        return String.format("%s\n%s\n%s\n%s\n", this.appId, this.timeStamp, this.nonceStr, this.packageValue);
    }
}

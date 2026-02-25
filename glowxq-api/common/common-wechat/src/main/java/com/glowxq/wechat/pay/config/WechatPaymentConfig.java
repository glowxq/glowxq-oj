package com.glowxq.wechat.pay.config;

import com.github.binarywang.wxpay.config.WxPayConfig;
import com.github.binarywang.wxpay.service.WxPayService;
import com.github.binarywang.wxpay.service.impl.WxPayServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;

/**
 * @author glowxq
 * @version 1.0
 * @date 2024/7/29
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class WechatPaymentConfig {

    private final WechatPaymentConfiguration properties;

    @Bean
    public WxPayService wxPayService() {
        WxPayConfig payConfig = new WxPayConfig();
        payConfig.setAppId(StringUtils.trimToNull(properties.getAppId()));
        payConfig.setMchId(StringUtils.trimToNull(properties.getMchId()));
        payConfig.setMchKey(StringUtils.trimToNull(properties.getMchKey()));
        payConfig.setApiV3Key(StringUtils.trimToNull(properties.getApiV3Key()));
        payConfig.setNotifyUrl(StringUtils.trimToNull(properties.getPayNotifyPath()));
        payConfig.setPrivateCertPath(StringUtils.trimToNull(properties.getPrivateCertPath()));
        payConfig.setPrivateKeyPath(StringUtils.trimToNull(properties.getPrivateKeyPath()));
        // 可以指定是否使用沙箱环境
        payConfig.setUseSandboxEnv(false);
        WxPayService wxPayService = new WxPayServiceImpl();

        HashMap<String, WxPayConfig> map = new HashMap<>();
        map.put(properties.getMchId(), payConfig);
        wxPayService.setMultiConfig(map, properties.getMchId());
        wxPayService.switchover(properties.getMchId(), properties.getAppId());
        return wxPayService;
    }
}

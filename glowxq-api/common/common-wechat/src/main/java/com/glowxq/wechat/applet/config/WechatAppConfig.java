package com.glowxq.wechat.applet.config;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.api.impl.WxMaServiceImpl;
import cn.binarywang.wx.miniapp.config.WxMaConfig;
import cn.binarywang.wx.miniapp.config.impl.WxMaDefaultConfigImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @author glowxq
 * @version 1.0
 * @date 2024/7/29
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class WechatAppConfig {

    private final WechatAppletConfiguration properties;

    @Bean
    public WxMaService wxMaService() {
        WxMaDefaultConfigImpl config = new WxMaDefaultConfigImpl();
        config.setAppid(properties.getAppId());
        config.setSecret(properties.getSecret());
        config.setToken(properties.getToken());
        config.setAesKey(properties.getAesKey());
        config.setMsgDataFormat(properties.getMsgDataFormat());

        WxMaService wxMaService = new WxMaServiceImpl();
        Map<String, WxMaConfig> map = new HashMap<>();
        map.put(properties.getAppId(), config);
        wxMaService.setMultiConfigs(map, properties.getAppId());
        wxMaService.switchoverTo(properties.getAppId());
        return wxMaService;
    }
}



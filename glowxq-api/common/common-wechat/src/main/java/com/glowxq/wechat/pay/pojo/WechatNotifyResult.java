package com.glowxq.wechat.pay.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WechatNotifyResult {

    /**
     * 状态码
     */
    private String code;

    /**
     * 消息内容
     */
    private String message;
}

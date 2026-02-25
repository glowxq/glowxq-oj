package com.glowxq.wechat.applet.pojo;

import lombok.Data;

/**
 * 微信错误信息实体类
 *
 * @author glowxq
 * @since 2024/4/26 11:11
 * @version 1.0
 */
@Data
public class ErrorMessage {

    private Integer errcode;

    private String errmsg;

}

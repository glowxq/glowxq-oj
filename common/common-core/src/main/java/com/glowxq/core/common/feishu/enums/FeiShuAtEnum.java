package com.glowxq.core.common.feishu.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 飞书
 *
 * @author glowxq
 * @Date: 2021/08/18
 * @Description: 获取飞书员工user_id:https://open.feishu.cn/document/uAjLw4CM/ukTMukTMukTM/reference/contact-v3/user/batch_get_id
 * @date 2023/04/28
 */
@Getter
@AllArgsConstructor
public enum FeiShuAtEnum {

    /**
     * WXQ
     */
    WXQ("13667700000", "吴晓强", "ou_3340fc2f1fb86de4bdb6a0140b324a23"),
    ;

    /**
     * 手机号
     */
    private final String phone;

    /**
     * 姓名
     */
    private final String name;

    /**
     * 飞书的at凭证
     */
    private final String userOpenId;
}

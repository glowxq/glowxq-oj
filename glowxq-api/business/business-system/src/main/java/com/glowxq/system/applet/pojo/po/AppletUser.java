package com.glowxq.system.applet.pojo.po;

import com.glowxq.core.common.entity.base.BaseEntity;
import com.glowxq.mysql.EntityChangeListener;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.time.LocalDateTime;

/**
 * <p>
 * 小程序用户
 * </p>
 *
 * @author glowxq
 * @since 2025-04-26
 */
@Data
@Table(value = "applet_user", onInsert = EntityChangeListener.class, onUpdate = EntityChangeListener.class)
@Schema(description = "小程序用户")
public class AppletUser implements BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    @Schema(description = "小程序用户ID")
    private Long id;

    @Schema(description = "关联的系统用户ID")
    private Integer sysUserId;

    @Schema(description = "小程序用户的唯一标识")
    private String openid;

    @Schema(description = "公众号的唯一标识")
    private String unionid;

    @Schema(description = "昵称")
    private String nickname;

    @Schema(description = "真实姓名")
    private String name;

    @Schema(description = "业务code")
    private String code;

    @Schema(description = "绑定code")
    private String bindCode;

    @Schema(description = "链接")
    private String url;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "地址")
    private String address;

    @Schema(description = "账号")
    private String username;

    @Schema(description = "密码")
    private String password;

    @Schema(description = "头像")
    private String avatar;

    @Schema(description = "是否订阅公众号（1是0否）")
    private Boolean subscribe;

    @Schema(description = "状态")
    private Boolean enable;

    @Schema(description = "性别，0-未知 1-男性，2-女性")
    private Integer sex;

    @Schema(description = "删除标识")
    private String delFlag;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "租户ID")
    private String tenantId;
}
package com.glowxq.tenant.business.pojo.po;

import com.glowxq.core.common.entity.base.BaseEntity;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.time.LocalDateTime;

/**
* <p>
* 租户
* </p>
*
* @author glowxq
* @since 2025-06-06
*/
@Data
@Table(value = "tenant")
@Schema(description = "租户")
public class Tenant implements BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    @Schema(description ="主键ID")
    private Long id;

    @Schema(description ="租户id")
    private String tenantId;

    @Schema(description ="租户编码，唯一标识")
    private String tenantCode;

    @Schema(description ="租户名称")
    private String tenantName;

    @Schema(description ="联系人姓名")
    private String contactName;

    @Schema(description ="联系人手机号")
    private String contactPhone;

    @Schema(description ="联系人邮箱")
    private String contactEmail;

    @Schema(description ="显示")
    private Boolean show;

    @Schema(description = "租户密码")
    private String password;

    @Schema(description ="状态：0-禁用，1-正常")
    private Boolean enable;

    @Schema(description ="到期时间")
    private LocalDateTime expiredTime;

    @Schema(description ="最大用户数")
    private Integer maxUserNum;

    @Schema(description ="当前用户数")
    private Integer currentUserNum;

    @Schema(description ="Logo地址")
    private String logoUrl;

    @Schema(description ="租户文本")
    private String text;

    @Schema(description ="系统名称")
    private String systemName;

    @Schema(description ="首页图片地址")
    private String homeImageUrl;

    @Schema(description ="主题色")
    private String themeColor;

    @Schema(description ="配置信息")
    private String config;

    @Schema(description ="自定义域名")
    private String customDomain;

    @Schema(description ="创建时间")
    private LocalDateTime createTime;

    @Schema(description ="更新时间")
    private LocalDateTime updateTime;

    @Schema(description ="是否删除")
    private String delFlag;

    @Schema(description ="创建人ID")
    private Long createId;

    @Schema(description ="更新人ID")
    private Long updateId;

    public void plusCurrentUserNum() {
        this.currentUserNum++;
    }
}
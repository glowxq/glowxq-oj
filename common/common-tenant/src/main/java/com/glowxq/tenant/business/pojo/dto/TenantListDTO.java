package com.glowxq.tenant.business.pojo.dto;

import com.glowxq.core.common.entity.PageQuery;
import com.glowxq.core.common.entity.base.BaseDTO;
import com.glowxq.core.util.BeanCopyUtils;
import com.glowxq.tenant.business.pojo.po.Tenant;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
/**
 * <p>
 * Tenant查询DTO
 * </p>
 *
 * @author glowxq
 * @since 2025-06-06
 */
@Data
@Schema(description = "Tenant查询DTO")
public class TenantListDTO extends PageQuery implements BaseDTO {

    @Schema(description =  "租户id")
    private String tenantId;

    @Schema(description =  "租户编码，唯一标识")
    private String tenantCode;

    @Schema(description =  "租户名称")
    private String tenantName;

    @Schema(description =  "联系人姓名")
    private String contactName;

    @Schema(description =  "联系人手机号")
    private String contactPhone;

    @Schema(description =  "联系人邮箱")
    private String contactEmail;

    @Schema(description =  "显示")
    private Boolean show;

    @Schema(description = "租户密码")
    private String password;

    @Schema(description =  "状态：0-禁用，1-正常")
    private Boolean enable;

    @Schema(description =  "到期时间开始")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime expiredTimeStart;

    @Schema(description =  "到期时间结束")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime expiredTimeEnd;

    @Schema(description =  "最大用户数")
    private Integer maxUserNum;

    @Schema(description =  "当前用户数")
    private Integer currentUserNum;

    @Schema(description =  "Logo地址")
    private String logoUrl;

    @Schema(description =  "租户文本")
    private String text;

    @Schema(description =  "系统名称")
    private String systemName;

    @Schema(description =  "首页图片地址")
    private String homeImageUrl;

    @Schema(description =  "主题色")
    private String themeColor;

    @Schema(description =  "配置信息")
    private String config;

    @Schema(description =  "自定义域名")
    private String customDomain;

    @Override
    public Tenant buildEntity() {
        return BeanCopyUtils.copy(this, Tenant.class);
    }
}
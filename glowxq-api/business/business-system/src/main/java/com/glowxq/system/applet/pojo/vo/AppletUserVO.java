package com.glowxq.system.applet.pojo.vo;

import cn.idev.excel.annotation.ExcelIgnore;
import cn.idev.excel.annotation.ExcelProperty;
import com.glowxq.core.common.entity.base.BaseVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * <p>
 * AppletUser返回vo
 * </p>
 *
 * @author glowxq
 * @since 2025-04-26
 */
@Data
@Schema(description = "AppletUser返回vo")
public class AppletUserVO implements BaseVO{

    @ExcelIgnore
    @Schema(description =  "小程序用户ID")
    private Long id;

    @ExcelProperty(value = "关联的系统用户ID")
    @Schema(description =  "关联的系统用户ID")
    private Integer sysUserId;

    @ExcelProperty(value = "小程序用户的唯一标识")
    @Schema(description =  "小程序用户的唯一标识")
    private String openid;

    @ExcelProperty(value = "公众号的唯一标识")
    @Schema(description =  "公众号的唯一标识")
    private String unionid;

    @ExcelProperty(value = "昵称")
    @Schema(description =  "昵称")
    private String nickname;

    @ExcelProperty(value = "真实姓名")
    @Schema(description =  "真实姓名")
    private String name;

    @ExcelProperty(value = "业务code")
    @Schema(description ="业务code")
    private String code;

    @ExcelProperty(value = "绑定code")
    @Schema(description ="绑定code")
    private String bindCode;

    @ExcelProperty(value = "链接")
    @Schema(description ="链接")
    private String url;

    @ExcelProperty(value = "手机号")
    @Schema(description =  "手机号")
    private String phone;

    @ExcelProperty(value = "地址")
    @Schema(description =  "地址")
    private String address;

    @ExcelProperty(value = "头像")
    @Schema(description =  "头像")
    private String avatar;

    @ExcelProperty(value = "是否订阅公众号（1是0否）")
    @Schema(description =  "是否订阅公众号（1是0否）")
    private Boolean subscribe;

    @ExcelProperty(value = "状态")
    @Schema(description =  "状态")
    private Boolean enable;

    @ExcelProperty(value = "性别，0-未知 1-男性，2-女性")
    @Schema(description =  "性别，0-未知 1-男性，2-女性")
    private Integer sex;

}
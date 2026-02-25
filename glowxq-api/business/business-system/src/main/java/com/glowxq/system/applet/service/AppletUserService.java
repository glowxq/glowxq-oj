package com.glowxq.system.applet.service;

import com.glowxq.core.common.entity.ImportExcelDTO;
import com.glowxq.core.common.entity.LoginUser;
import com.glowxq.core.common.entity.PageResult;
import com.glowxq.core.common.entity.SelectIdsDTO;
import com.glowxq.oss.UploadResult;
import com.glowxq.system.applet.pojo.dto.*;
import com.glowxq.system.applet.pojo.po.AppletUser;
import com.glowxq.system.applet.pojo.vo.AppletUserVO;
import com.glowxq.wechat.applet.pojo.WechatPhoneInfo;
import com.mybatisflex.core.service.IService;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

/**
 * <p>
 * 小程序用户 Service
 * </p>
 *
 * @author glowxq
 * @since 2025-04-26
 */
public interface AppletUserService extends IService<AppletUser> {

    void create(AppletUserCreateDTO dto);

    void update(AppletUserUpdateDTO dto);

    PageResult<AppletUserVO> page(AppletUserListDTO dto);

    List<AppletUserVO> list(AppletUserListDTO dto);

    void remove(SelectIdsDTO dto);

    AppletUserVO detail(Object id);

    void importExcel(ImportExcelDTO dto);

    void exportExcel(AppletUserListDTO dto, HttpServletResponse response);

    LoginUser doLogin(AppletLoginDTO dto);

    LoginUser loginInfo(String openid, String unionid, Boolean autoRegister);

    void register(RegisterDTO registerDTO);

    AppletUser getByBindCode(String bindCode);

    LoginUser passwordLogin(String username, String password);

    WechatPhoneInfo getPhoneInfo(String phoneCode);

    UploadResult buildWechatCodeDTO(BuildWechatCodeDTO buildWechatCodeDTO);
}
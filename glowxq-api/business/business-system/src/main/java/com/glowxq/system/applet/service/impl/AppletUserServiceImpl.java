package com.glowxq.system.applet.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.glowxq.core.common.constant.OssPathConstant;
import com.glowxq.core.common.entity.*;
import com.glowxq.core.common.enums.CommonResponseEnum;
import com.glowxq.core.common.exception.common.BusinessException;
import com.glowxq.core.util.*;
import com.glowxq.excel.core.ExcelResult;
import com.glowxq.excel.utils.ExcelUtils;
import com.glowxq.oss.OssClient;
import com.glowxq.oss.UploadResult;
import com.glowxq.security.core.util.LoginUtils;
import com.glowxq.system.applet.mapper.AppletUserMapper;
import com.glowxq.system.applet.pojo.dto.*;
import com.glowxq.system.applet.pojo.po.AppletUser;
import com.glowxq.system.applet.pojo.vo.AppletUserVO;
import com.glowxq.system.applet.service.AppletUserService;
import com.glowxq.wechat.applet.WechatService;
import com.glowxq.wechat.applet.config.WechatCodeUnLimitConfiguration;
import com.glowxq.wechat.applet.pojo.LoginInfoResult;
import com.glowxq.wechat.applet.pojo.WechatPhoneInfo;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.OutputStream;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 小程序用户 服务实现类
 * </p>
 *
 * @author glowxq
 * @since 2025-04-26
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AppletUserServiceImpl extends ServiceImpl<AppletUserMapper, AppletUser> implements AppletUserService {

    private final OssClient ossClient;

    private final WechatService wechatService;

    private final WechatCodeUnLimitConfiguration wechatCodeUnLimitConfiguration;

    private static QueryWrapper buildQueryWrapper(AppletUserListDTO dto) {
        QueryWrapper wrapper = QueryWrapper.create().from(AppletUser.class);
        wrapper.eq(AppletUser::getSysUserId, dto.getSysUserId(), Utils.isNotNull(dto.getSysUserId()));
        wrapper.eq(AppletUser::getOpenid, dto.getOpenid(), Utils.isNotNull(dto.getOpenid()));
        wrapper.eq(AppletUser::getUnionid, dto.getUnionid(), Utils.isNotNull(dto.getUnionid()));
        wrapper.like(AppletUser::getNickname, dto.getNickname(), Utils.isNotNull(dto.getNickname()));
        wrapper.like(AppletUser::getName, dto.getName(), Utils.isNotNull(dto.getName()));
        wrapper.eq(AppletUser::getPhone, dto.getPhone(), Utils.isNotNull(dto.getPhone()));
        wrapper.eq(AppletUser::getAddress, dto.getAddress(), Utils.isNotNull(dto.getAddress()));
        wrapper.eq(AppletUser::getAvatar, dto.getAvatar(), Utils.isNotNull(dto.getAvatar()));
        wrapper.eq(AppletUser::getSubscribe, dto.getSubscribe(), Utils.isNotNull(dto.getSubscribe()));
        wrapper.eq(AppletUser::getEnable, dto.getEnable(), Utils.isNotNull(dto.getEnable()));
        wrapper.eq(AppletUser::getSex, dto.getSex(), Utils.isNotNull(dto.getSex()));
        wrapper.eq(AppletUser::getCode, dto.getCode(), Utils.isNotNull(dto.getCode()));
        wrapper.eq(AppletUser::getBindCode, dto.getBindCode(), Utils.isNotNull(dto.getBindCode()));
        wrapper.eq(AppletUser::getUrl, dto.getUrl(), Utils.isNotNull(dto.getUrl()));
        return wrapper;
    }

    @Override
    public void create(AppletUserCreateDTO dto) {
        AppletUser appletUser = BeanCopyUtils.copy(dto, AppletUser.class);
        save(appletUser);
    }

    @Override
    public void update(AppletUserUpdateDTO dto) {
        AppletUser appletUser = BeanCopyUtils.copy(dto, AppletUser.class);
        QueryWrapper wrapper;
        // id有效性校验
        wrapper = QueryWrapper.create()
                              .eq(AppletUser::getId, dto.getId());
        CommonResponseEnum.INVALID_ID.assertTrue(count(wrapper) <= 0);

        saveOrUpdate(appletUser);
    }

    @Override
    public PageResult<AppletUserVO> page(AppletUserListDTO dto) {
        Page<AppletUserVO> page = pageAs(PageUtils.getPage(dto), buildQueryWrapper(dto), AppletUserVO.class);
        return PageUtils.getPageResult(page);
    }

    @Override
    public List<AppletUserVO> list(AppletUserListDTO dto) {
        return listAs(buildQueryWrapper(dto), AppletUserVO.class);
    }

    @Override
    public void remove(SelectIdsDTO dto) {
        CommonResponseEnum.INVALID_ID.assertTrue(dto.getIds().isEmpty());
        removeByIds(dto.getIds());
    }

    @Override
    public AppletUserVO detail(Object id) {
        AppletUser appletUser = getById((Serializable) id);
        CommonResponseEnum.INVALID_ID.assertNull(appletUser);
        return BeanCopyUtils.copy(appletUser, AppletUserVO.class);
    }

    @SneakyThrows
    @Override
    public void importExcel(ImportExcelDTO dto) {
        ExcelResult<AppletUserImportDTO> excelResult = ExcelUtils.importExcel(dto.getFile().getInputStream(), AppletUserImportDTO.class, true);
        List<AppletUserImportDTO> list = excelResult.getList();
        List<String> errorList = excelResult.getErrorList();
        String analysis = excelResult.getAnalysis();
        System.out.println(" analysis : " + analysis);
        System.out.println(" isCover : " + dto.getIsCover());
    }

    @SneakyThrows
    @Override
    public void exportExcel(AppletUserListDTO dto, HttpServletResponse response) {
        List<AppletUserVO> list = list(dto);
        String fileName = "小程序用户模板";
        OutputStream os = FileUtils.getOutputStream(response, fileName + ".xlsx");
        ExcelUtils.exportExcel(list, "小程序用户", AppletUserVO.class, os);
    }

    @Override
    public LoginUser doLogin(AppletLoginDTO dto) {
        String accessToken = wechatService.getAccessToken();
        LoginInfoResult loginInfoResult = wechatService.login(dto.getCode(), accessToken);
        log.info(" 小程序登录返回信息：{}", JsonUtils.toJsonString(loginInfoResult));
        // [do something ...] 结合实际业务进行处理
        return null;
    }

    @Override
    public LoginUser loginInfo(String openid, String unionid, Boolean autoRegister) {
        AppletUser appletUser = mapper.getByOpenId(openid);

        if (appletUser == null) {
            // 开启自动注册
            if (autoRegister) {
                // [do something ...] 创建新的微信用户信息
                appletUser = new AppletUser();
                appletUser.setOpenid(openid);
                appletUser.setUnionid(unionid);
                appletUser.setCreateTime(LocalDateTime.now());
                appletUser.setUpdateTime(LocalDateTime.now());
                mapper.insert(appletUser, true);
            }
            // 注册失败
            else {
                throw new BusinessException("用户不存在");
            }
        }
        else {
            // 绑定了sys_user账户
            if (Utils.isNotNull(appletUser.getSysUserId())) {
                // [do something ...]
            }
            else {
                // 未绑定sys_user账户
                // [do something ...]
            }
        }

        LoginUser loginUser = new LoginUser();
        BaseUserInfo userInfo = BeanCopyUtils.copy(appletUser, BaseUserInfo.class);
        loginUser.setUserInfo(userInfo);

        return loginUser;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void register(RegisterDTO registerDTO) {
        AppletUser appletUser = registerDTO.buildEntity();
        appletUser.setCode(RandomUtil.randomStringUpper(6));
        appletUser.setUsername(registerDTO.autoUsername());
        appletUser.setPassword(registerDTO.autoPassword());
        AppletUser user = mapper.getByUsername(appletUser.getUsername());
        if (user != null) {
            throw new BusinessException("手机号已经注册过");
        }
        if (StringUtils.isNotBlank(registerDTO.getBindCode())) {
            AppletUser bindUser = mapper.getByCode(registerDTO.getBindCode());
            if (bindUser == null) {
                throw new BusinessException("绑定码无效");
            }
        }
        File wxaCodeUnlimit = wechatService.createQRcode(
                "code=" + appletUser.getCode(),
                wechatCodeUnLimitConfiguration.getPage(),
                wechatCodeUnLimitConfiguration.getEnvironment());
        String fileKey = "%s%s.png".formatted(OssPathConstant.APPLET_QRCODE, appletUser.getCode());
        UploadResult uploadResult = ossClient.uploadFile(wxaCodeUnlimit, fileKey);
        LoginInfoResult loginInfoResult = wechatService.login(registerDTO.getLoginCode());
        appletUser.setOpenid(loginInfoResult.getOpenid());

        AppletUser openIdUser = mapper.getByOpenId(appletUser.getOpenid());
        if (openIdUser != null) {
            throw new BusinessException("微信已绑定");
        }
        appletUser.setUrl(uploadResult.getUrl());
        this.save(appletUser);
    }

    @Override
    public AppletUser getByBindCode(String bindCode) {
        if (StringUtils.isBlank(bindCode)) {
            return new AppletUser();
        }
        return mapper.getByBindCode(bindCode);
    }

    @Override
    public LoginUser passwordLogin(String username, String password) {
        AppletUser appletUser = mapper.getByUsername(username);
        if (appletUser == null) {
            throw new BusinessException("用户不存在");
        }
        if (!StringUtils.equals(appletUser.getPassword(), password)) {
            throw new BusinessException("密码错误");
        }
        LoginUser loginUser = new LoginUser();
        BaseUserInfo userInfo = BeanCopyUtils.copy(appletUser, BaseUserInfo.class);
        loginUser.setUserInfo(userInfo);
        return loginUser;
    }

    @Override
    public WechatPhoneInfo getPhoneInfo(String phoneCode) {
        return wechatService.getPhoneInfo(phoneCode);
    }

    @Override
    public UploadResult buildWechatCodeDTO(BuildWechatCodeDTO buildWechatCodeDTO) {
        File wxaCodeUnlimit = wechatService.createQRcode(
                buildWechatCodeDTO.getScene(),
                buildWechatCodeDTO.getPage(),
                buildWechatCodeDTO.getEnvironment().getCode());
        String fileKey = "%s%s.png".formatted(OssPathConstant.APPLET_QRCODE, buildWechatCodeDTO.getScene());
        UploadResult uploadResult = ossClient.uploadFile(wxaCodeUnlimit, fileKey);
        Long userId = LoginUtils.getUserId();
        AppletUser appletUser = mapper.selectOneById(userId);
        appletUser.setUrl(uploadResult.getUrl());
        mapper.update(appletUser);
        return uploadResult;
    }
}
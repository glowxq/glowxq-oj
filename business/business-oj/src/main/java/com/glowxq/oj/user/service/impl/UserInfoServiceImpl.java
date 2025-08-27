package com.glowxq.oj.user.service.impl;

import com.glowxq.core.common.entity.ImportExcelDTO;
import com.glowxq.core.common.entity.PageResult;
import com.glowxq.core.common.entity.SelectIdsDTO;
import com.glowxq.core.common.enums.CommonResponseEnum;
import com.glowxq.core.common.enums.DeleteFlag;
import com.glowxq.core.common.exception.common.BusinessException;
import com.glowxq.core.util.*;
import com.glowxq.excel.core.ExcelResult;
import com.glowxq.excel.utils.ExcelUtils;
import com.glowxq.oj.course.pojo.po.Course;

import com.glowxq.oj.user.mapper.UserInfoMapper;
import com.glowxq.oj.user.pojo.dto.*;
import com.glowxq.oj.user.pojo.po.UserInfo;
import com.glowxq.oj.user.pojo.vo.UserInfoVO;
import com.glowxq.oj.user.service.UserInfoService;
import com.glowxq.system.admin.pojo.dto.sysmenu.SysUserRoleDTO;
import com.glowxq.system.admin.pojo.dto.sysuser.UserDeptDTO;
import com.glowxq.system.admin.pojo.po.SysRole;
import com.glowxq.system.admin.pojo.po.SysUser;
import com.glowxq.system.admin.service.SysRoleService;
import com.glowxq.system.admin.service.SysUserService;
import com.glowxq.system.meta.enums.SystemTagBind;
import com.glowxq.system.meta.pojo.po.MetaTag;
import com.glowxq.system.meta.service.MetaTagService;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.OutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 用户信息 服务实现类
 * </p>
 *
 * @author glowxq
 * @since 2025-06-23
 */
@Service
@RequiredArgsConstructor
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements UserInfoService {



    private final MetaTagService metaTagService;

    private final SysUserService sysUserService;

    private final SysRoleService sysRoleService;

    private static QueryWrapper buildQueryWrapper(UserInfoListDTO dto) {
        QueryWrapper wrapper = QueryWrapper.create().from(UserInfo.class);
        wrapper.eq(UserInfo::getUserId, dto.getUserId(), Utils.isNotNull(dto.getUserId()));
        wrapper.like(UserInfo::getName, dto.getName(), Utils.isNotNull(dto.getName()));
        wrapper.like(UserInfo::getNickName, dto.getNickName(), Utils.isNotNull(dto.getNickName()));
        wrapper.eq(UserInfo::getPhone, dto.getPhone(), Utils.isNotNull(dto.getPhone()));
        wrapper.eq(UserInfo::getEmail, dto.getEmail(), Utils.isNotNull(dto.getEmail()));
        wrapper.eq(UserInfo::getAvatar, dto.getAvatar(), Utils.isNotNull(dto.getAvatar()));
        wrapper.eq(UserInfo::getSex, dto.getSex(), Utils.isNotNull(dto.getSex()));
        wrapper.between(UserInfo::getBirthday, dto.getBirthdayStart(), dto.getBirthdayEnd(),
                Utils.isNotNull(dto.getBirthdayStart()) && Utils.isNotNull(dto.getBirthdayEnd()));
        wrapper.eq(UserInfo::getImage, dto.getImage(), Utils.isNotNull(dto.getImage()));
        wrapper.eq(UserInfo::getAcNum, dto.getAcNum(), Utils.isNotNull(dto.getAcNum()));
        wrapper.eq(UserInfo::getIntegral, dto.getIntegral(), Utils.isNotNull(dto.getIntegral()));
        wrapper.eq(UserInfo::getContinuousSignDay, dto.getContinuousSignDay(), Utils.isNotNull(dto.getContinuousSignDay()));
        wrapper.eq(UserInfo::getSubmitNum, dto.getSubmitNum(), Utils.isNotNull(dto.getSubmitNum()));
        wrapper.eq(UserInfo::getTitle, dto.getTitle(), Utils.isNotNull(dto.getTitle()));
        wrapper.eq(UserInfo::getColor, dto.getColor(), Utils.isNotNull(dto.getColor()));
        wrapper.between(UserInfo::getExpirationTime, dto.getExpirationTimeStart(), dto.getExpirationTimeEnd(),
                Utils.isNotNull(dto.getExpirationTimeStart()) && Utils.isNotNull(dto.getExpirationTimeEnd()));
        wrapper.between(UserInfo::getLastLoginTime, dto.getLastLoginTimeStart(), dto.getLastLoginTimeEnd(),
                Utils.isNotNull(dto.getLastLoginTimeStart()) && Utils.isNotNull(dto.getLastLoginTimeEnd()));
        wrapper.eq(UserInfo::getLastLoginIp, dto.getLastLoginIp(), Utils.isNotNull(dto.getLastLoginIp()));
        wrapper.eq(UserInfo::getTenantId, dto.getTenantId(), Utils.isNotNull(dto.getTenantId()));
        if (StringUtils.isNotBlank(dto.getSearchKey())) {
            wrapper.or(qw -> {qw.like(UserInfo::getName, dto.getSearchKey());});
            wrapper.or(qw -> {qw.like(UserInfo::getNickName, dto.getSearchKey());});
            wrapper.or(qw -> {qw.like(UserInfo::getPhone, dto.getSearchKey());});
            wrapper.or(qw -> {qw.like(UserInfo::getEmail, dto.getSearchKey());});
        }
        return wrapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(UserInfoCreateDTO dto) {
        UserInfo userInfo = mapper.getByPhone(dto.getPhone());
        if (userInfo != null) {
            throw new BusinessException("手机号已经存在");
        }
        SysUser sysUser = sysUserService.create(dto.buildSysUserDTO());

        userInfo = BeanCopyUtils.copy(dto, UserInfo.class);
        userInfo.setId(sysUser.getId());
        userInfo.setUserId(sysUser.getId());
        save(userInfo);

        metaTagService.bindTags(userInfo.getUserId(), dto.getTagIds(), SystemTagBind.User);


        sysUserService.bindUserDept(new UserDeptDTO(sysUser.getId(), dto.getDeptId()));
        sysUserService.changeSysUserRole(new SysUserRoleDTO(sysUser.getId(), dto.getRoleIds()));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(UserInfoUpdateDTO dto) {
        UserInfo userInfo = BeanCopyUtils.copy(dto, UserInfo.class);
        QueryWrapper wrapper = QueryWrapper.create().eq(UserInfo::getId, dto.getId());
        CommonResponseEnum.INVALID_ID.assertTrue(count(wrapper) <= 0);

        sysUserService.update(dto.buildSysUserDTO());
        saveOrUpdate(userInfo);

        metaTagService.unBindAll(userInfo.getUserId(), SystemTagBind.User);
        metaTagService.bindTags(userInfo.getUserId(), dto.getTagIds(), SystemTagBind.User);

        sysUserService.bindUserDept(new UserDeptDTO(userInfo.getId(), dto.getDeptId()));
        sysUserService.changeSysUserRole(new SysUserRoleDTO(userInfo.getId(), dto.getRoleIds()));
    }

    @Override
    public PageResult<UserInfoVO> page(UserInfoListDTO dto) {
        QueryWrapper qw = buildQueryWrapper(dto);


        qw.orderBy(UserInfo::getId);
        Page<UserInfoVO> page = pageAs(PageUtils.getPage(dto), qw, UserInfoVO.class);
        return PageUtils.getPageResult(page);
    }

    @Override
    public List<UserInfoVO> list(UserInfoListDTO dto) {
        QueryWrapper qw = buildQueryWrapper(dto);


        if (CollectionUtils.isNotEmpty(dto.getTagIds())) {
            List<Long> userIds = metaTagService.listBusinessIdByTagIds(dto.getTagIds(), SystemTagBind.User);
            qw.in(UserInfo::getUserId, userIds);
        }
        qw.orderBy(UserInfo::getId);
        return listAs(qw, UserInfoVO.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void remove(SelectIdsDTO dto) {
        CommonResponseEnum.INVALID_ID.assertTrue(dto.getIds().isEmpty());
        List<Long> insideIds = dto.getIds().stream().filter(id -> id <= 10).toList();
        if (CollectionUtils.isNotEmpty(insideIds)) {
            throw new BusinessException("内置用户不允许删除");
        }
        removeByIds(dto.getIds());
        sysUserService.remove(dto);
    }

    @Override
    public UserInfoVO detail(Long id) {
        UserInfoVO userInfoVo = mapper.selectOneWithRelationsByIdAs(id, UserInfoVO.class);
        CommonResponseEnum.INVALID_ID.assertNull(userInfoVo);

        List<MetaTag> metaTags = metaTagService.listByBusinessId(userInfoVo.getUserId(), SystemTagBind.User);
        List<SysRole> roles = sysRoleService.listByUserId(userInfoVo.getUserId());
        userInfoVo.setRoles(roles);
        userInfoVo.setGroups(List.of());
        userInfoVo.setTags(metaTags);
        return userInfoVo;
    }

    @SneakyThrows
    @Override
    public void importExcel(ImportExcelDTO dto) {
        ExcelResult<UserInfoImportDTO> excelResult = ExcelUtils.importExcel(dto.getFile().getInputStream(), UserInfoImportDTO.class, true);
        List<UserInfoImportDTO> list = excelResult.getList();
        List<String> errorList = excelResult.getErrorList();
        String analysis = excelResult.getAnalysis();
        System.out.println(" analysis : " + analysis);
        System.out.println(" isCover : " + dto.getIsCover());
    }

    @SneakyThrows
    @Override
    public void exportExcel(UserInfoListDTO dto, HttpServletResponse response) {
        List<UserInfoVO> list = list(dto);
        String fileName = "用户信息模板";
        OutputStream os = FileUtils.getOutputStream(response, fileName + ".xlsx");
        ExcelUtils.exportExcel(list, "用户信息", UserInfoVO.class, os);
    }

    @Override
    public List<UserInfo> acRankingList(RankingDTO rankingDTO) {
        List<Long> userIds = new ArrayList<>();

        return mapper.acRankingList(userIds);
    }

    @Override
    public List<UserInfo> acRankingList() {
        return mapper.acRankingList();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void register(UserInfoRegisterDTO dto) {
        UserInfo userInfo = mapper.getByPhone(dto.getPhone());
        if (userInfo != null) {
            throw new BusinessException("手机号已经存在");
        }
        SysUser sysUser = sysUserService.create(dto.buildSysUserDTO());

        userInfo = BeanCopyUtils.copy(dto, UserInfo.class);
        userInfo.setId(sysUser.getId());
        userInfo.setUserId(sysUser.getId());
        save(userInfo);

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void bind(UserInfoBindDTO dto) {

    }

    @PostConstruct
    @Transactional(rollbackFor = Exception.class)
    public void initUserInfo() {
        if (sysUserService.count() == this.count()) {
            return;
        }
        List<SysUser> sysUserList = sysUserService.list();

        List<UserInfo> userInfoList = sysUserList.stream().map(sysUser -> {
            UserInfo userInfo = new UserInfo();
            userInfo.setId(sysUser.getId());
            userInfo.setUserId(sysUser.getId());
            userInfo.setDeptId(-1L);
            userInfo.setName(StringUtils.defaultIfBlank(sysUser.getName(), ""));
            userInfo.setNickName(StringUtils.defaultIfBlank(sysUser.getNickname(), ""));
            userInfo.setAvatar(StringUtils.defaultIfBlank(sysUser.getLogo(), ""));
            userInfo.setPhone(StringUtils.defaultIfBlank(sysUser.getPhone(), ""));
            userInfo.setEmail(StringUtils.defaultIfBlank(sysUser.getEmail(), ""));
            userInfo.setSex("");
            userInfo.setBirthday(LocalDate.now());
            userInfo.setRemark("");
            userInfo.setCreateTime(LocalDateTime.now());
            userInfo.setUpdateTime(LocalDateTime.now());
            userInfo.setDelFlag(DeleteFlag.F.getCode());
            return userInfo;
        }).toList();
        saveBatch(userInfoList);
    }
}
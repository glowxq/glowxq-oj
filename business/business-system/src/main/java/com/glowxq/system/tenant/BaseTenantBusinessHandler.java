package com.glowxq.system.tenant;

import com.glowxq.system.admin.mapper.SysRoleMapper;
import com.glowxq.system.admin.pojo.dto.sysmenu.SysUserRoleDTO;
import com.glowxq.system.admin.pojo.dto.sysuser.SysUserCreateDTO;
import com.glowxq.system.admin.pojo.po.SysRole;
import com.glowxq.system.admin.pojo.po.SysUser;
import com.glowxq.system.admin.service.SysUserService;
import com.glowxq.system.enums.DictUserStatusEnum;
import com.glowxq.system.enums.DictUserTypeEnum;
import com.glowxq.system.meta.pojo.po.*;
import com.glowxq.system.meta.service.*;
import com.glowxq.tenant.business.handler.TenantBusinessHandler;
import com.glowxq.tenant.business.pojo.po.Tenant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author glowxq
 * @version 1.0
 * @date 2025/6/9
 */
@Slf4j
@Component
@Primary
public abstract class BaseTenantBusinessHandler implements TenantBusinessHandler {

    @Autowired
    protected MetaTagService metaTagService;

    @Autowired
    protected MetaTextService metaTextService;

    @Autowired
    protected MetaImageService metaImageService;

    @Autowired
    protected MetaMenuService metaMenuService;

    @Autowired
    protected MetaCategoryService metaCategoryService;

    @Autowired
    protected SysUserService sysUserService;

    @Autowired
    protected SysRoleMapper sysRoleMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void newTenantBusiness(Tenant tenant) {
        doTenantBusiness(tenant);
    }

    /**
     * 执行具体的租户业务信息
     *
     * @param tenant
     */
    protected void doTenantBusiness(Tenant tenant) {
        createTenantUser(tenant);
        copyTenantMetaData(tenant);
    }

    protected void copyTenantMetaData(Tenant tenant) {
        List<MetaText> textList = metaTextService.list();
        List<MetaText> copyTextList = textList.stream().peek(text -> text.setTenantId(tenant.getTenantId())).toList();
        metaTextService.saveBatch(copyTextList);

        List<MetaTag> tagList = metaTagService.list();
        List<MetaTag> copyTagList = tagList.stream().peek(tag -> tag.setTenantId(tenant.getTenantId())).toList();
        metaTagService.saveBatch(copyTagList);

        List<MetaImage> imageList = metaImageService.list();
        List<MetaImage> copyImageList = imageList.stream().peek(image -> image.setTenantId(tenant.getTenantId())).toList();
        metaImageService.saveBatch(copyImageList);

        List<MetaMenu> menuList = metaMenuService.list();
        List<MetaMenu> copyMenuList = menuList.stream().peek(menu -> menu.setTenantId(tenant.getTenantId())).toList();
        metaMenuService.saveBatch(copyMenuList);

        List<MetaCategory> categoryList = metaCategoryService.list();
        List<MetaCategory> copyCategoryList = categoryList.stream().peek(category -> category.setTenantId(tenant.getTenantId())).toList();
        metaCategoryService.saveBatch(copyCategoryList);
    }

    /**
     * 创建租户的管理员用户
     *
     * @param tenant
     * @return
     */
    protected SysUser createTenantUser(Tenant tenant) {
        SysUserCreateDTO sysUserCreateDTO = new SysUserCreateDTO();
        sysUserCreateDTO.setUsername(tenant.getContactPhone());
        sysUserCreateDTO.setPhone(tenant.getContactPhone());
        sysUserCreateDTO.setName(tenant.getContactName());
        sysUserCreateDTO.setNickname(tenant.getSystemName());
        sysUserCreateDTO.setLogo(tenant.getLogoUrl());
        sysUserCreateDTO.setEmail(tenant.getContactEmail());
        sysUserCreateDTO.setOpenid("");
        sysUserCreateDTO.setAge(0);
        sysUserCreateDTO.setSex(0);
        sysUserCreateDTO.setIdCard("");
        sysUserCreateDTO.setDataScope("");
        sysUserCreateDTO.setAccountStatusCd(DictUserStatusEnum.Normal.getCode());
        sysUserCreateDTO.setUserTagCd(DictUserTypeEnum.Admin.getCode());
        sysUserCreateDTO.setBirthday("");
        sysUserCreateDTO.setDeptId(-1L);
        sysUserCreateDTO.setTenantId(tenant.getTenantId());
        sysUserCreateDTO.setTagIds(Lists.newArrayList());
        return sysUserService.create(sysUserCreateDTO);
    }

    /**
     * 增加租户用户角色
     *
     * @param tenantUser
     * @param roleKey
     */
    protected void addTenantUserRole(SysUser tenantUser, String roleKey) {
        SysRole sysRole = sysRoleMapper.selectByKey(roleKey);
        SysUserRoleDTO userRoleDTO = new SysUserRoleDTO(tenantUser.getId(), sysRole.getId());
        sysUserService.changeSysUserRole(userRoleDTO);
    }
}

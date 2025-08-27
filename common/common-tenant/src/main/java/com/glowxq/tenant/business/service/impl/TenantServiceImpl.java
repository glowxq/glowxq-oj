package com.glowxq.tenant.business.service.impl;

import com.glowxq.core.common.entity.PageResult;
import com.glowxq.core.common.entity.SelectIdsDTO;
import com.glowxq.core.common.enums.CommonResponseEnum;
import com.glowxq.core.common.exception.common.BusinessException;
import com.glowxq.core.util.BeanCopyUtils;
import com.glowxq.core.util.PageUtils;
import com.glowxq.core.util.StringUtils;
import com.glowxq.core.util.Utils;
import com.glowxq.tenant.business.mapper.TenantMapper;
import com.glowxq.tenant.business.pojo.dto.TenantCreateDTO;
import com.glowxq.tenant.business.pojo.dto.TenantListDTO;
import com.glowxq.tenant.business.pojo.dto.TenantUpdateDTO;
import com.glowxq.tenant.business.pojo.po.Tenant;
import com.glowxq.tenant.business.pojo.vo.TenantVO;
import com.glowxq.tenant.business.service.TenantService;
import com.glowxq.tenant.constants.TenantConstants;
import com.glowxq.tenant.utils.TenantUtils;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 租户 服务实现类
 * </p>
 *
 * @author glowxq
 * @since 2025-06-06
 */
@Slf4j
@Service
public class TenantServiceImpl extends ServiceImpl<TenantMapper, Tenant> implements TenantService {

    private static QueryWrapper buildQueryWrapper(TenantListDTO dto) {
        QueryWrapper wrapper = QueryWrapper.create().from(Tenant.class);
        wrapper.eq(Tenant::getTenantId, dto.getTenantId(), Utils.isNotNull(dto.getTenantId()));
        wrapper.eq(Tenant::getTenantCode, dto.getTenantCode(), Utils.isNotNull(dto.getTenantCode()));
        wrapper.like(Tenant::getTenantName, dto.getTenantName(), Utils.isNotNull(dto.getTenantName()));
        wrapper.like(Tenant::getContactName, dto.getContactName(), Utils.isNotNull(dto.getContactName()));
        wrapper.eq(Tenant::getContactPhone, dto.getContactPhone(), Utils.isNotNull(dto.getContactPhone()));
        wrapper.eq(Tenant::getContactEmail, dto.getContactEmail(), Utils.isNotNull(dto.getContactEmail()));
        wrapper.eq(Tenant::getShow, dto.getShow(), Utils.isNotNull(dto.getShow()));
        wrapper.eq(Tenant::getPassword, dto.getPassword(), Utils.isNotNull(dto.getPassword()));
        wrapper.eq(Tenant::getEnable, dto.getEnable(), Utils.isNotNull(dto.getEnable()));
        wrapper.between(Tenant::getExpiredTime,
                dto.getExpiredTimeStart(),
                dto.getExpiredTimeEnd(),
                Utils.isNotNull(dto.getExpiredTimeStart()) && Utils.isNotNull(dto.getExpiredTimeEnd()));
        wrapper.eq(Tenant::getMaxUserNum, dto.getMaxUserNum(), Utils.isNotNull(dto.getMaxUserNum()));
        wrapper.eq(Tenant::getCurrentUserNum, dto.getCurrentUserNum(), Utils.isNotNull(dto.getCurrentUserNum()));
        wrapper.eq(Tenant::getLogoUrl, dto.getLogoUrl(), Utils.isNotNull(dto.getLogoUrl()));
        wrapper.eq(Tenant::getText, dto.getText(), Utils.isNotNull(dto.getText()));
        wrapper.like(Tenant::getSystemName, dto.getSystemName(), Utils.isNotNull(dto.getSystemName()));
        wrapper.eq(Tenant::getHomeImageUrl, dto.getHomeImageUrl(), Utils.isNotNull(dto.getHomeImageUrl()));
        wrapper.eq(Tenant::getThemeColor, dto.getThemeColor(), Utils.isNotNull(dto.getThemeColor()));
        wrapper.eq(Tenant::getConfig, dto.getConfig(), Utils.isNotNull(dto.getConfig()));
        wrapper.eq(Tenant::getCustomDomain, dto.getCustomDomain(), Utils.isNotNull(dto.getCustomDomain()));
        return wrapper;
    }

    @Override
    public Tenant checkTenantThrow(String tenantKey) {
        if (!TenantUtils.isEnable()) {
            return new Tenant();
        }
        // 保存用户信息
        Tenant tenant = mapper.getByTenantKey(tenantKey);
        if (tenant == null) {
            throw new BusinessException("租户不存在");
        }
        LocalDateTime expiredTime = tenant.getExpiredTime();
        if (LocalDateTime.now().isAfter(expiredTime)) {
            throw new BusinessException("租户已经过期，请联系管理员续租");
        }
        if (tenant.getCurrentUserNum() + 1 > tenant.getMaxUserNum()) {
            throw new BusinessException("租户已达最大用户数，请联系管理员扩大容量");
        }
        return tenant;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Tenant create(TenantCreateDTO dto) {
        // 校验唯一索引约束
        checkTenantUniqueConstraints(dto.getTenantId(), dto.getTenantCode(), dto.getCustomDomain(), dto.getContactPhone());

        // 构建并保存租户实体
        Tenant tenant = dto.buildEntity();
        this.save(tenant);
        return tenant;

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(TenantUpdateDTO dto) {
        // 获取原有租户数据
        Tenant existingTenant = getById(dto.getId());
        CommonResponseEnum.INVALID_ID.assertNull(existingTenant);

        // 检查唯一索引约束（排除自身ID）
        checkTenantUniqueConstraintsForUpdate(dto.getId(), dto.getTenantId(), dto.getTenantCode(), dto.getCustomDomain(), dto.getContactPhone());

        // 更新租户数据
        Tenant tenant = BeanCopyUtils.copy(dto, Tenant.class);
        saveOrUpdate(tenant);
    }

    @Override
    public PageResult<TenantVO> page(TenantListDTO dto) {
        Page<TenantVO> page = pageAs(PageUtils.getPage(dto), buildQueryWrapper(dto), TenantVO.class);
        return PageUtils.getPageResult(page);
    }

    @Override
    public List<TenantVO> list(TenantListDTO dto) {
        return listAs(buildQueryWrapper(dto), TenantVO.class);
    }

    @Override
    public void remove(SelectIdsDTO dto) {
        CommonResponseEnum.INVALID_ID.assertTrue(dto.getIds().isEmpty());
        QueryWrapper queryWrapper = QueryWrapper.create();
        queryWrapper.in(Tenant::getId, dto.getIds());
        queryWrapper.ne(Tenant::getTenantId, TenantConstants.ROOT_TENANT_ID);
        mapper.deleteByQuery(queryWrapper);
    }

    @Override
    public TenantVO getByTenantKey(Long id) {
        Tenant tenant = getById(id);
        CommonResponseEnum.INVALID_ID.assertNull(tenant);
        return BeanCopyUtils.copy(tenant, TenantVO.class);
    }

    @Override
    public TenantVO getByTenantKey(String tenantKey) {
        Tenant tenant = mapper.getByTenantKey(tenantKey);
        CommonResponseEnum.NOT_EXISTS.assertNull(tenant);
        return BeanCopyUtils.copy(tenant, TenantVO.class);
    }

    @Override
    public Tenant getByTenantId(String tenantId) {
        return mapper.getByTenantId(tenantId);
    }

    /**
     * 校验租户的唯一性约束（新增时使用）
     * <p>
     * 校验以下唯一索引：
     * - uk_tenant_id: 租户ID唯一
     * - uk_tenant_code: 租户编码唯一
     * - uk_custom_domain: 自定义域名唯一
     * </p>
     *
     * @param tenantId     租户ID
     * @param tenantCode   租户编码
     * @param customDomain 自定义域名
     * @param contactPhone
     */
    private void checkTenantUniqueConstraints(String tenantId, String tenantCode, String customDomain, String contactPhone) {
        // 检查租户ID唯一性
        if (StringUtils.isNotBlank(tenantId)) {
            Tenant existingTenant = mapper.getByTenantId(tenantId);
            CommonResponseEnum.EXISTS.assertNotNull(existingTenant, "租户ID已存在");
        }

        // 检查租户编码唯一性
        if (StringUtils.isNotBlank(tenantCode)) {
            Tenant existingTenant = mapper.getByTenantCode(tenantCode);
            CommonResponseEnum.EXISTS.assertNotNull(existingTenant, "租户编码已存在");
        }

        // 检查自定义域名唯一性
        if (StringUtils.isNotBlank(customDomain)) {
            Tenant existingTenant = mapper.getByCustomDomain(customDomain);
            CommonResponseEnum.EXISTS.assertNotNull(existingTenant, "自定义域名已被使用");
        }
        if (StringUtils.isNotBlank(contactPhone)) {
            Tenant existingTenant = mapper.getByContactPhone(contactPhone);
            CommonResponseEnum.EXISTS.assertNotNull(existingTenant, "手机号已被使用");
        }
    }

    /**
     * 校验租户的唯一性约束（更新时使用）
     * <p>
     * 校验以下唯一索引，排除当前ID：
     * - uk_tenant_id: 租户ID唯一
     * - uk_tenant_code: 租户编码唯一
     * - uk_custom_domain: 自定义域名唯一
     * </p>
     *
     * @param id           当前租户ID（主键）
     * @param tenantId     租户ID
     * @param tenantCode   租户编码
     * @param customDomain 自定义域名
     */
    private void checkTenantUniqueConstraintsForUpdate(Long id, String tenantId, String tenantCode, String customDomain, String contactPhone) {
        // 检查租户ID唯一性
        if (StringUtils.isNotBlank(tenantId)) {
            Tenant existingTenant = mapper.getByTenantId(tenantId);
            if (existingTenant != null && !existingTenant.getId().equals(id)) {
                throw new BusinessException("租户ID已存在");
            }
        }

        // 检查租户编码唯一性
        if (StringUtils.isNotBlank(tenantCode)) {
            Tenant existingTenant = mapper.getByTenantCode(tenantCode);
            if (existingTenant != null && !existingTenant.getId().equals(id)) {
                throw new BusinessException("租户编码已存在");
            }
        }

        // 检查自定义域名唯一性
        if (StringUtils.isNotBlank(customDomain)) {
            Tenant existingTenant = mapper.getByCustomDomain(customDomain);
            if (existingTenant != null && !existingTenant.getId().equals(id)) {
                throw new BusinessException("自定义域名已被使用");
            }
        }

        if (StringUtils.isNotBlank(contactPhone)) {
            Tenant existingTenant = mapper.getByContactPhone(contactPhone);
            if (existingTenant != null && !existingTenant.getId().equals(id)) {
                CommonResponseEnum.EXISTS.assertNotNull(existingTenant, "手机号已被使用");
            }
        }
    }
}
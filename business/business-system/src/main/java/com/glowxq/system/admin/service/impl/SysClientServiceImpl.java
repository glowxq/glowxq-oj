package com.glowxq.system.admin.service.impl;

import com.glowxq.core.common.entity.PageResult;
import com.glowxq.core.common.entity.SelectIdsDTO;
import com.glowxq.core.common.enums.CommonResponseEnum;
import com.glowxq.core.util.BeanCopyUtils;
import com.glowxq.core.util.PageUtils;
import com.glowxq.core.util.StreamUtils;
import com.glowxq.core.util.Utils;
import com.glowxq.security.pojo.ClientVO;
import com.glowxq.security.service.ClientService;
import com.glowxq.system.admin.mapper.SysClientMapper;
import com.glowxq.system.admin.pojo.dto.sysclient.SysClientCreateDTO;
import com.glowxq.system.admin.pojo.dto.sysclient.SysClientListDTO;
import com.glowxq.system.admin.pojo.dto.sysclient.SysClientUpdateDTO;
import com.glowxq.system.admin.pojo.po.SysClient;
import com.glowxq.system.admin.pojo.vo.sysclient.SysClientVO;
import com.glowxq.system.admin.service.SysClientService;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * 系统授权表 服务实现类
 * </p>
 *
 * @author glowxq
 * @since 2024-01-22
 */
@Service
@RequiredArgsConstructor
public class SysClientServiceImpl extends ServiceImpl<SysClientMapper, SysClient> implements SysClientService, ClientService {

    @Override
    public void create(SysClientCreateDTO dto) {
        SysClient sysClient = BeanCopyUtils.copy(dto, SysClient.class);
        String clientId = Utils.generateUUIDs();
        sysClient.setClientId(clientId);
        sysClient.setClientSecret(Utils.generateUUIDs());
        // 唯一性校验
        QueryWrapper wrapper = QueryWrapper.create().eq(SysClient::getClientKey, dto.getClientKey());
        CommonResponseEnum.EXISTS.message("clientKey已存在").assertTrue(count(wrapper) > 0);
        List<String> grantTypeCdList = dto.getGrantTypeCdList();
        formatGrantType(grantTypeCdList, sysClient);
        save(sysClient);
    }

    @Override
    public void update(SysClientUpdateDTO dto) {
        SysClient sysClient = BeanCopyUtils.copy(dto, SysClient.class);
        QueryWrapper wrapper;
        // id有效性校验
        wrapper = QueryWrapper.create().eq(SysClient::getClientId, dto.getClientId());
        CommonResponseEnum.INVALID_ID.assertTrue(count(wrapper) <= 0);
        List<String> grantTypeCdList = dto.getGrantTypeCdList();
        formatGrantType(grantTypeCdList, sysClient);

        saveOrUpdate(sysClient);
    }

    @Override
    public PageResult<SysClientVO> page(SysClientListDTO dto) {
        Page<SysClientVO> page = pageAs(PageUtils.getPage(dto), buildQueryWrapper(dto), SysClientVO.class);
        return PageUtils.getPageResult(page);
    }

    @Override
    public List<SysClientVO> list(SysClientListDTO dto) {
        return listAs(buildQueryWrapper(dto), SysClientVO.class);
    }

    @Override
    public void remove(SelectIdsDTO dto) {
        CommonResponseEnum.INVALID_ID.assertTrue(dto.getIds().isEmpty());
        removeByIds(dto.getIds());
    }

    @Override
    public ClientVO detail(Object id) {
        SysClient sysClient = getById((Serializable) id);
        CommonResponseEnum.INVALID_ID.assertNull(sysClient);
        ClientVO clientVO = BeanCopyUtils.copy(sysClient, ClientVO.class);
        clientVO.setGrantTypeCdList(Arrays.asList(sysClient.getGrantTypeCd().split(",")));
        return clientVO;
    }

    @Override
    public ClientVO getClientByClientId(Object id) {
        return detail(id);
    }

    private static QueryWrapper buildQueryWrapper(SysClientListDTO dto) {
        QueryWrapper wrapper = QueryWrapper.create().from(SysClient.class);
        if (Utils.isNotNull(dto.getClientKey())) {
            wrapper.eq(SysClient::getClientKey, dto.getClientKey());
        }

        if (Utils.isNotNull(dto.getClientSecret())) {
            wrapper.eq(SysClient::getClientSecret, dto.getClientSecret());
        }

        if (Utils.isNotNull(dto.getGrantTypeCd())) {
            wrapper.eq(SysClient::getGrantTypeCd, dto.getGrantTypeCd());
        }

        if (Utils.isNotNull(dto.getDeviceTypeCd())) {
            wrapper.eq(SysClient::getDeviceTypeCd, dto.getDeviceTypeCd());
        }

        if (Utils.isNotNull(dto.getActiveTimeout())) {
            wrapper.eq(SysClient::getActiveTimeout, dto.getActiveTimeout());
        }

        return wrapper;
    }

    private static void formatGrantType(List<String> grantTypeCdList, SysClient sysClient) {
        if (!grantTypeCdList.isEmpty()) {
            String grantTypes = StreamUtils.listToStr(grantTypeCdList);
            sysClient.setGrantTypeCd(grantTypes);
        }
    }

}
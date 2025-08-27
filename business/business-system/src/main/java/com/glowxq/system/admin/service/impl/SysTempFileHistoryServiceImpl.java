package com.glowxq.system.admin.service.impl;

import com.glowxq.core.common.entity.PageResult;
import com.glowxq.core.util.BeanCopyUtils;
import com.glowxq.core.util.PageUtils;
import com.glowxq.system.admin.mapper.SysTempFileHistoryMapper;
import com.glowxq.system.admin.pojo.dto.systempfile.SysTempFileHistoryCreateDTO;
import com.glowxq.system.admin.pojo.dto.systempfile.SysTempFileHistoryListDTO;
import com.glowxq.system.admin.pojo.po.SysTempFileHistory;
import com.glowxq.system.admin.service.SysTempFileHistoryService;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.glowxq.system.admin.pojo.po.table.SysTempFileHistoryTableDef.SYS_TEMP_FILE_HISTORY;

/**
 * <p>
 * 模版文件历史 服务实现类
 * </p>
 *
 * @author glowxq
 * @since 2024-12-05
 */
@Service
@RequiredArgsConstructor
public class SysTempFileHistoryServiceImpl extends ServiceImpl<SysTempFileHistoryMapper, SysTempFileHistory> implements SysTempFileHistoryService {

    @Override
    public void create(SysTempFileHistoryCreateDTO dto) {
        SysTempFileHistory history = BeanCopyUtils.copy(dto, SysTempFileHistory.class);
        save(history);
    }

    @Override
    public PageResult<SysTempFileHistory> historyList(SysTempFileHistoryListDTO dto) {
        QueryWrapper wrapper = QueryWrapper.create().where(SYS_TEMP_FILE_HISTORY.SYS_TEMP_FILE_ID.eq(dto.getSysTempFileId()));
        Page<SysTempFileHistory> page = page(PageUtils.getPage(dto), wrapper);
        return PageUtils.getPageResult(page);
    }

}
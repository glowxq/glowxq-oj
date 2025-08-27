package com.glowxq.system.admin.service.impl;

import com.glowxq.core.common.entity.PageResult;
import com.glowxq.core.common.entity.SelectIdsDTO;
import com.glowxq.core.common.enums.CommonResponseEnum;
import com.glowxq.core.util.BeanCopyUtils;
import com.glowxq.core.util.PageUtils;
import com.glowxq.core.util.Utils;
import com.glowxq.oss.OssClient;
import com.glowxq.oss.UploadResult;
import com.glowxq.system.admin.mapper.SysTempFileMapper;
import com.glowxq.system.admin.pojo.dto.systempfile.SysTempFileCreateDTO;
import com.glowxq.system.admin.pojo.dto.systempfile.SysTempFileHistoryCreateDTO;
import com.glowxq.system.admin.pojo.dto.systempfile.SysTempFileListDTO;
import com.glowxq.system.admin.pojo.dto.systempfile.SysTempFileUpdateDTO;
import com.glowxq.system.admin.pojo.po.SysTempFile;
import com.glowxq.system.admin.pojo.vo.systempfile.SysTempFileInfoVO;
import com.glowxq.system.admin.pojo.vo.systempfile.SysTempFileVO;
import com.glowxq.system.admin.service.SysFileService;
import com.glowxq.system.admin.service.SysTempFileHistoryService;
import com.glowxq.system.admin.service.SysTempFileService;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.util.List;

import static com.glowxq.system.admin.pojo.po.table.SysFileTableDef.SYS_FILE;
import static com.glowxq.system.admin.pojo.po.table.SysTempFileTableDef.SYS_TEMP_FILE;

/**
 * <p>
 * 模版文件表 服务实现类
 * </p>
 *
 * @author glowxq
 * @since 2024-12-05
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysTempFileServiceImpl extends ServiceImpl<SysTempFileMapper, SysTempFile> implements SysTempFileService {

    private final OssClient ossClient;

    private final SysFileService sysFileService;

    private final SysTempFileHistoryService sysTempFileHistoryService;

    @Override
    public void create(SysTempFileCreateDTO dto) {
        SysTempFile sysTempFile = BeanCopyUtils.copy(dto, SysTempFile.class);
        save(sysTempFile);
        SysTempFileHistoryCreateDTO history = BeanCopyUtils.copy(sysTempFile, SysTempFileHistoryCreateDTO.class);
        history.setSysTempFileId(sysTempFile.getId());
        sysTempFileHistoryService.create(history);
    }

    @Override
    public void update(SysTempFileUpdateDTO dto) {
        SysTempFile sysTempFile = BeanCopyUtils.copy(dto, SysTempFile.class);
        QueryWrapper wrapper;
        // id有效性校验
        wrapper = QueryWrapper.create().eq(SysTempFile::getId, dto.getId());
        CommonResponseEnum.INVALID_ID.assertTrue(count(wrapper) <= 0);

        saveOrUpdate(sysTempFile);

        SysTempFileHistoryCreateDTO history = BeanCopyUtils.copy(sysTempFile, SysTempFileHistoryCreateDTO.class);
        history.setSysTempFileId(sysTempFile.getId());
        sysTempFileHistoryService.create(history);
    }

    @Override
    public PageResult<SysTempFileVO> page(SysTempFileListDTO dto) {
        Page<SysTempFileVO> page = pageAs(PageUtils.getPage(dto), buildQueryWrapper(dto), SysTempFileVO.class);
        return PageUtils.getPageResult(page);
    }

    @Override
    public List<SysTempFileVO> list(SysTempFileListDTO dto) {
        return listAs(buildQueryWrapper(dto), SysTempFileVO.class);
    }

    @Override
    public void remove(SelectIdsDTO dto) {
        CommonResponseEnum.INVALID_ID.assertTrue(dto.getIds().isEmpty());
        removeByIds(dto.getIds());
    }

    @Override
    public SysTempFileVO detail(Object id) {
        SysTempFile sysTempFile = getById((Serializable) id);
        CommonResponseEnum.INVALID_ID.assertNull(sysTempFile);
        return BeanCopyUtils.copy(sysTempFile, SysTempFileVO.class);
    }

    @Override
    public UploadResult uploadFile(MultipartFile file) {
        UploadResult uploadResult = null;
        try {
            uploadResult = ossClient.upload(file, "tmp");
            Long fileId = sysFileService.fileLog(uploadResult);
            uploadResult.setFileId(fileId);
        } catch (Exception e) {
            log.error(" sysTempFile oss upload error", e);
            CommonResponseEnum.FILE_UPLOAD_ERROR.assertTrue(true);
        }
        return uploadResult;
    }

    private static QueryWrapper buildQueryWrapper(SysTempFileListDTO dto) {
        QueryWrapper wrapper = QueryWrapper.create().from(SysTempFile.class);
        if (Utils.isNotNull(dto.getTempName())) {
            wrapper.like(SysTempFile::getTempName, dto.getTempName());
        }
        return wrapper;
    }

    @Override
    public SysTempFileInfoVO detailByName(String tempName) {
        QueryWrapper wrapper = QueryWrapper.create().from(SYS_TEMP_FILE).leftJoin(SYS_FILE).on(SYS_TEMP_FILE.SYS_FILE_ID.eq(SYS_FILE.ID))
                .where(SYS_TEMP_FILE.TEMP_NAME.eq(tempName));
        return getOneAs(wrapper, SysTempFileInfoVO.class);
    }

}
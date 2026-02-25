package com.glowxq.system.admin.service.impl;

import com.glowxq.core.common.entity.PageResult;
import com.glowxq.core.common.enums.CommonResponseEnum;
import com.glowxq.core.util.BeanCopyUtils;
import com.glowxq.core.util.PageUtils;
import com.glowxq.core.util.Utils;
import com.glowxq.oss.OssClient;
import com.glowxq.oss.UploadResult;
import com.glowxq.system.admin.mapper.CommonFileMapper;
import com.glowxq.system.admin.pojo.dto.sysfile.SysFileListDTO;
import com.glowxq.system.admin.pojo.po.SysFile;
import com.glowxq.system.admin.pojo.po.table.SysFileTableDef;
import com.glowxq.system.admin.service.SysFileService;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author glowxq
 * @since 2023-08-31
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysFileServiceImpl extends ServiceImpl<CommonFileMapper, SysFile> implements SysFileService {

    private final OssClient ossClient;

    /**
     * 文件列表
     *
     * @param dto
     *            dto
     * @return {@link PageResult}<{@link SysFile}>
     */
    @Override
    public PageResult<SysFile> fileList(SysFileListDTO dto) {
        QueryWrapper wrapper = QueryWrapper.create();
        if (Utils.isNotNull(dto.getFilename())) {
            wrapper.where(SysFileTableDef.SYS_FILE.FILENAME.like(dto.getFilename()));
        }
        wrapper.orderBy(SysFileTableDef.SYS_FILE.CREATE_TIME.desc());
        Page<SysFile> page = page(PageUtils.getPage(dto), wrapper);
        return PageUtils.getPageResult(page);
    }

    /**
     * 上传文件
     *
     * @param file
     *            文件
     * @param dirTag
     *            文件夹标识
     * @return {@link String}
     */
    @Override
    public UploadResult uploadFile(MultipartFile file, String dirTag) {
        UploadResult uploadResult = null;
        try {
            uploadResult = ossClient.upload(file, dirTag);
            Long fileId = fileLog(uploadResult);
            uploadResult.setFileId(fileId);
        } catch (Exception e) {
            log.error(" sysFile oss upload error", e);
            CommonResponseEnum.FILE_UPLOAD_ERROR.assertTrue(true);
        }
        return uploadResult;
    }

    @Override
    public Long fileLog(UploadResult uploadResult) {
        SysFile sysFile = BeanCopyUtils.copy(uploadResult, SysFile.class);
        this.save(sysFile);
        return sysFile.getId();
    }

    @Override
    public String generatePreUrl(String fileName) {
        return ossClient.generatePreUrl(fileName);
    }

    @Override
    @PostConstruct
    public void initBucket() {
        ossClient.initBucket();
    }
}

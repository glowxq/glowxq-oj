package com.glowxq.system.admin.service;

import com.glowxq.core.common.entity.ApiResult;
import com.glowxq.core.common.entity.PageResult;
import com.glowxq.oss.UploadResult;
import com.glowxq.system.admin.pojo.dto.sysfile.SysFileListDTO;
import com.glowxq.system.admin.pojo.po.SysFile;
import com.mybatisflex.core.service.IService;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author glowxq
 * @since 2023-08-31
 */
public interface SysFileService extends IService<SysFile> {

    /**
     * 文件列表
     *
     * @param dto
     *            dto
     * @return {@link List}<{@link SysFile}>
     */
    PageResult<SysFile> fileList(SysFileListDTO dto);

    /**
     * 上传文件
     *
     * @param file
     *            文件
     * @return {@link ApiResult}
     */
    UploadResult uploadFile(MultipartFile file, String dirTag);

    Long fileLog(UploadResult uploadResult);

    String generatePreUrl(String fileName);

    void initBucket();
}

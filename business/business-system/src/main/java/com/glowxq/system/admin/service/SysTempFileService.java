package com.glowxq.system.admin.service;

import com.glowxq.core.common.entity.PageResult;
import com.glowxq.core.common.entity.SelectIdsDTO;
import com.glowxq.oss.UploadResult;
import com.glowxq.system.admin.pojo.dto.systempfile.SysTempFileCreateDTO;
import com.glowxq.system.admin.pojo.dto.systempfile.SysTempFileListDTO;
import com.glowxq.system.admin.pojo.dto.systempfile.SysTempFileUpdateDTO;
import com.glowxq.system.admin.pojo.po.SysTempFile;
import com.glowxq.system.admin.pojo.vo.systempfile.SysTempFileInfoVO;
import com.glowxq.system.admin.pojo.vo.systempfile.SysTempFileVO;
import com.mybatisflex.core.service.IService;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * <p>
 * 模版文件表 Service
 * </p>
 *
 * @author glowxq-admin
 * @since 2024-12-05
 */
public interface SysTempFileService extends IService<SysTempFile> {

    void create(SysTempFileCreateDTO dto);

    void update(SysTempFileUpdateDTO dto);

    PageResult<SysTempFileVO> page(SysTempFileListDTO dto);

    List<SysTempFileVO> list(SysTempFileListDTO dto);

    void remove(SelectIdsDTO dto);

    SysTempFileVO detail(Object id);

    UploadResult uploadFile(MultipartFile file);

    SysTempFileInfoVO detailByName(String tempName);
}
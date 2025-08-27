package com.glowxq.system.meta.service.impl;

import com.glowxq.core.common.entity.ImportExcelDTO;
import com.glowxq.core.common.entity.PageResult;
import com.glowxq.core.common.entity.SelectIdsDTO;
import com.glowxq.core.common.enums.CommonResponseEnum;
import com.glowxq.core.util.BeanCopyUtils;
import com.glowxq.core.util.FileUtils;
import com.glowxq.core.util.PageUtils;
import com.glowxq.core.util.Utils;
import com.glowxq.excel.core.ExcelResult;
import com.glowxq.excel.utils.ExcelUtils;
import com.glowxq.system.meta.enums.ImageBusinessType;
import com.glowxq.system.meta.mapper.MetaImageMapper;
import com.glowxq.system.meta.pojo.dto.MetaImageCreateDTO;
import com.glowxq.system.meta.pojo.dto.MetaImageImportDTO;
import com.glowxq.system.meta.pojo.dto.MetaImageListDTO;
import com.glowxq.system.meta.pojo.dto.MetaImageUpdateDTO;
import com.glowxq.system.meta.pojo.po.MetaImage;
import com.glowxq.system.meta.pojo.vo.MetaImageVO;
import com.glowxq.system.meta.service.MetaImageService;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.io.OutputStream;
import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 图片 服务实现类
 * </p>
 *
 * @author glowxq
 * @since 2025-04-25
 */
@Service
@RequiredArgsConstructor
public class MetaImageServiceImpl extends ServiceImpl<MetaImageMapper, MetaImage> implements MetaImageService {
    @Override
    public void create(MetaImageCreateDTO dto){
        MetaImage metaImage = BeanCopyUtils.copy(dto, MetaImage.class);
        save(metaImage);
    }

    @Override
    public void update(MetaImageUpdateDTO dto){
        MetaImage metaImage = BeanCopyUtils.copy(dto, MetaImage.class);
        QueryWrapper wrapper;
        // id有效性校验
        wrapper = QueryWrapper.create()
            .eq(MetaImage::getId, dto.getId());
        CommonResponseEnum.INVALID_ID.assertTrue(count(wrapper) <= 0);

        saveOrUpdate(metaImage);
    }

    @Override
    public PageResult<MetaImageVO> page(MetaImageListDTO dto){
        Page<MetaImageVO> page = pageAs(PageUtils.getPage(dto), buildQueryWrapper(dto), MetaImageVO.class);
        return PageUtils.getPageResult(page);
    }

    @Override
    public List<MetaImageVO> list(MetaImageListDTO dto){
        return listAs(buildQueryWrapper(dto), MetaImageVO.class);
    }

    @Override
    public void remove(SelectIdsDTO dto){
        CommonResponseEnum.INVALID_ID.assertTrue(dto.getIds().isEmpty());
        removeByIds(dto.getIds());
    }

    @Override
    public MetaImageVO detail(Object id){
        MetaImage metaImage = getById((Serializable) id);
        CommonResponseEnum.INVALID_ID.assertNull(metaImage);
        return BeanCopyUtils.copy(metaImage, MetaImageVO.class);
    }

    @SneakyThrows
    @Override
    public void importExcel(ImportExcelDTO dto) {
        ExcelResult<MetaImageImportDTO> excelResult = ExcelUtils.importExcel(dto.getFile().getInputStream(), MetaImageImportDTO.class, true);
        List<MetaImageImportDTO> list = excelResult.getList();
        List<String> errorList = excelResult.getErrorList();
        String analysis = excelResult.getAnalysis();
        System.out.println(" analysis : " + analysis);
        System.out.println(" isCover : " + dto.getIsCover());
    }

    @SneakyThrows
    @Override
    public void exportExcel(MetaImageListDTO dto, HttpServletResponse response) {
        List<MetaImageVO> list = list(dto);
        String fileName = "图片模板";
        OutputStream os = FileUtils.getOutputStream(response, fileName + ".xlsx");
        ExcelUtils.exportExcel(list, "图片", MetaImageVO.class, os);
    }

    @Override
    public MetaImage getByKey(String key) {
        return mapper.getByKey(key);
    }

    @Override
    public List<MetaImage> listByType(ImageBusinessType imageBusinessType) {
        return mapper.listByType(imageBusinessType);
    }

    private static QueryWrapper buildQueryWrapper(MetaImageListDTO dto) {
    QueryWrapper wrapper = QueryWrapper.create().from(MetaImage.class);
            wrapper.like(MetaImage::getName, dto.getName(), Utils.isNotNull(dto.getName()));
            wrapper.eq(MetaImage::getImageKey, dto.getImageKey(), Utils.isNotNull(dto.getImageKey()));
            wrapper.eq(MetaImage::getBusinessType, dto.getBusinessType(), Utils.isNotNull(dto.getBusinessType()));
            wrapper.eq(MetaImage::getUrl, dto.getUrl(), Utils.isNotNull(dto.getUrl()));
            wrapper.eq(MetaImage::getContent, dto.getContent(), Utils.isNotNull(dto.getContent()));
            wrapper.eq(MetaImage::getSkipUrl, dto.getSkipUrl(), Utils.isNotNull(dto.getSkipUrl()));
            wrapper.eq(MetaImage::getSort, dto.getSort(), Utils.isNotNull(dto.getSort()));
            wrapper.eq(MetaImage::getEnable, dto.getEnable(), Utils.isNotNull(dto.getEnable()));
    return wrapper;
    }
}
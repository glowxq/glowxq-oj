package com.glowxq.system.admin.service.impl;

import com.glowxq.core.common.entity.PageResult;
import com.glowxq.core.common.entity.SelectIdsDTO;
import com.glowxq.core.common.enums.CommonResponseEnum;
import com.glowxq.core.util.BeanCopyUtils;
import com.glowxq.core.util.PageUtils;
import com.glowxq.core.util.SysConfigUtils;
import com.glowxq.core.util.Utils;
import com.glowxq.platform.factory.DictLoaderFactory;
import com.glowxq.redis.RedisCache;
import com.glowxq.system.admin.mapper.SysDictTypeMapper;
import com.glowxq.system.admin.pojo.dto.sysdict.SysDictTypeAddDTO;
import com.glowxq.system.admin.pojo.dto.sysdict.SysDictTypeListDTO;
import com.glowxq.system.admin.pojo.dto.sysdict.SysDictTypeUpDTO;
import com.glowxq.system.admin.pojo.po.SysDictType;
import com.glowxq.system.admin.pojo.vo.sysdict.DictTypeVO;
import com.glowxq.system.admin.service.SysDictTypeService;
import com.mybatisflex.core.logicdelete.LogicDeleteManager;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.core.query.QueryMethods;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static com.glowxq.system.admin.pojo.po.table.SysDictTypeTableDef.SYS_DICT_TYPE;

/**
 * <p>
 * 字典类型 服务实现类
 * </p>
 *
 * @author glowxq
 * @since 2023-08-18
 */
@Service
@RequiredArgsConstructor
public class SysDictTypeServiceImpl extends ServiceImpl<SysDictTypeMapper, SysDictType> implements SysDictTypeService {

    private final DictLoaderFactory dictLoaderFactory;

    private final RedisCache redisCache;

    @Override
    public void create(SysDictTypeAddDTO dto) {
        String confValue = SysConfigUtils.getConfValue("sys.dict.startNo");
        SysDictType sysDictType = BeanCopyUtils.copy(dto, SysDictType.class);
        String type = dto.getType();
        if ("system".equals(type)) { // 系统字典
            AtomicReference<Long> maxId = new AtomicReference<>(0L);
            LogicDeleteManager.execWithoutLogicDelete(() -> {
                Long count = QueryChain.of(SysDictType.class).select(QueryMethods.max(SYS_DICT_TYPE.ID)).from(SYS_DICT_TYPE)
                        .where(SYS_DICT_TYPE.ID.lt(confValue)).oneAs(Long.class);
                maxId.set(count);
            });
            sysDictType.setId(maxId.get() + 1);
        } else {
            AtomicReference<Long> maxId = new AtomicReference<>(0L);
            LogicDeleteManager.execWithoutLogicDelete(() -> {
                Long count = QueryChain.of(SysDictType.class).select(QueryMethods.max(SYS_DICT_TYPE.ID)).from(SYS_DICT_TYPE)
                        .where(SYS_DICT_TYPE.ID.ge(confValue)).oneAs(Long.class);
                maxId.set(count);
            });
            if (Utils.isNotNull(maxId) && Utils.isNotNull(maxId.get())) {
                sysDictType.setId(maxId.get() + 1);
            } else {
                sysDictType.setId(Utils.getLongVal(confValue));
            }
        }
        QueryWrapper wrapper = QueryWrapper.create().where(SYS_DICT_TYPE.TYPE_CODE.eq(dto.getTypeCode()));
        CommonResponseEnum.EXISTS.message("typeCode已存在").assertTrue(count(wrapper) > 0);
        save(sysDictType);
    }

    @Override
    public void update(SysDictTypeUpDTO dto) {
        SysDictType sysDictType = BeanCopyUtils.copy(dto, SysDictType.class);
        sysDictType.setId(dto.getId());
        SysDictType oldDetail = detail(dto.getId());
        // 修改时的重复性效验需要排除本身
        QueryWrapper wrapper = QueryWrapper.create().where(SYS_DICT_TYPE.TYPE_CODE.eq(dto.getTypeCode())).where(SYS_DICT_TYPE.ID.ne(dto.getId()));
        CommonResponseEnum.EXISTS.message("typeCode已存在").assertTrue(count(wrapper) > 0);
        saveOrUpdate(sysDictType);
        redisCache.clearDict(oldDetail.getTypeCode()); // 清除redis缓存
        dictLoaderFactory.getDictByType(sysDictType.getTypeCode()); // 更新缓存
    }

    @Override
    public void remove(SelectIdsDTO dto) {
        CommonResponseEnum.INVALID_ID.assertNull(dto.getIds());
        QueryWrapper wrapper = QueryWrapper.create().select(SYS_DICT_TYPE.TYPE_CODE).where(SYS_DICT_TYPE.ID.in(dto.getIds()));
        List<String> typeCodes = listAs(wrapper, String.class);
        for (String typeCode : typeCodes) {
            redisCache.clearDict(typeCode); // 清除redis缓存
        }
        removeByIds(dto.getIds());
    }

    @Override
    public SysDictType detail(Long id) {
        SysDictType dictType = getById(id);
        CommonResponseEnum.INVALID_ID.assertNull(dictType);
        return dictType;
    }

    @Override
    public PageResult<SysDictType> list(SysDictTypeListDTO dto) {
        QueryWrapper wrapper = QueryWrapper.create();
        if (Utils.isNotNull(dto.getTypeName())) {
            wrapper.where(SYS_DICT_TYPE.TYPE_NAME.like(dto.getTypeName()));
        }
        if (Utils.isNotNull(dto.getTypeCode())) {
            wrapper.where(SYS_DICT_TYPE.TYPE_CODE.like(dto.getTypeCode()));
        }
        wrapper.orderBy(SYS_DICT_TYPE.CREATE_TIME.asc());
        Page<SysDictType> page = page(PageUtils.getPage(dto), wrapper);
        return PageUtils.getPageResult(page);
    }

    @Override
    public List<DictTypeVO> findDictType() {
        QueryWrapper wrapper = QueryWrapper.create().orderBy(SYS_DICT_TYPE.CREATE_TIME.desc());
        return listAs(wrapper, DictTypeVO.class);
    }

    @Override
    public List<DictTypeVO> selectDictTypeOptions() {
        return dictLoaderFactory.getAllDictType();
    }

}

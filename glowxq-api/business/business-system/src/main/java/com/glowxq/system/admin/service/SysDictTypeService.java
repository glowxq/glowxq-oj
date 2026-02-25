package com.glowxq.system.admin.service;

import com.glowxq.core.common.entity.PageResult;
import com.glowxq.core.common.entity.SelectIdsDTO;
import com.glowxq.system.admin.pojo.dto.sysdict.SysDictTypeAddDTO;
import com.glowxq.system.admin.pojo.dto.sysdict.SysDictTypeListDTO;
import com.glowxq.system.admin.pojo.dto.sysdict.SysDictTypeUpDTO;
import com.glowxq.system.admin.pojo.po.SysDictType;
import com.glowxq.system.admin.pojo.vo.sysdict.DictTypeVO;
import com.mybatisflex.core.service.IService;

import java.util.List;

/**
 * <p>
 * 字典类型 服务类
 * </p>
 *
 * @author glowxq
 * @since 2023-08-18
 */
public interface SysDictTypeService extends IService<SysDictType> {

    void create(SysDictTypeAddDTO dto);

    void update(SysDictTypeUpDTO dto);

    void remove(SelectIdsDTO dto);

    SysDictType detail(Long id);

    PageResult<SysDictType> list(SysDictTypeListDTO dto);

    List<DictTypeVO> findDictType();

    List<DictTypeVO> selectDictTypeOptions();
}

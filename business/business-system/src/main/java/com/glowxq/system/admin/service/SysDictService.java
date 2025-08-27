package com.glowxq.system.admin.service;

import com.glowxq.core.common.entity.DictVO;
import com.glowxq.core.common.entity.PageResult;
import com.glowxq.core.common.entity.SelectIdsDTO;
import com.glowxq.system.admin.pojo.dto.sysdict.SysDictCreateDTO;
import com.glowxq.system.admin.pojo.dto.sysdict.SysDictListDTO;
import com.glowxq.system.admin.pojo.dto.sysdict.SysDictUpdateDTO;
import com.glowxq.system.admin.pojo.po.SysDict;
import com.mybatisflex.core.service.IService;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 字典表 服务类
 * </p>
 *
 * @author glowxq
 * @since 2023-08-18
 */
public interface SysDictService extends IService<SysDict> {

    void create(SysDictCreateDTO dto);

    void update(SysDictUpdateDTO dto);

    void remove(SelectIdsDTO dto);

    PageResult<SysDict> list(SysDictListDTO dto);

    Map<String, List<DictVO>> dictList(String typeCode);

    Map<String, List<DictVO>> dictAll();

    List<DictVO> getDictByType(String typeCode);

    String exportDictSql(SelectIdsDTO dto);

    Map<String, List<DictVO>> getDictByCode(List<String> typeCode);
}

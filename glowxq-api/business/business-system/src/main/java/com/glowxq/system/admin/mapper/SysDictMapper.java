package com.glowxq.system.admin.mapper;

import com.glowxq.core.common.entity.DictVO;
import com.glowxq.system.admin.pojo.po.SysDict;
import com.mybatisflex.core.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 字典表 Mapper 接口
 * </p>
 *
 * @author glowxq
 * @since 2023-08-18
 */
public interface SysDictMapper extends BaseMapper<SysDict> {

    List<DictVO> listDict(@Param("typeCode") String typeCode);

}

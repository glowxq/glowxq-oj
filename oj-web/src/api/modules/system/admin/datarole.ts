import http from '@/api';
import { ADMIN_MODULE } from '@/api/helper/prefix';
import type { IPage } from '@/api/interface';
import type { ISysDataRole } from '@/api/interface/system/admin/datarole';
/**
 * 查询列表
 * @param params
 * @returns {*}
 */
export const getSysDataRoleListApi = (params: ISysDataRole.Query) => {
  return http.get<IPage<ISysDataRole.Row>>(ADMIN_MODULE + `/sys-data-role`, params);
};

/**
 * 添加
 * @param params
 * @returns {*}
 */
export const createSysDataRoleApi = (params: ISysDataRole.Form) => {
  return http.post(ADMIN_MODULE + `/sys-data-role`, params);
};

/**
 * 修改
 * @param params
 * @returns {*}
 */
export const updateSysDataRoleApi = (params: ISysDataRole.Form) => {
  return http.put(ADMIN_MODULE + `/sys-data-role`, params);
};

/**
 * 删除
 * @param params
 * @returns {*}
 */
export const removeSysDataRoleApi = (params: { ids: (string | number)[] }) => {
  return http.delete(ADMIN_MODULE + `/sys-data-role`, params);
};

/**
 * 获取详情
 * @param params
 * @returns {*}
 */
export const getSysDataRoleDetailApi = (params: { id: number }) => {
  const { id } = params;
  return http.get<ISysDataRole.Row>(ADMIN_MODULE + `/sys-data-role/${id}`);
};

/**
 * 获取数据角色初始菜单信息
 * @param params
 * @returns {*}
 */
export const getSysDataRoleMenuApi = () => {
  return http.get<ISysDataRole.Meta>(ADMIN_MODULE + `/sys-data-role/menu`);
};

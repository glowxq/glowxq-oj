import http from '@/api';
import { ADMIN_MODULE } from '@/api/helper/prefix';
import type { IPage } from '@/api/interface';
import type { ISysClient } from '@/api/interface/system/admin/client';
/**
 * 查询列表
 * @param params
 * @returns {*}
 */
export const getSysClientListApi = (params: ISysClient.Query) => {
  return http.get<IPage<ISysClient.Row>>(ADMIN_MODULE + `/sys-client`, params);
};

/**
 * 添加
 * @param params
 * @returns {*}
 */
export const createSysClientApi = (params: ISysClient.Form) => {
  return http.post(ADMIN_MODULE + `/sys-client`, params);
};

/**
 * 修改
 * @param params
 * @returns {*}
 */
export const updateSysClientApi = (params: ISysClient.Form) => {
  return http.put(ADMIN_MODULE + `/sys-client`, params);
};

/**
 * 删除
 * @param params
 * @returns {*}
 */
export const removeSysClientApi = (params: { ids: number[] }) => {
  return http.delete(ADMIN_MODULE + `/sys-client`, params);
};

/**
 * 获取详情
 * @param params
 * @returns {*}
 */
export const getSysClientDetailApi = (params: { id: number }) => {
  const { id } = params;
  return http.get<ISysClient.Row>(ADMIN_MODULE + `/sys-client/${id}`);
};

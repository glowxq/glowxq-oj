import http from '@/api';
import { ADMIN_MODULE } from '@/api/helper/prefix';
import type { IConfig } from '@/api/interface/system/admin/config';
import type { IPage } from '@/api/interface';

/**
 * 获取列表
 * @param params
 * @returns {*}
 */
export const getConfigList = (params: IConfig.Query) => {
  return http.get<IPage<IConfig.Info>>(ADMIN_MODULE + `/sys-config`, params);
};

/**
 * 添加
 * @param params
 * @returns {*}
 */
export const addConfig = (params: IConfig.Form) => {
  return http.post(ADMIN_MODULE + `/sys-config`, params);
};

/**
 * 修改
 * @param params
 * @returns {*}
 */
export const editConfig = (params: IConfig.Form) => {
  return http.put(ADMIN_MODULE + `/sys-config`, params);
};

/**
 * 删除
 * @param params
 * @returns {*}
 */
export const deleteConfig = (params: { ids: number[] }) => {
  return http.delete(ADMIN_MODULE + `/sys-config`, params);
};

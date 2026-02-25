import http from '@/api'
import { OJ_MODULE } from '@/api/helper/prefix'
import type { IPage } from '@/api/interface'
import type { ITenant } from '@/api/interface/system/tenant/tenant'

/**
* 查询列表
* @param params
* @returns {*}
*/
export const getTenantListApi = (params: ITenant.Query) => {
  return http.get<IPage<ITenant.Row>>(OJ_MODULE + `/tenant/list`, params)
}

/**
* 获取所有可用租户列表
* @returns {*}
*/
export const getTenantListAllApi = () => {
  return http.get<ITenant.Row[]>(OJ_MODULE + `/client/tenant/list`)
}

/**
* 根据租户Key获取租户信息
* @param tenantKey
* @returns {*}
*/
export const getTenantByTenantKeyApi = (tenantKey: string) => {
  console.log('API调用getTenantByTenantKeyApi，参数:', tenantKey);
  // 确保租户key统一大写处理
  const normalizedKey = tenantKey.toUpperCase();
  console.log('规范化后的租户Key:', normalizedKey);
  return http.get<ITenant.Row>(OJ_MODULE + `/client/tenant/getByTenantKey`, { tenantKey: normalizedKey });
}

/**
* 添加
* @param params
* @returns {*}
*/
export const createTenantApi = (params: ITenant.Form) => {
  return http.post(OJ_MODULE + `/tenant/create`, params)
}

/**
* 修改
* @param params
* @returns {*}
*/
export const updateTenantApi = (params: ITenant.Form) => {
  return http.put(OJ_MODULE + `/tenant/update`, params)
}

/**
* 删除
* @param params
* @returns {*}
*/
export const removeTenantApi = (params: { ids: (string | number)[] }) => {
 return http.delete(OJ_MODULE + `/tenant/remove`, params)
}

/**
* 获取详情
* @param params
* @returns {*}
*/
export const getTenantDetailApi = (params: { id: string | number }) => {
  const { id } = params
  return http.get<ITenant.Row>(OJ_MODULE + `/tenant/detail`, params)
}

/**
* 查询租户功能是否启用
* @returns {*}
*/
export const getTenantEnableStatusApi = () => {
  return http.get<{ enable: boolean }>(OJ_MODULE + `/tenant/enable/status`)
}


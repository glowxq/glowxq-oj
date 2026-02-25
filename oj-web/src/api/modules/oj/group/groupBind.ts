import http from '@/api'
import { OJ_MODULE } from '@/api/helper/prefix'
import type { IPage } from '@/api/interface'
import type { IGroupBind } from '@/api/interface/oj/group/groupBind'
import type { UploadRawFile } from "element-plus/es/components/upload/src/upload";
import type { AxiosRequestConfig } from 'axios';

/**
* 查询列表
* @param params
* @returns {*}
*/
export const getGroupBindListApi = (params: IGroupBind.Query) => {
  return http.get<IPage<IGroupBind.Row>>(OJ_MODULE + `/group-bind`, params)
}

/**
* 添加
* @param params
* @returns {*}
*/
export const createGroupBindApi = (params: IGroupBind.Form) => {
  return http.post(OJ_MODULE + `/group-bind`, params)
}

/**
* 修改
* @param params
* @returns {*}
*/
export const updateGroupBindApi = (params: IGroupBind.Form) => {
  return http.put(OJ_MODULE + `/group-bind`, params)
}

/**
* 删除
* @param params
* @returns {*}
*/
export const removeGroupBindApi = (params: { ids: (string | number)[] }) => {
 return http.delete(OJ_MODULE + `/group-bind`, params)
}

/**
* 获取详情
* @param params
* @returns {*}
*/
export const getGroupBindDetailApi = (params: { id: number }) => {
  const { id } = params
  return http.get<IGroupBind.Row>(OJ_MODULE + `/group-bind/${id}`)
}

/**
* 导入excel
* @param params
*/
export const importGroupBindExcelApi = (params : UploadRawFile, config?: AxiosRequestConfig<{}> | undefined) => {
  return http.upload(OJ_MODULE + `/group-bind/import`, params, config)
}

/**
* 导出excel
* @param params
* @returns {*}
*/
export const exportGroupBindExcelApi  = (params: IGroupBind.Query) => {
  return http.download(OJ_MODULE + `/group-bind/export`, params)
}

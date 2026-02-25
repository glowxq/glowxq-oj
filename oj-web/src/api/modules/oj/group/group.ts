import http from '@/api'
import { OJ_MODULE } from '@/api/helper/prefix'
import type { IPage } from '@/api/interface'
import type { IGroup } from '@/api/interface/oj/group/group'
import type { UploadRawFile } from "element-plus/es/components/upload/src/upload";
import type { AxiosRequestConfig } from 'axios';

/**
* 查询列表
* @param params
* @returns {*}
*/
export const getGroupListApi = (params: IGroup.Query) => {
  return http.get<IPage<IGroup.Row>>(OJ_MODULE + `/group`, params)
}

/**
* 添加
* @param params
* @returns {*}
*/
export const createGroupApi = (params: IGroup.Form) => {
  return http.post(OJ_MODULE + `/group`, params)
}

/**
* 修改
* @param params
* @returns {*}
*/
export const updateGroupApi = (params: IGroup.Form) => {
  return http.put(OJ_MODULE + `/group`, params)
}

/**
* 删除
* @param params
* @returns {*}
*/
export const removeGroupApi = (params: { ids: (string | number)[] }) => {
 return http.delete(OJ_MODULE + `/group`, params)
}

/**
* 获取详情
* @param params
* @returns {*}
*/
export const getGroupDetailApi = (params: { id: number }) => {
  const { id } = params
  return http.get<IGroup.Row>(OJ_MODULE + `/group/${id}`)
}

/**
* 导入excel
* @param params
*/
export const importGroupExcelApi = (params : UploadRawFile, config?: AxiosRequestConfig<{}> | undefined) => {
  return http.upload(OJ_MODULE + `/group/import`, params, config)
}

/**
* 导出excel
* @param params
* @returns {*}
*/
export const exportGroupExcelApi  = (params: IGroup.Query) => {
  return http.download(OJ_MODULE + `/group/export`, params)
}

import http from '@/api'
import { SYSTEM_MODULE } from '@/api/helper/prefix'
import type { IPage } from '@/api/interface'
import type { IMetaTagBind } from '@/api/interface/system/meta/metaTagBind'
import type { UploadRawFile } from "element-plus/es/components/upload/src/upload";
import type { AxiosRequestConfig } from 'axios';

/**
* 查询列表
* @param params
* @returns {*}
*/
export const getMetaTagBindListApi = (params: IMetaTagBind.Query) => {
  return http.get<IPage<IMetaTagBind.Row>>(SYSTEM_MODULE + `/meta-tag-bind/list`, params)
}

/**
* 添加
* @param params
* @returns {*}
*/
export const createMetaTagBindApi = (params: IMetaTagBind.Form) => {
  return http.post(SYSTEM_MODULE + `/meta-tag-bind/create`, params)
}

/**
* 修改
* @param params
* @returns {*}
*/
export const updateMetaTagBindApi = (params: IMetaTagBind.Form) => {
  return http.put(SYSTEM_MODULE + `/meta-tag-bind/update`, params)
}

/**
* 删除
* @param params
* @returns {*}
*/
export const removeMetaTagBindApi = (params: { ids: (string | number)[] }) => {
 return http.delete(SYSTEM_MODULE + `/meta-tag-bind/remove`, params)
}

/**
* 获取详情
* @param params
* @returns {*}
*/
export const getMetaTagBindDetailApi = (params: { id: number }) => {
  const { id } = params
  return http.get<IMetaTagBind.Row>(SYSTEM_MODULE + `/meta-tag-bind/detail`, params)
}

/**
* 导入excel
* @param params
*/
export const importMetaTagBindExcelApi = (params : UploadRawFile, config?: AxiosRequestConfig<{}> | undefined) => {
  return http.upload(SYSTEM_MODULE + `/meta-tag-bind/import`, params, config)
}

/**
* 导出excel
* @param params
* @returns {*}
*/
export const exportMetaTagBindExcelApi  = (params: IMetaTagBind.Query) => {
  return http.download(SYSTEM_MODULE + `/meta-tag-bind/export`, params)
}
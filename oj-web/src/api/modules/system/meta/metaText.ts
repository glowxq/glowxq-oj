import http from '@/api'
import { SYSTEM_MODULE } from '@/api/helper/prefix'
import type { IPage } from '@/api/interface'
import type { IMetaText } from '@/api/interface/system/meta/metaText'
import type { UploadRawFile } from "element-plus/es/components/upload/src/upload";
import type { AxiosRequestConfig } from 'axios';

/**
* 查询列表
* @param params
* @returns {*}
*/
export const getMetaTextListApi = (params: IMetaText.Query) => {
  return http.get<IPage<IMetaText.Row>>(SYSTEM_MODULE + `/meta-text/list`, params)
}

/**
* 添加
* @param params
* @returns {*}
*/
export const createMetaTextApi = (params: IMetaText.Form) => {
  return http.post(SYSTEM_MODULE + `/meta-text/create`, params)
}

/**
* 修改
* @param params
* @returns {*}
*/
export const updateMetaTextApi = (params: IMetaText.Form) => {
  return http.put(SYSTEM_MODULE + `/meta-text/update`, params)
}

/**
* 删除
* @param params
* @returns {*}
*/
export const removeMetaTextApi = (params: { ids: (string | number)[] }) => {
 return http.delete(SYSTEM_MODULE + `/meta-text/remove`, params)
}

/**
* 获取详情
* @param params
* @returns {*}
*/
export const getMetaTextDetailApi = (params: { id: number }) => {
  const { id } = params
  return http.get<IMetaText.Row>(SYSTEM_MODULE + `/meta-text/detail`, params)
}

/**
* 导入excel
* @param params
*/
export const importMetaTextExcelApi = (params : UploadRawFile, config?: AxiosRequestConfig<{}> | undefined) => {
  return http.upload(SYSTEM_MODULE + `/meta-text/import`, params, config)
}

/**
* 导出excel
* @param params
* @returns {*}
*/
export const exportMetaTextExcelApi  = (params: IMetaText.Query) => {
  return http.download(SYSTEM_MODULE + `/meta-text/export`, params)
}

/**
* 根据key获取metaText
* @param key
* @returns {*}
*/
export const getMetaTextByKeyApi = (key: string) => {
  return http.get<IMetaText.Row>(SYSTEM_MODULE + `/meta-text/getByKey`, { key })
}
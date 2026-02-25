import http from '@/api'
import { SYSTEM_MODULE } from '@/api/helper/prefix'
import type { IPage } from '@/api/interface'
import type { IMetaMenu } from '@/api/interface/system/meta/metaMenu'
import type { UploadRawFile } from "element-plus/es/components/upload/src/upload";
import type { AxiosRequestConfig } from 'axios';

/**
* 查询列表
* @param params
* @returns {*}
*/
export const getMetaMenuListApi = (params: IMetaMenu.Query) => {
  return http.get<IPage<IMetaMenu.Row>>(SYSTEM_MODULE + `/meta-menu/list`, params)
}

/**
* 添加
* @param params
* @returns {*}
*/
export const createMetaMenuApi = (params: IMetaMenu.Form) => {
  return http.post(SYSTEM_MODULE + `/meta-menu/create`, params)
}

/**
* 修改
* @param params
* @returns {*}
*/
export const updateMetaMenuApi = (params: IMetaMenu.Form) => {
  return http.put(SYSTEM_MODULE + `/meta-menu/update`, params)
}

/**
* 删除
* @param params
* @returns {*}
*/
export const removeMetaMenuApi = (params: { ids: (string | number)[] }) => {
 return http.delete(SYSTEM_MODULE + `/meta-menu/remove`, params)
}

/**
* 获取详情
* @param params
* @returns {*}
*/
export const getMetaMenuDetailApi = (params: { id: number }) => {
  const { id } = params
  return http.get<IMetaMenu.Row>(SYSTEM_MODULE + `/meta-menu/detail`, params)
}

/**
* 导入excel
* @param params
*/
export const importMetaMenuExcelApi = (params : UploadRawFile, config?: AxiosRequestConfig<{}> | undefined) => {
  return http.upload(SYSTEM_MODULE + `/meta-menu/import`, params, config)
}

/**
* 导出excel
* @param params
* @returns {*}
*/
export const exportMetaMenuExcelApi  = (params: IMetaMenu.Query) => {
  return http.download(SYSTEM_MODULE + `/meta-menu/export`, params)
}
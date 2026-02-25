import http from '@/api'
import { SYSTEM_MODULE } from '@/api/helper/prefix'
import type { IPage } from '@/api/interface'
import type { IMetaImage } from '@/api/interface/system/meta/metaImage'
import type { UploadRawFile } from "element-plus/es/components/upload/src/upload";
import type { AxiosRequestConfig } from 'axios';

/**
* 查询列表
* @param params
* @returns {*}
*/
export const getMetaImageListApi = (params: IMetaImage.Query) => {
  return http.get<IPage<IMetaImage.Row>>(SYSTEM_MODULE + `/meta-image/list`, params)
}

/**
* 添加
* @param params
* @returns {*}
*/
export const createMetaImageApi = (params: IMetaImage.Form) => {
  return http.post(SYSTEM_MODULE + `/meta-image/create`, params)
}

/**
* 修改
* @param params
* @returns {*}
*/
export const updateMetaImageApi = (params: IMetaImage.Form) => {
  return http.put(SYSTEM_MODULE + `/meta-image/update`, params)
}

/**
* 删除
* @param params
* @returns {*}
*/
export const removeMetaImageApi = (params: { ids: (string | number)[] }) => {
 return http.delete(SYSTEM_MODULE + `/meta-image/remove`, params)
}

/**
* 获取详情
* @param params
* @returns {*}
*/
export const getMetaImageDetailApi = (params: { id: number }) => {
  const { id } = params
  return http.get<IMetaImage.Row>(SYSTEM_MODULE + `/meta-image/detail`, params)
}

/**
* 导入excel
* @param params
*/
export const importMetaImageExcelApi = (params : UploadRawFile, config?: AxiosRequestConfig<{}> | undefined) => {
  return http.upload(SYSTEM_MODULE + `/meta-image/import`, params, config)
}

/**
* 导出excel
* @param params
* @returns {*}
*/
export const exportMetaImageExcelApi  = (params: IMetaImage.Query) => {
  return http.download(SYSTEM_MODULE + `/meta-image/export`, params)
}
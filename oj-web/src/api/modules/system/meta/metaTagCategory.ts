import http from '@/api'
import { SYSTEM_MODULE } from '@/api/helper/prefix'
import type { IPage } from '@/api/interface'
import type { IMetaTagCategory } from '@/api/interface/system/meta/metaTagCategory'
import type { UploadRawFile } from "element-plus/es/components/upload/src/upload";
import type { AxiosRequestConfig } from 'axios';

/**
* 查询列表
* @param params
* @returns {*}
*/
export const getMetaTagCategoryListApi = (params: IMetaTagCategory.Query) => {
  return http.get<IPage<IMetaTagCategory.Row>>(SYSTEM_MODULE + `/meta-tag-category/list`, params)
}

/**
* 添加
* @param params
* @returns {*}
*/
export const createMetaTagCategoryApi = (params: IMetaTagCategory.Form) => {
  return http.post(SYSTEM_MODULE + `/meta-tag-category/create`, params)
}

/**
* 修改
* @param params
* @returns {*}
*/
export const updateMetaTagCategoryApi = (params: IMetaTagCategory.Form) => {
  return http.put(SYSTEM_MODULE + `/meta-tag-category/update`, params)
}

/**
* 删除
* @param params
* @returns {*}
*/
export const removeMetaTagCategoryApi = (params: { ids: (string | number)[] }) => {
 return http.delete(SYSTEM_MODULE + `/meta-tag-category/remove`, params)
}

/**
* 获取详情
* @param params
* @returns {*}
*/
export const getMetaTagCategoryDetailApi = (params: { id: number }) => {
  const { id } = params
  return http.get<IMetaTagCategory.Row>(SYSTEM_MODULE + `/meta-tag-category/detail`, params)
}

/**
* 导入excel
* @param params
*/
export const importMetaTagCategoryExcelApi = (params : UploadRawFile, config?: AxiosRequestConfig<{}> | undefined) => {
  return http.upload(SYSTEM_MODULE + `/meta-tag-category/import`, params, config)
}

/**
* 导出excel
* @param params
* @returns {*}
*/
export const exportMetaTagCategoryExcelApi  = (params: IMetaTagCategory.Query) => {
  return http.download(SYSTEM_MODULE + `/meta-tag-category/export`, params)
}
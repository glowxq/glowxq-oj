import http from '@/api'
import { SYSTEM_MODULE } from '@/api/helper/prefix'
import type { IPage } from '@/api/interface'
import type { IMetaTag } from '@/api/interface/system/meta/metaTag'
import type { UploadRawFile } from "element-plus/es/components/upload/src/upload";
import type { AxiosRequestConfig } from 'axios';

/**
* 查询列表
* @param params
* @returns {*}
*/
export const getMetaTagListApi = (params: IMetaTag.Query) => {
  return http.get<IPage<IMetaTag.Row>>(SYSTEM_MODULE + `/meta-tag/list`, params)
}

/**
* 获取分类标签数量
* @returns {*}
*/
export const getMetaTagCategoryCountApi = () => {
  return http.get<IMetaTag.CategoryCount[]>(SYSTEM_MODULE + `/meta-tag/categoryCount`)
}

/**
* 添加
* @param params
* @returns {*}
*/
export const createMetaTagApi = (params: IMetaTag.Form) => {
  return http.post(SYSTEM_MODULE + `/meta-tag/create`, params)
}

/**
* 修改
* @param params
* @returns {*}
*/
export const updateMetaTagApi = (params: IMetaTag.Form) => {
  return http.put(SYSTEM_MODULE + `/meta-tag/update`, params)
}

/**
* 删除
* @param params
* @returns {*}
*/
export const removeMetaTagApi = (params: { ids: (string | number)[] }) => {
 return http.delete(SYSTEM_MODULE + `/meta-tag/remove`, params)
}

/**
* 获取详情
* @param params
* @returns {*}
*/
export const getMetaTagDetailApi = (params: { id: number }) => {
  const { id } = params
  return http.get<IMetaTag.Row>(SYSTEM_MODULE + `/meta-tag/detail`, params)
}

/**
* 导入excel
* @param params
*/
export const importMetaTagExcelApi = (params : UploadRawFile, config?: AxiosRequestConfig<{}> | undefined) => {
  return http.upload(SYSTEM_MODULE + `/meta-tag/import`, params, config)
}

/**
* 导出excel
* @param params
* @returns {*}
*/
export const exportMetaTagExcelApi  = (params: IMetaTag.Query) => {
  return http.download(SYSTEM_MODULE + `/meta-tag/export`, params)
}
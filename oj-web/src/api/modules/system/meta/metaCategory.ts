import http from '@/api'
import { SYSTEM_MODULE } from '@/api/helper/prefix'
import type { IPage } from '@/api/interface'
import type { IMetaCategory } from '@/api/interface/system/meta/metaCategory'
import type { UploadRawFile } from "element-plus/es/components/upload/src/upload";
import type { AxiosRequestConfig } from 'axios';

/**
* 查询列表
* @param params
* @returns {*}
*/
export const getMetaCategoryListApi = (params: IMetaCategory.Query) => {
  return http.get<IPage<IMetaCategory.Row>>(SYSTEM_MODULE + `/meta-category/list`, params)
}

/**
* 添加
* @param params
* @returns {*}
*/
export const createMetaCategoryApi = (params: IMetaCategory.Form) => {
  return http.post(SYSTEM_MODULE + `/meta-category/create`, params)
}

/**
* 修改
* @param params
* @returns {*}
*/
export const updateMetaCategoryApi = (params: IMetaCategory.Form) => {
  return http.put(SYSTEM_MODULE + `/meta-category/update`, params)
}

/**
* 删除
* @param params
* @returns {*}
*/
export const removeMetaCategoryApi = (params: { ids: (string | number)[] }) => {
 return http.delete(SYSTEM_MODULE + `/meta-category/remove`, params)
}

/**
* 获取详情
* @param params
* @returns {*}
*/
export const getMetaCategoryDetailApi = (params: { id: number }) => {
  const { id } = params
  return http.get<IMetaCategory.Row>(SYSTEM_MODULE + `/meta-category/detail`, params)
}

/**
* 获取分类树
* @param params
* @returns {*}
*/
export const getMetaCategoryTreeApi = (params: { excludeNodeId?: number; appendRoot?: boolean }) => {
  return http.get<IMetaCategory.Tree[]>(SYSTEM_MODULE + `/meta-category/tree`, params)
}

/**
* 导入excel
* @param params
*/
export const importMetaCategoryExcelApi = (params : UploadRawFile, config?: AxiosRequestConfig<{}> | undefined) => {
  return http.upload(SYSTEM_MODULE + `/meta-category/import`, params, config)
}

/**
* 导出excel
* @param params
* @returns {*}
*/
export const exportMetaCategoryExcelApi  = (params: IMetaCategory.Query) => {
  return http.download(SYSTEM_MODULE + `/meta-category/export`, params)
}
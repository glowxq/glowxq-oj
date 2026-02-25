import http from '@/api'
import { SYSTEM_MODULE } from '@/api/helper/prefix'
import type { IPage } from '@/api/interface'
import type { IMetaRegion } from '@/api/interface/system/meta/metaRegion'
import type { UploadRawFile } from "element-plus/es/components/upload/src/upload";
import type { AxiosRequestConfig } from 'axios';

/**
* 查询列表
* @param params
* @returns {*}
*/
export const getMetaRegionListApi = (params: IMetaRegion.Query) => {
  return http.get<IPage<IMetaRegion.Row>>(SYSTEM_MODULE + `/meta-region/list`, params)
}

/**
* 查询全量列表
* @param params
* @returns {*}
*/
export const getMetaRegionListAllApi = (params?: IMetaRegion.Query) => {
  return http.get<IMetaRegion.Row[]>(SYSTEM_MODULE + `/meta-region/listAll`, params)
}

/**
* 添加
* @param params
* @returns {*}
*/
export const createMetaRegionApi = (params: IMetaRegion.Form) => {
  return http.post(SYSTEM_MODULE + `/meta-region/create`, params)
}

/**
* 修改
* @param params
* @returns {*}
*/
export const updateMetaRegionApi = (params: IMetaRegion.Form) => {
  return http.put(SYSTEM_MODULE + `/meta-region/update`, params)
}

/**
* 删除
* @param params
* @returns {*}
*/
export const removeMetaRegionApi = (params: { ids: (string | number)[] }) => {
 return http.delete(SYSTEM_MODULE + `/meta-region/remove`, params)
}

/**
* 获取详情
* @param params
* @returns {*}
*/
export const getMetaRegionDetailApi = (params: { id: number }) => {
  const { id } = params
  return http.get<IMetaRegion.Row>(SYSTEM_MODULE + `/meta-region/detail`, params)
}

/**
* 导入excel
* @param params
*/
export const importMetaRegionExcelApi = (params : UploadRawFile, config?: AxiosRequestConfig<{}> | undefined) => {
  return http.upload(SYSTEM_MODULE + `/meta-region/import`, params, config)
}

/**
* 导出excel
* @param params
* @returns {*}
*/
export const exportMetaRegionExcelApi  = (params: IMetaRegion.Query) => {
  return http.download(SYSTEM_MODULE + `/meta-region/export`, params)
}
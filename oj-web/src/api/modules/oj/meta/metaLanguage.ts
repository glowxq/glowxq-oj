import http from '@/api'
import { OJ_MODULE } from '@/api/helper/prefix'
import type { IPage } from '@/api/interface'
import type { IMetaLanguage } from '@/api/interface/oj/meta/metaLanguage'
import type { UploadRawFile } from "element-plus/es/components/upload/src/upload";
import type { AxiosRequestConfig } from 'axios';

/**
* 查询列表
* @param params
* @returns {*}
*/
export const getMetaLanguageListApi = (params: IMetaLanguage.Query) => {
  return http.get<IPage<IMetaLanguage.Row>>(OJ_MODULE + `/meta-language`, params)
}

/**
* 添加
* @param params
* @returns {*}
*/
export const createMetaLanguageApi = (params: IMetaLanguage.Form) => {
  return http.post(OJ_MODULE + `/meta-language`, params)
}

/**
* 修改
* @param params
* @returns {*}
*/
export const updateMetaLanguageApi = (params: IMetaLanguage.Form) => {
  return http.put(OJ_MODULE + `/meta-language`, params)
}

/**
* 删除
* @param params
* @returns {*}
*/
export const removeMetaLanguageApi = (params: { ids: (string | number)[] }) => {
 return http.delete(OJ_MODULE + `/meta-language`, params)
}

/**
* 获取详情
* @param params
* @returns {*}
*/
export const getMetaLanguageDetailApi = (params: { id: string }) => {
  const { id } = params
  return http.get<IMetaLanguage.Row>(OJ_MODULE + `/meta-language/${id}`)
}

/**
* 导入excel
* @param params
*/
export const importMetaLanguageExcelApi = (params : UploadRawFile, config?: AxiosRequestConfig<{}> | undefined) => {
  return http.upload(OJ_MODULE + `/meta-language/import`, params, config)
}

/**
* 导出excel
* @param params
* @returns {*}
*/
export const exportMetaLanguageExcelApi  = (params: IMetaLanguage.Query) => {
  return http.download(OJ_MODULE + `/meta-language/export`, params)
}

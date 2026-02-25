import http from '@/api'
import { OJ_MODULE } from '@/api/helper/prefix'
import type { IPage } from '@/api/interface'
import type { IProblemLanguage } from '@/api/interface/oj/problem/problemLanguage'
import type { UploadRawFile } from "element-plus/es/components/upload/src/upload";
import type { AxiosRequestConfig } from 'axios';

/**
* 查询列表
* @param params
* @returns {*}
*/
export const getProblemLanguageListApi = (params: IProblemLanguage.Query) => {
  return http.get<IPage<IProblemLanguage.Row>>(OJ_MODULE + `/problem-language`, params)
}

/**
* 添加
* @param params
* @returns {*}
*/
export const createProblemLanguageApi = (params: IProblemLanguage.Form) => {
  return http.post(OJ_MODULE + `/problem-language`, params)
}

/**
* 修改
* @param params
* @returns {*}
*/
export const updateProblemLanguageApi = (params: IProblemLanguage.Form) => {
  return http.put(OJ_MODULE + `/problem-language`, params)
}

/**
* 删除
* @param params
* @returns {*}
*/
export const removeProblemLanguageApi = (params: { ids: (string | number)[] }) => {
 return http.delete(OJ_MODULE + `/problem-language`, params)
}

/**
* 获取详情
* @param params
* @returns {*}
*/
export const getProblemLanguageDetailApi = (params: { id: string }) => {
  const { id } = params
  return http.get<IProblemLanguage.Row>(OJ_MODULE + `/problem-language/${id}`)
}

/**
* 导入excel
* @param params
*/
export const importProblemLanguageExcelApi = (params : UploadRawFile, config?: AxiosRequestConfig<{}> | undefined) => {
  return http.upload(OJ_MODULE + `/problem-language/import`, params, config)
}

/**
* 导出excel
* @param params
* @returns {*}
*/
export const exportProblemLanguageExcelApi  = (params: IProblemLanguage.Query) => {
  return http.download(OJ_MODULE + `/problem-language/export`, params)
}

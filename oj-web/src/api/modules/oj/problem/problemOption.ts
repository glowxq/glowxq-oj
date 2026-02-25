import http from '@/api'
import { OJ_MODULE } from '@/api/helper/prefix'
import type { IPage } from '@/api/interface'
import type { IProblemOption } from '@/api/interface/oj/problem/problemOption'
import type { UploadRawFile } from "element-plus/es/components/upload/src/upload";
import type { AxiosRequestConfig } from 'axios';

/**
* 查询列表
* @param params
* @returns {*}
*/
export const getProblemOptionListApi = (params: IProblemOption.Query) => {
  return http.get<IPage<IProblemOption.Row>>(OJ_MODULE + `/problem-option`, params)
}

/**
* 添加
* @param params
* @returns {*}
*/
export const createProblemOptionApi = (params: IProblemOption.Form) => {
  return http.post(OJ_MODULE + `/problem-option`, params)
}

/**
* 修改
* @param params
* @returns {*}
*/
export const updateProblemOptionApi = (params: IProblemOption.Form) => {
  return http.put(OJ_MODULE + `/problem-option`, params)
}

/**
* 删除
* @param params
* @returns {*}
*/
export const removeProblemOptionApi = (params: { ids: (string | number)[] }) => {
 return http.delete(OJ_MODULE + `/problem-option`, params)
}

/**
* 获取详情
* @param params
* @returns {*}
*/
export const getProblemOptionDetailApi = (params: { id: string }) => {
  const { id } = params
  return http.get<IProblemOption.Row>(OJ_MODULE + `/problem-option/${id}`)
}

/**
* 导入excel
* @param params
*/
export const importProblemOptionExcelApi = (params : UploadRawFile, config?: AxiosRequestConfig<{}> | undefined) => {
  return http.upload(OJ_MODULE + `/problem-option/import`, params, config)
}

/**
* 导出excel
* @param params
* @returns {*}
*/
export const exportProblemOptionExcelApi  = (params: IProblemOption.Query) => {
  return http.download(OJ_MODULE + `/problem-option/export`, params)
}

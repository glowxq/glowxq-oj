import http from '@/api'
import { OJ_MODULE } from '@/api/helper/prefix'
import type { IPage } from '@/api/interface'
import type { IProblemBlack } from '@/api/interface/oj/problem/problemBlack'
import type { UploadRawFile } from "element-plus/es/components/upload/src/upload";
import type { AxiosRequestConfig } from 'axios';

/**
* 查询列表
* @param params
* @returns {*}
*/
export const getProblemBlackListApi = (params: IProblemBlack.Query) => {
  return http.get<IPage<IProblemBlack.Row>>(OJ_MODULE + `/problem-black`, params)
}

/**
* 添加
* @param params
* @returns {*}
*/
export const createProblemBlackApi = (params: IProblemBlack.Form) => {
  return http.post(OJ_MODULE + `/problem-black`, params)
}

/**
* 修改
* @param params
* @returns {*}
*/
export const updateProblemBlackApi = (params: IProblemBlack.Form) => {
  return http.put(OJ_MODULE + `/problem-black`, params)
}

/**
* 删除
* @param params
* @returns {*}
*/
export const removeProblemBlackApi = (params: { ids: (string | number)[] }) => {
 return http.delete(OJ_MODULE + `/problem-black`, params)
}

/**
* 获取详情
* @param params
* @returns {*}
*/
export const getProblemBlackDetailApi = (params: { id: number }) => {
  const { id } = params
  return http.get<IProblemBlack.Row>(OJ_MODULE + `/problem-black/${id}`)
}

/**
* 导入excel
* @param params
*/
export const importProblemBlackExcelApi = (params : UploadRawFile, config?: AxiosRequestConfig<{}> | undefined) => {
  return http.upload(OJ_MODULE + `/problem-black/import`, params, config)
}

/**
* 导出excel
* @param params
* @returns {*}
*/
export const exportProblemBlackExcelApi  = (params: IProblemBlack.Query) => {
  return http.download(OJ_MODULE + `/problem-black/export`, params)
}

import http from '@/api'
import { OJ_MODULE } from '@/api/helper/prefix'
import type { IPage } from '@/api/interface'
import type { IJudge } from '@/api/interface/oj/judge/judge'
import type { UploadRawFile } from "element-plus/es/components/upload/src/upload";
import type { AxiosRequestConfig } from 'axios';

/**
* 查询列表
* @param params
* @returns {*}
*/
export const getJudgeListApi = (params: IJudge.Query) => {
  return http.get<IPage<IJudge.Row>>(OJ_MODULE + `/judge`, params)
}

/**
* 添加
* @param params
* @returns {*}
*/
export const createJudgeApi = (params: IJudge.Form) => {
  return http.post(OJ_MODULE + `/judge`, params)
}

/**
* 修改
* @param params
* @returns {*}
*/
export const updateJudgeApi = (params: IJudge.Form) => {
  return http.put(OJ_MODULE + `/judge`, params)
}

/**
* 人工测评
* @param params
* @returns {*}
*/
export const manualEvaluationApi = (params: {
  id: string;
  judgeStatus: number;
  score: number;
}) => {
  return http.put(OJ_MODULE + `/judge/manualEvaluation`, params)
}

/**
* 删除
* @param params
* @returns {*}
*/
export const removeJudgeApi = (params: { ids: (string | number)[] }) => {
 return http.delete(OJ_MODULE + `/judge`, params)
}

/**
* 获取详情
* @param params
* @returns {*}
*/
export const getJudgeDetailApi = (params: { id: number }) => {
  const { id } = params
  return http.get<IJudge.Row>(OJ_MODULE + `/judge/${id}`)
}

/**
* 导入excel
* @param params
*/
export const importJudgeExcelApi = (params : UploadRawFile, config?: AxiosRequestConfig<{}> | undefined) => {
  return http.upload(OJ_MODULE + `/judge/import`, params, config)
}

/**
* 导出excel
* @param params
* @returns {*}
*/
export const exportJudgeExcelApi  = (params: IJudge.Query) => {
  return http.download(OJ_MODULE + `/judge/export`, params)
}

/**
* 获取测评结果
* @param params
* @returns {*}
*/
export const getJudgeResultApi = (params: { id: number }) => {
  const { id } = params
  return http.get<IJudge.Row>(OJ_MODULE + `/judge/result/${id}`)
}

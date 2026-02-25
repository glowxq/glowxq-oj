import http from '@/api'
import { OJ_MODULE } from '@/api/helper/prefix'
import type { IPage } from '@/api/interface'
import type { IJudgeCase } from '@/api/interface/oj/judge/judgeCase'
import type { UploadRawFile } from "element-plus/es/components/upload/src/upload";
import type { AxiosRequestConfig } from 'axios';

/**
* 查询列表
* @param params
* @returns {*}
*/
export const getJudgeCaseListApi = (params: IJudgeCase.Query) => {
  return http.get<IPage<IJudgeCase.Row>>(OJ_MODULE + `/judge-case`, params)
}

/**
* 添加
* @param params
* @returns {*}
*/
export const createJudgeCaseApi = (params: IJudgeCase.Form) => {
  return http.post(OJ_MODULE + `/judge-case`, params)
}

/**
* 修改
* @param params
* @returns {*}
*/
export const updateJudgeCaseApi = (params: IJudgeCase.Form) => {
  return http.put(OJ_MODULE + `/judge-case`, params)
}

/**
* 删除
* @param params
* @returns {*}
*/
export const removeJudgeCaseApi = (params: { ids: (string | number)[] }) => {
 return http.delete(OJ_MODULE + `/judge-case`, params)
}

/**
* 获取详情
* @param params
* @returns {*}
*/
export const getJudgeCaseDetailApi = (params: { id: string }) => {
  const { id } = params
  return http.get<IJudgeCase.Row>(OJ_MODULE + `/judge-case/${id}`)
}

/**
* 导入excel
* @param params
*/
export const importJudgeCaseExcelApi = (params : UploadRawFile, config?: AxiosRequestConfig<{}> | undefined) => {
  return http.upload(OJ_MODULE + `/judge-case/import`, params, config)
}

/**
* 导出excel
* @param params
* @returns {*}
*/
export const exportJudgeCaseExcelApi  = (params: IJudgeCase.Query) => {
  return http.download(OJ_MODULE + `/judge-case/export`, params)
}

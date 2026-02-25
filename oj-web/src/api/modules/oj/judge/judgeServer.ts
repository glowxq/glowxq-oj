import http from '@/api'
import { OJ_MODULE } from '@/api/helper/prefix'
import type { IPage } from '@/api/interface'
import type { IJudgeServer } from '@/api/interface/oj/judge/judgeServer'
import type { UploadRawFile } from "element-plus/es/components/upload/src/upload";
import type { AxiosRequestConfig } from 'axios';

/**
* 查询列表
* @param params
* @returns {*}
*/
export const getJudgeServerListApi = (params: IJudgeServer.Query) => {
  return http.get<IPage<IJudgeServer.Row>>(OJ_MODULE + `/judge-server`, params)
}

/**
* 添加
* @param params
* @returns {*}
*/
export const createJudgeServerApi = (params: IJudgeServer.Form) => {
  return http.post(OJ_MODULE + `/judge-server`, params)
}

/**
* 修改
* @param params
* @returns {*}
*/
export const updateJudgeServerApi = (params: IJudgeServer.Form) => {
  return http.put(OJ_MODULE + `/judge-server`, params)
}

/**
* 删除
* @param params
* @returns {*}
*/
export const removeJudgeServerApi = (params: { ids: (string | number)[] }) => {
 return http.delete(OJ_MODULE + `/judge-server`, params)
}

/**
* 获取详情
* @param params
* @returns {*}
*/
export const getJudgeServerDetailApi = (params: { id: number }) => {
  const { id } = params
  return http.get<IJudgeServer.Row>(OJ_MODULE + `/judge-server/${id}`)
}

/**
* 导入excel
* @param params
*/
export const importJudgeServerExcelApi = (params : UploadRawFile, config?: AxiosRequestConfig<{}> | undefined) => {
  return http.upload(OJ_MODULE + `/judge-server/import`, params, config)
}

/**
* 导出excel
* @param params
* @returns {*}
*/
export const exportJudgeServerExcelApi  = (params: IJudgeServer.Query) => {
  return http.download(OJ_MODULE + `/judge-server/export`, params)
}

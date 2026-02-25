import http from '@/api'
import { OJ_MODULE } from '@/api/helper/prefix'
import type { ITopicSubmit } from '@/api/interface/oj/topic/topicSubmit'
import type { UploadRawFile } from "element-plus/es/components/upload/src/upload";
import type { AxiosRequestConfig } from 'axios';

/**
* 查询列表
* @param params
* @returns {*}
*/
export const getTopicSubmitListApi = (params: ITopicSubmit.Query) => {
  return http.get<ITopicSubmit.Page>(OJ_MODULE + `/topic-submit`, params)
}

/**
* 添加
* @param params
* @returns {*}
*/
export const createTopicSubmitApi = (params: ITopicSubmit.Form) => {
  return http.post(OJ_MODULE + `/topic-submit`, params)
}

/**
* 修改
* @param params
* @returns {*}
*/
export const updateTopicSubmitApi = (params: ITopicSubmit.Form) => {
  return http.put(OJ_MODULE + `/topic-submit`, params)
}

/**
* 删除
* @param params
* @returns {*}
*/
export const removeTopicSubmitApi = (params: { ids: (string | number)[] }) => {
  return http.delete(OJ_MODULE + `/topic-submit`, params)
}

/**
* 获取详情
* @param params
* @returns {*}
*/
export const getTopicSubmitDetailApi = (params: { id: number }) => {
  const { id } = params
  return http.get<ITopicSubmit.Row>(OJ_MODULE + `/topic-submit/${id}`)
}

/**
* 获取主题做题情况
* @param params
* @returns {*}
*/
export const getTopicSubmitInfoApi = (params: { id: number | string }) => {
  const { id } = params
  return http.get<ITopicSubmit.SubmitInfo>(OJ_MODULE + `/topic-submit/${id}/info`)
}

/**
* 获取主题做题情况
* @param params
* @returns {*}
*/
export const getTopicSubmitStatusApi = (params: { topicId: number | string }) => {
  return http.post<ITopicSubmit.SubmitInfo>(OJ_MODULE + `/topic/topicSubmitInfo`,params)
}

/**
* 导入excel
* @param file 文件
*/
export const importTopicSubmitExcelApi = (file: File) => {
  return http.upload(OJ_MODULE + `/topic-submit/import`, { file }, {})
}

/**
* 导出excel
* @param params
* @returns {*}
*/
export const exportTopicSubmitExcelApi = (params: ITopicSubmit.Query) => {
  return http.download(OJ_MODULE + `/topic-submit/export`, params, {})
}

/**
* 导出主题提交情况excel
* @param params
* @returns {*}
*/
export const exportTopicSubmitStatusExcelApi = (params: { topicId: number | string }) => {
  const { topicId } = params
  return http.download(OJ_MODULE + `/topic/topicSubmitInfo/export?id=${topicId}`, {}, {})
}

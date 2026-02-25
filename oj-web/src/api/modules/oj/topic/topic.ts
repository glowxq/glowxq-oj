import http from '@/api'
import { OJ_MODULE } from '@/api/helper/prefix'
import type { IPage } from '@/api/interface'
import type { ITopic } from '@/api/interface/oj/topic/topic'
import type { UploadRawFile } from "element-plus/es/components/upload/src/upload";
import type { AxiosRequestConfig } from 'axios';

/**
* 查询列表
* @param params
* @returns {*}
*/
export const getTopicListApi = (params: ITopic.Query) => {
  return http.post<IPage<ITopic.Row>>(OJ_MODULE + `/topic/topicPage`, params)
}

/**
* 添加
* @param params
* @returns {*}
*/
export const createTopicApi = (params: ITopic.Form) => {
  return http.post(OJ_MODULE + `/topic`, params)
}

/**
* 修改
* @param params
* @returns {*}
*/
export const updateTopicApi = (params: ITopic.Form) => {
  return http.put(OJ_MODULE + `/topic`, params)
}

/**
* 删除
* @param params
* @returns {*}
*/
export const removeTopicApi = (params: { ids: (string | number)[] }) => {
 return http.delete(OJ_MODULE + `/topic`, params)
}

/**
* 获取详情
* @param params
* @returns {*}
*/
export const getTopicDetailApi = (params: { id: number }) => {
  const { id } = params
  return http.get<ITopic.Row>(OJ_MODULE + `/topic/${id}`)
}

/**
* 导入excel
* @param params
*/
export const importTopicExcelApi = (params : UploadRawFile, config?: AxiosRequestConfig<{}> | undefined) => {
  return http.upload(OJ_MODULE + `/topic/import`, params, config)
}

/**
* 导出excel
* @param params
* @returns {*}
*/
export const exportTopicExcelApi  = (params: ITopic.Query) => {
  return http.download(OJ_MODULE + `/topic/export`, params)
}

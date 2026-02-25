import http from '@/api'
import { OJ_MODULE } from '@/api/helper/prefix'
import type { IPage } from '@/api/interface'
import type { ITopicProblem } from '@/api/interface/oj/topic/topicProblem'
import type { UploadRawFile } from "element-plus/es/components/upload/src/upload";
import type { AxiosRequestConfig } from 'axios';

/**
* 查询列表
* @param params
* @returns {*}
*/
export const getTopicProblemListApi = (params: ITopicProblem.Query) => {
  return http.get<IPage<ITopicProblem.Row>>(OJ_MODULE + `/topic-problem`, params)
}

/**
* 添加
* @param params
* @returns {*}
*/
export const createTopicProblemApi = (params: ITopicProblem.Form) => {
  return http.post(OJ_MODULE + `/topic-problem`, params)
}

/**
* 修改
* @param params
* @returns {*}
*/
export const updateTopicProblemApi = (params: ITopicProblem.Form) => {
  return http.put(OJ_MODULE + `/topic-problem`, params)
}

/**
* 删除
* @param params
* @returns {*}
*/
export const removeTopicProblemApi = (params: { ids: (string | number)[] }) => {
 return http.delete(OJ_MODULE + `/topic-problem`, params)
}

/**
* 获取详情
* @param params
* @returns {*}
*/
export const getTopicProblemDetailApi = (params: { id: number }) => {
  const { id } = params
  return http.get<ITopicProblem.Row>(OJ_MODULE + `/topic-problem/${id}`)
}

/**
* 导入excel
* @param params
*/
export const importTopicProblemExcelApi = (params : UploadRawFile, config?: AxiosRequestConfig<{}> | undefined) => {
  return http.upload(OJ_MODULE + `/topic-problem/import`, params, config)
}

/**
* 导出excel
* @param params
* @returns {*}
*/
export const exportTopicProblemExcelApi  = (params: ITopicProblem.Query) => {
  return http.download(OJ_MODULE + `/topic-problem/export`, params)
}

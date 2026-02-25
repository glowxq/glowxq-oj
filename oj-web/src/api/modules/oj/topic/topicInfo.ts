import http from '@/api'
import { OJ_MODULE } from '@/api/helper/prefix'
import type { IPage } from '@/api/interface'
import type { ITopicInfo } from '@/api/interface/oj/topic/topicInfo'
import type { UploadRawFile } from "element-plus/es/components/upload/src/upload";
import type { AxiosRequestConfig } from 'axios';

/**
* 查询列表
* @param params
* @returns {*}
*/
export const getTopicInfoListApi = (params: ITopicInfo.Query) => {
  return http.get<IPage<ITopicInfo.Row>>(OJ_MODULE + `/topic-info`, params)
}

/**
* 添加
* @param params
* @returns {*}
*/
export const createTopicInfoApi = (params: ITopicInfo.Form) => {
  return http.post(OJ_MODULE + `/topic-info`, params)
}

/**
* 修改
* @param params
* @returns {*}
*/
export const updateTopicInfoApi = (params: ITopicInfo.Form) => {
  return http.put(OJ_MODULE + `/topic-info`, params)
}

/**
* 删除
* @param params
* @returns {*}
*/
export const removeTopicInfoApi = (params: { ids: (string | number)[] }) => {
 return http.delete(OJ_MODULE + `/topic-info`, params)
}

/**
* 获取详情
* @param params
* @returns {*}
*/
export const getTopicInfoDetailApi = (params: { id: number }) => {
  const { id } = params
  return http.get<ITopicInfo.Row>(OJ_MODULE + `/topic-info/${id}`)
}

/**
* 获取主题排行榜
* @param params
* @returns {*}
*/
export const getTopicRankApi = (params: { topicId: number, topicJudgeType?: number }) => {
  return http.post<ITopicInfo.Row[]>(OJ_MODULE + `/topic/topicRank`, params)
}

/**
* 导入excel
* @param params
*/
export const importTopicInfoExcelApi = (params : UploadRawFile, config?: AxiosRequestConfig<{}> | undefined) => {
  return http.upload(OJ_MODULE + `/topic-info/import`, params, config)
}

/**
* 导出excel
* @param params
* @returns {*}
*/
export const exportTopicInfoExcelApi  = (params: ITopicInfo.Query) => {
  return http.download(OJ_MODULE + `/topic-info/export`, params)
}

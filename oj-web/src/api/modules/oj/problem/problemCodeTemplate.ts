import http from '@/api'
import { OJ_MODULE } from '@/api/helper/prefix'
import type { IPage } from '@/api/interface'
import type { IProblemCodeTemplate } from '@/api/interface/oj/problem/problemCodeTemplate'
import type { UploadRawFile } from "element-plus/es/components/upload/src/upload";
import type { AxiosRequestConfig } from 'axios';

/**
* 查询列表
* @param params
* @returns {*}
*/
export const getProblemCodeTemplateListApi = (params: IProblemCodeTemplate.Query) => {
  return http.get<IPage<IProblemCodeTemplate.Row>>(OJ_MODULE + `/problem-code-template`, params)
}

/**
* 添加
* @param params
* @returns {*}
*/
export const createProblemCodeTemplateApi = (params: IProblemCodeTemplate.Form) => {
  return http.post(OJ_MODULE + `/problem-code-template`, params)
}

/**
* 修改
* @param params
* @returns {*}
*/
export const updateProblemCodeTemplateApi = (params: IProblemCodeTemplate.Form) => {
  return http.put(OJ_MODULE + `/problem-code-template`, params)
}

/**
* 删除
* @param params
* @returns {*}
*/
export const removeProblemCodeTemplateApi = (params: { ids: (string | number)[] }) => {
 return http.delete(OJ_MODULE + `/problem-code-template`, params)
}

/**
* 获取详情
* @param params
* @returns {*}
*/
export const getProblemCodeTemplateDetailApi = (params: { id: number }) => {
  const { id } = params
  return http.get<IProblemCodeTemplate.Row>(OJ_MODULE + `/problem-code-template/${id}`)
}

/**
* 导入excel
* @param params
*/
export const importProblemCodeTemplateExcelApi = (params : UploadRawFile, config?: AxiosRequestConfig<{}> | undefined) => {
  return http.upload(OJ_MODULE + `/problem-code-template/import`, params, config)
}

/**
* 导出excel
* @param params
* @returns {*}
*/
export const exportProblemCodeTemplateExcelApi  = (params: IProblemCodeTemplate.Query) => {
  return http.download(OJ_MODULE + `/problem-code-template/export`, params)
}

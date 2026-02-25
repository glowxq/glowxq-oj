import http from '@/api'
import { OJ_MODULE } from '@/api/helper/prefix'
import type { IPage } from '@/api/interface'
import type { ICodeRecord } from '@/api/interface/oj/code/codeRecord'
import type { UploadRawFile } from "element-plus/es/components/upload/src/upload";
import type { AxiosRequestConfig } from 'axios';

/**
* 查询列表
* @param params
* @returns {*}
*/
export const getCodeRecordListApi = (params: ICodeRecord.Query) => {
  return http.get<IPage<ICodeRecord.Row>>(OJ_MODULE + `/code-record`, params)
}

/**
* 添加
* @param params
* @returns {*}
*/
export const createCodeRecordApi = (params: ICodeRecord.Form) => {
  return http.post(OJ_MODULE + `/code-record`, params)
}

/**
* 修改
* @param params
* @returns {*}
*/
export const updateCodeRecordApi = (params: ICodeRecord.Form) => {
  return http.put(OJ_MODULE + `/code-record`, params)
}

/**
* 删除
* @param params
* @returns {*}
*/
export const removeCodeRecordApi = (params: { ids: (string | number)[] }) => {
 return http.delete(OJ_MODULE + `/code-record`, params)
}

/**
* 获取详情
* @param params
* @returns {*}
*/
export const getCodeRecordDetailApi = (params: { id: number }) => {
  const { id } = params
  return http.get<ICodeRecord.Row>(OJ_MODULE + `/code-record/${id}`)
}

/**
* 导入excel
* @param params
*/
export const importCodeRecordExcelApi = (params : UploadRawFile, config?: AxiosRequestConfig<{}> | undefined) => {
  return http.upload(OJ_MODULE + `/code-record/import`, params, config)
}

/**
* 导出excel
* @param params
* @returns {*}
*/
export const exportCodeRecordExcelApi  = (params: ICodeRecord.Query) => {
  return http.download(OJ_MODULE + `/code-record/export`, params)
}

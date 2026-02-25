import http from '@/api'
import { OJ_MODULE } from '@/api/helper/prefix'
import type { IPage } from '@/api/interface'
import type { ICodeMonitor } from '@/api/interface/oj/code/codeMonitor'
import type { UploadRawFile } from "element-plus/es/components/upload/src/upload";
import type { AxiosRequestConfig } from 'axios';

/**
* 查询列表
* @param params
* @returns {*}
*/
export const getCodeMonitorListApi = (params: ICodeMonitor.Query) => {
  return http.get<IPage<ICodeMonitor.Row>>(OJ_MODULE + `/code-monitor`, params)
}

/**
* 添加
* @param params
* @returns {*}
*/
export const createCodeMonitorApi = (params: ICodeMonitor.Form) => {
  return http.post(OJ_MODULE + `/code-monitor`, params)
}

/**
* 修改
* @param params
* @returns {*}
*/
export const updateCodeMonitorApi = (params: ICodeMonitor.Form) => {
  return http.put(OJ_MODULE + `/code-monitor`, params)
}

/**
* 删除
* @param params
* @returns {*}
*/
export const removeCodeMonitorApi = (params: { ids: (string | number)[] }) => {
 return http.delete(OJ_MODULE + `/code-monitor`, params)
}

/**
* 获取详情
* @param params
* @returns {*}
*/
export const getCodeMonitorDetailApi = (params: { id: number }) => {
  const { id } = params
  return http.get<ICodeMonitor.Row>(OJ_MODULE + `/code-monitor/${id}`)
}

/**
* 导入excel
* @param params
*/
export const importCodeMonitorExcelApi = (params : UploadRawFile, config?: AxiosRequestConfig<{}> | undefined) => {
  return http.upload(OJ_MODULE + `/code-monitor/import`, params, config)
}

/**
* 导出excel
* @param params
* @returns {*}
*/
export const exportCodeMonitorExcelApi  = (params: ICodeMonitor.Query) => {
  return http.download(OJ_MODULE + `/code-monitor/export`, params)
}

/**
* 监控某个班级的代码
* @param params
* @returns {*}
*/
export const getCodeMonitorListByGroupApi = (params: { groupId: number }) => {
  return http.get<ICodeMonitor.Row[]>(OJ_MODULE + `/code-monitor/listGroup`, params)
}

/**
* 搜索监控的代码
* @param params
* @returns {*}
*/
export const searchMonitorCodeListApi = (params: ICodeMonitor.SearchMonitorCodeDTO) => {
  return http.get<IPage<ICodeMonitor.Row>>(OJ_MODULE + `/code-monitor/searchMonitorCodeList`, params)
}

/**
* 老师推送覆盖代码
* @param params
* @returns {*}
*/
export const coveredPushApi = (params: ICodeMonitor.CoveredCodeMonitorDTO) => {
  return http.put(OJ_MODULE + `/code-monitor/coveredPush`, params)
}

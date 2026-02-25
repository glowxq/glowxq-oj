import http from '@/api'
import { SYSTEM_MODULE } from '@/api/helper/prefix'
import type { IPage } from '@/api/interface'
import type { ISysOperationLog } from '@/api/interface/system/admin/sysOperationLog'
import type { UploadRawFile } from "element-plus/es/components/upload/src/upload";
import type { AxiosRequestConfig } from 'axios';

/**
* 查询列表
* @param params
* @returns {*}
*/
export const getSysOperationLogListApi = (params: ISysOperationLog.Query) => {
  return http.get<IPage<ISysOperationLog.Row>>(SYSTEM_MODULE + `/sys-operation-log/list`, params)
}

/**
* 添加
* @param params
* @returns {*}
*/
export const createSysOperationLogApi = (params: ISysOperationLog.Form) => {
  return http.post(SYSTEM_MODULE + `/sys-operation-log/create`, params)
}

/**
* 修改
* @param params
* @returns {*}
*/
export const updateSysOperationLogApi = (params: ISysOperationLog.Form) => {
  return http.put(SYSTEM_MODULE + `/sys-operation-log/update`, params)
}

/**
* 删除
* @param params
* @returns {*}
*/
export const removeSysOperationLogApi = (params: { ids: (string | number)[] }) => {
 return http.delete(SYSTEM_MODULE + `/sys-operation-log/remove`, params)
}

/**
* 获取详情
* @param params
* @returns {*}
*/
export const getSysOperationLogDetailApi = (params: { id: number }) => {
  const { id } = params
  return http.get<ISysOperationLog.Row>(SYSTEM_MODULE + `/sys-operation-log/detail`, params)
}

/**
* 导入excel
* @param params
*/
export const importSysOperationLogExcelApi = (params : UploadRawFile, config?: AxiosRequestConfig<{}> | undefined) => {
  return http.upload(SYSTEM_MODULE + `/sys-operation-log/import`, params, config)
}

/**
* 导出excel
* @param params
* @returns {*}
*/
export const exportSysOperationLogExcelApi  = (params: ISysOperationLog.Query) => {
  return http.download(SYSTEM_MODULE + `/sys-operation-log/export`, params)
}
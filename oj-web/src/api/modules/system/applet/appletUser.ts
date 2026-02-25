import http from '@/api'
import { SYSTEM_MODULE } from '@/api/helper/prefix'
import type { IPage } from '@/api/interface'
import type { IAppletUser } from '@/api/interface/system/applet/appletUser'
import type { UploadRawFile } from "element-plus/es/components/upload/src/upload";
import type { AxiosRequestConfig } from 'axios';

/**
* 查询列表
* @param params
* @returns {*}
*/
export const getAppletUserListApi = (params: IAppletUser.Query) => {
  return http.get<IPage<IAppletUser.Row>>(SYSTEM_MODULE + `/applet-user/list`, params)
}

/**
* 添加
* @param params
* @returns {*}
*/
export const createAppletUserApi = (params: IAppletUser.Form) => {
  return http.post(SYSTEM_MODULE + `/applet-user/create`, params)
}

/**
* 修改
* @param params
* @returns {*}
*/
export const updateAppletUserApi = (params: IAppletUser.Form) => {
  return http.put(SYSTEM_MODULE + `/applet-user/update`, params)
}

/**
* 删除
* @param params
* @returns {*}
*/
export const removeAppletUserApi = (params: { ids: (string | number)[] }) => {
 return http.delete(SYSTEM_MODULE + `/applet-user/remove`, params)
}

/**
* 获取详情
* @param params
* @returns {*}
*/
export const getAppletUserDetailApi = (params: { id: number }) => {
  const { id } = params
  return http.get<IAppletUser.Row>(SYSTEM_MODULE + `/applet-user/detail`, params)
}

/**
* 导入excel
* @param params
*/
export const importAppletUserExcelApi = (params : UploadRawFile, config?: AxiosRequestConfig<{}> | undefined) => {
  return http.upload(SYSTEM_MODULE + `/applet-user/import`, params, config)
}

/**
* 导出excel
* @param params
* @returns {*}
*/
export const exportAppletUserExcelApi  = (params: IAppletUser.Query) => {
  return http.download(SYSTEM_MODULE + `/applet-user/export`, params)
}
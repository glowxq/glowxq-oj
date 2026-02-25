import http from '@/api'
import { OJ_MODULE } from '@/api/helper/prefix'
import type { IPage } from '@/api/interface'
import type { IUserProblem } from '@/api/interface/oj/user/userProblem'
import type { UploadRawFile } from "element-plus/es/components/upload/src/upload";
import type { AxiosRequestConfig } from 'axios';

/**
* 查询列表
* @param params
* @returns {*}
*/
export const getUserProblemListApi = (params: IUserProblem.Query) => {
  return http.get<IPage<IUserProblem.Row>>(OJ_MODULE + `/user-problem`, params)
}

/**
* 添加
* @param params
* @returns {*}
*/
export const createUserProblemApi = (params: IUserProblem.Form) => {
  return http.post(OJ_MODULE + `/user-problem`, params)
}

/**
* 修改
* @param params
* @returns {*}
*/
export const updateUserProblemApi = (params: IUserProblem.Form) => {
  return http.put(OJ_MODULE + `/user-problem`, params)
}

/**
* 删除
* @param params
* @returns {*}
*/
export const removeUserProblemApi = (params: { ids: (string | number)[] }) => {
 return http.delete(OJ_MODULE + `/user-problem`, params)
}

/**
* 获取详情
* @param params
* @returns {*}
*/
export const getUserProblemDetailApi = (params: { id: number }) => {
  const { id } = params
  return http.get<IUserProblem.Row>(OJ_MODULE + `/user-problem/${id}`)
}

/**
* 导入excel
* @param params
*/
export const importUserProblemExcelApi = (params : UploadRawFile, config?: AxiosRequestConfig<{}> | undefined) => {
  return http.upload(OJ_MODULE + `/user-problem/import`, params, config)
}

/**
* 导出excel
* @param params
* @returns {*}
*/
export const exportUserProblemExcelApi  = (params: IUserProblem.Query) => {
  return http.download(OJ_MODULE + `/user-problem/export`, params)
}

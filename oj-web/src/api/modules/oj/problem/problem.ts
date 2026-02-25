import http from '@/api'
import { OJ_MODULE } from '@/api/helper/prefix'
import type { IPage } from '@/api/interface'
import type { IProblem } from '@/api/interface/oj/problem/problem'
import type { UploadRawFile } from "element-plus/es/components/upload/src/upload";
import type { AxiosRequestConfig } from 'axios';

/**
* 查询列表
* @param params
* @returns {*}
*/
export const getProblemListApi = (params: IProblem.Query) => {
  return http.post<IPage<IProblem.Row>>(OJ_MODULE + `/problem/list`, params)
}

/**
* 添加
* @param params
* @returns {*}
*/
export const createProblemApi = (params: IProblem.Form) => {
  return http.post(OJ_MODULE + `/problem`, params)
}

/**
* 修改
* @param params
* @returns {*}
*/
export const updateProblemApi = (params: IProblem.Form) => {
  return http.put(OJ_MODULE + `/problem`, params)
}

/**
* 删除
* @param params
* @returns {*}
*/
export const removeProblemApi = (params: { ids: (string | number)[] }) => {
 return http.delete(OJ_MODULE + `/problem`, params)
}

/**
* 获取详情
* @param params
* @returns {*}
*/
export const getProblemDetailApi = (params: { id: number }) => {
  const { id } = params
  return http.get<IProblem.Row>(OJ_MODULE + `/problem/${id}`)
}

/**
* 导入excel
* @param params
*/
export const importProblemExcelApi = (params : UploadRawFile, config?: AxiosRequestConfig<{}> | undefined) => {
  return http.upload(OJ_MODULE + `/problem/import`, params, config)
}

/**
* 导出excel
* @param params
* @returns {*}
*/
export const exportProblemExcelApi  = (params: IProblem.Query) => {
  return http.download(OJ_MODULE + `/problem/export`, params)
}

/**
 * 导入题目
 * @param params 导入参数
 * @returns
 */
export const importProblemProgramApi = (params: {
  fileUrl: string;
  problemSourceType: string;
  needInsertUnExistTag: boolean;
  problemType?: string;
}) => {
  return http.post<any>(`/problem/program/import`, params);
}

/**
 * 批量导入编程题
 * @param params 批量导入参数
 * @returns
 */
export const batchImportProblemProgramApi = (params: {
  batchFileUrl: string;
  problemSourceType: string;
  needInsertUnExistTag: boolean;
}) => {
  return http.post<string[]>(OJ_MODULE + `/problem/program/batch-import`, params);
}

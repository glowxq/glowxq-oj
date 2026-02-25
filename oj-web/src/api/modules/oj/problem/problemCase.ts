import http from '@/api'
import { OJ_MODULE } from '@/api/helper/prefix'
import type { IPage } from '@/api/interface'
import type { IProblemCase } from '@/api/interface/oj/problem/problemCase'
import type { UploadRawFile } from "element-plus/es/components/upload/src/upload";
import type { AxiosRequestConfig } from 'axios';

/**
* 查询列表
* @param params
* @returns {*}
*/
export const getProblemCaseListApi = (params: IProblemCase.Query) => {
  return http.get<IPage<IProblemCase.Row>>(OJ_MODULE + `/problem-case`, params)
}

/**
* 添加
* @param params
* @returns {*}
*/
export const createProblemCaseApi = (params: IProblemCase.Form) => {
  return http.post(OJ_MODULE + `/problem-case`, params)
}

/**
* 修改
* @param params
* @returns {*}
*/
export const updateProblemCaseApi = (params: IProblemCase.Form) => {
  return http.put(OJ_MODULE + `/problem-case`, params)
}

/**
* 删除
* @param params
* @returns {*}
*/
export const removeProblemCaseApi = (params: { ids: (string | number)[] }) => {
 return http.delete(OJ_MODULE + `/problem-case`, params)
}

/**
* 获取详情
* @param params
* @returns {*}
*/
export const getProblemCaseDetailApi = (params: { id: string }) => {
  const { id } = params
  return http.get<IProblemCase.Row>(OJ_MODULE + `/problem-case/${id}`)
}

/**
* 导入excel
* @param params
*/
export const importProblemCaseExcelApi = (params : UploadRawFile, config?: AxiosRequestConfig<{}> | undefined) => {
  return http.upload(OJ_MODULE + `/problem-case/import`, params, config)
}

/**
* 导出excel
* @param params
* @returns {*}
*/
export const exportProblemCaseExcelApi  = (params: IProblemCase.Query) => {
  return http.download(OJ_MODULE + `/problem-case/export`, params)
}

/**
 * 上传测试用例压缩包
 * @param params 文件以及模式
 * @returns {*}
 */
export const uploadTestcaseZipApi = (params: { file: File, mode: string }, config?: AxiosRequestConfig<{}> | undefined) => {
  return http.upload(OJ_MODULE + `/problem-case/case/uploadTestcaseZip?mode=${params.mode}`, { file: params.file }, config)
}

/**
 * 上传测试用例ZIP文件（URL方式）
 * @param url 已上传到OSS的文件URL
 * @param mode 处理模式，默认为"default"
 * @returns
 */
export const uploadTestcaseZipUrlApi = (url: string, mode: string = 'default') => {
  return http.post<any>(OJ_MODULE + '/oj/problem-case/case/uploadTestcaseZipUrl', {}, { params: { url, mode } });
};

import http from '@/api'
import { OJ_MODULE } from '@/api/helper/prefix'
import type { IPage } from '@/api/interface'
import type { ICourse } from '@/api/interface/oj/course/course'
import type { UploadRawFile } from "element-plus/es/components/upload/src/upload";
import type { AxiosRequestConfig } from 'axios';

/**
* 查询列表
* @param params
* @returns {*}
*/
export const getCourseListApi = (params: ICourse.Query) => {
  return http.post<IPage<ICourse.Row>>(OJ_MODULE + `/course/list`, params)
}

/**
* 添加
* @param params
* @returns {*}
*/
export const createCourseApi = (params: ICourse.Form) => {
  return http.post(OJ_MODULE + `/course`, params)
}

/**
* 修改
* @param params
* @returns {*}
*/
export const updateCourseApi = (params: ICourse.Form) => {
  return http.put(OJ_MODULE + `/course`, params)
}

/**
* 删除
* @param params
* @returns {*}
*/
export const removeCourseApi = (params: { ids: (string | number)[] }) => {
 return http.delete(OJ_MODULE + `/course`, params)
}

/**
* 获取详情
* @param params
* @returns {*}
*/
export const getCourseDetailApi = (params: { id: number }) => {
  const { id } = params
  return http.get<ICourse.Row>(OJ_MODULE + `/course/${id}`)
}

/**
* 导入excel
* @param params
*/
export const importCourseExcelApi = (params : UploadRawFile, config?: AxiosRequestConfig<{}> | undefined) => {
  return http.upload(OJ_MODULE + `/course/import`, params, config)
}

/**
* 导出excel
* @param params
* @returns {*}
*/
export const exportCourseExcelApi  = (params: ICourse.Query) => {
  return http.download(OJ_MODULE + `/course/export`, params)
}

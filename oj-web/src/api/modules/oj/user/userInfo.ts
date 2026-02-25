import http from '@/api'
import {OJ_MODULE} from '@/api/helper/prefix'
import type {IPage} from '@/api/interface'
import type {IUserInfo} from '@/api/interface/oj/user/userInfo'
import type {UploadRawFile} from "element-plus/es/components/upload/src/upload";
import type {AxiosRequestConfig} from 'axios';

/**
 * 查询列表
 * @param params
 * @returns {*}
 */
export const getUserInfoListApi = (params: IUserInfo.Query) => {
    return http.post<IPage<IUserInfo.Row>>(OJ_MODULE + `/user-info/list`, params)
}

/**
 * 添加
 * @param params
 * @returns {*}
 */
export const createUserInfoApi = (params: IUserInfo.Form) => {
    return http.post(OJ_MODULE + `/user-info/create`, params)
}

/**
 * 修改
 * @param params
 * @returns {*}
 */
export const updateUserInfoApi = (params: IUserInfo.Form) => {
    return http.put(OJ_MODULE + `/user-info/update`, params)
}

/**
 * 删除
 * @param params
 * @returns {*}
 */
export const removeUserInfoApi = (params: { ids: (string | number)[] }) => {
    return http.delete(OJ_MODULE + `/user-info/remove`, params)
}

/**
 * 获取详情
 * @param params
 * @returns {*}
 */
export const getUserInfoDetailApi = (params: { id: number }) => {
    return http.get<IUserInfo.Row>(OJ_MODULE + `/user-info/detail`, params)
}

/**
 * 导入excel
 * @param params
 */
export const importUserInfoExcelApi = (params: UploadRawFile, config?: AxiosRequestConfig<{}> | undefined) => {
    return http.upload(OJ_MODULE + `/user-info/import`, params, config)
}

/**
 * 导出excel
 * @param params
 * @returns {*}
 */
export const exportUserInfoExcelApi = (params: IUserInfo.Query) => {
    return http.download(OJ_MODULE + `/user-info/export`, params)
}

/**
 * 获取AC题目排名列表
 * @param params
 * @returns {*}
 */
export const getUserAcRankingListApi = (params: { groupId?: number }) => {
    return http.post<IUserInfo.Row[]>(OJ_MODULE + `/user-info/rank`, params)
}

/**
 * 批量绑定班级
 * @param params 用户ID数组和班级ID数组
 * @returns 绑定结果
 */
export const bindUserInfoGroupApi = (params: { userIds: number[], groupIds: number[] }) => {
    return http.post(OJ_MODULE + `/user-info/bind`, params)
}

/**
 * OJ用户注册
 * @param params 注册表单数据
 * @returns 注册结果
 */
export const registerUserInfoApi = (params: IUserInfo.RegisterForm) => {
    return http.post(OJ_MODULE + `/user-info/register`, params)
}

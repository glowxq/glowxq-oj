import http from '@/api';
import { OJ_MODULE } from '@/api/helper/prefix';
import type { IOJUser } from '@/api/interface/oj/user/user';
import type { IPage } from '@/api/interface';

/**
 * 获取OJ用户列表
 * @param params 查询参数
 * @returns 用户分页数据
 */
export const getOJUserList = (params: IOJUser.Query) => {
  return http.post<IPage<IOJUser.Info>>(OJ_MODULE + `/oj-user/listPage`, params, { loading: false });
};

/**
 * 根据ID获取OJ用户详情
 * @param params ID参数
 * @returns 用户详情
 */
export const getOJUserDetail = (params: { id: number }) => {
  const { id } = params;
  return http.get<IOJUser.Info>(OJ_MODULE + `/oj-user/${id}`);
};

/**
 * 添加OJ用户
 * @param params 用户表单数据
 * @returns 添加结果
 */
export const addOJUser = (params: IOJUser.Form) => {
  return http.post(OJ_MODULE + `/oj-user`, params);
};

/**
 * 修改OJ用户
 * @param params 用户表单数据
 * @returns 修改结果
 */
export const editOJUser = (params: IOJUser.Form) => {
  return http.put(OJ_MODULE + `/oj-user`, params);
};

/**
 * 删除OJ用户
 * @param params ID数组
 * @returns 删除结果
 */
export const deleteOJUser = (params: { ids: number[] }) => {
  return http.delete(OJ_MODULE + `/oj-user`, params);
};

/**
 * 获取OJ用户绑定信息（标签和班级）
 * @param params 用户ID
 * @returns 绑定信息
 */
export const getOJUserBindInfo = (params: { userId: number }) => {
  return http.get<IOJUser.UserBindInfo>(OJ_MODULE + `/oj-user/bind-info`, params);
};

/**
 * 获取用户绑定信息（标签和班级）
 * @param params
 * @returns {*}
 */
export const getUserBindInfo = <T = any>(params: { userId: number }) => {
    return http.get<T>(OJ_MODULE + `/oj-user/bind-info/getBindInfo`, params);
  };

  /**
   * 绑定用户班级
   * @param params
   * @returns {*}
   */
  export const bindUserGroupApi = (params: { userIds: number[], groupIds: number[] }) => {
    return http.post(OJ_MODULE + `/oj-user/bind-info/bindGroup`, params);
  };

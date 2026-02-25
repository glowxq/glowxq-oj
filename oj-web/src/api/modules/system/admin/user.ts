import http from '@/api';
import { ADMIN_MODULE } from '@/api/helper/prefix';
import type { IUser } from '@/api/interface/system/admin/user';
import type { IPage } from '@/api/interface';
import type { ISysDept } from '@/api/interface/system/admin/dept';

/**
 * 获取用户列表
 * @param params
 * @returns {*}
 */
export const getUserList = (params: IUser.Query) => {
  return http.post<IPage<IUser.Info>>(ADMIN_MODULE + `/sys-user/listPage`, params, { loading: false });
};

/**
 * 添加用户
 * @param params
 * @returns {*}
 */
export const addUser = (params: IUser.Form) => {
  return http.post(ADMIN_MODULE + `/sys-user`, params);
};

/**
 * 修改用户
 * @param params
 * @returns {*}
 */
export const editUser = (params: IUser.Form) => {
  return http.put(ADMIN_MODULE + `/sys-user`, params);
};

/**
 * 删除用户
 * @param params
 * @returns {*}
 */
export const deleteUser = (params: { ids: number[] }) => {
  return http.delete(ADMIN_MODULE + `/sys-user`, params);
};

/**
 * 获取用户角色
 * @param params
 * @returns {*}
 */
export const getUserRole = (params: { userId: number }) => {
  return http.get<IUser.RoleData>(ADMIN_MODULE + `/sys-user/role`, params);
};

/**
 * 设置用户角色
 * @param params
 * @returns {*}
 */
export const setUserRole = (params: IUser.RoleForm) => {
  return http.put(ADMIN_MODULE + `/sys-user/role`, params);
};

/**
 * （个人）修改密码
 * @param params
 * @returns {*}
 */
export const changePassword = (params: IUser.PasswordForm) => {
  return http.put(ADMIN_MODULE + `/sys-user/password`, params);
};

/**
 * 重置密码
 * @param params
 */
export const resetPassword = (params: { id: number }) => {
  const { id } = params;
  return http.put(ADMIN_MODULE + `/sys-user/reset/password/${id}`, {});
};

/**
 * 获取登录用户信息
 * @returns {*}
 */
export const getUserinfo = () => {
  return http.get<IUser.Info>(ADMIN_MODULE + `/sys-user/userinfo`);
};

/**
 * 添加
 * @param params
 * @returns {*}
 */
export const bindUserDeptApi = (params: ISysDept.DeptSetting) => {
  return http.post(ADMIN_MODULE + `/sys-user/dept/bind`, params);
};

/**
 * 用户部门属性列表
 */
export const getUserDeptTree = () => {
  return http.get<ISysDept.Tree[]>(ADMIN_MODULE + `/sys-user/dept/tree`);
};

/**
 * 解锁用户
 * @param params
 * @returns {*}
 */
export const unlockUser = (params: { ids: (string | number)[] }) => {
  return http.post(ADMIN_MODULE + `/sys-user/unlock`, params);
};

/**
 * 获取详情
 * @param params
 * @returns {*}
 */
export const getUserDetailApi = (params: { id: string }) => {
  const { id } = params;
  return http.get<IUser.Info>(ADMIN_MODULE + `/sys-user/${id}`);
};

/**
 * 获取用户数据角色
 * @param params
 * @returns {*}
 */
export const getDataUserRole = (params: { userId: number }) => {
  return http.get<IUser.RoleData>(ADMIN_MODULE + `/sys-user/datarole`, params);
};

/**
 * 设置用户数据角色
 * @param params
 * @returns {*}
 */
export const setUserDataRole = (params: IUser.RoleForm) => {
  return http.put(ADMIN_MODULE + `/sys-user/datarole`, params);
};

/**
 * 根据关键词搜索用户
 * @param params
 * @returns {*}
 */
export const searchUserList = (params: IUser.SearchUserDTO) => {
  return http.post<IUser.PageResult<IUser.SearchUserVO>>(ADMIN_MODULE + `/sys-user/searchUserList`, params);
};

/**
 * 批量获取用户信息
 * @param params
 * @returns {*}
 */
export const batchGetUsers = (params: { ids: number[] }) => {
  return http.post<IUser.Info[]>(ADMIN_MODULE + `/sys-user/batchGet`, params);
};


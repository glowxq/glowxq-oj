import http from '@/api';
import { ADMIN_MODULE } from '@/api/helper/prefix';
import type { IPage } from '@/api/interface';
import type { ISysDept } from '@/api/interface/system/admin/dept';

/**
 * 查询列表
 * @param params
 * @returns {*}
 */
export const getSysDeptListApi = (params: ISysDept.Query) => {
  return http.get<IPage<ISysDept.Row>>(ADMIN_MODULE + `/sys-dept`, params);
};

/**
 * 添加
 * @param params
 * @returns {*}
 */
export const createSysDeptApi = (params: ISysDept.Form) => {
  return http.post(ADMIN_MODULE + `/sys-dept`, params);
};

/**
 * 修改
 * @param params
 * @returns {*}
 */
export const updateSysDeptApi = (params: ISysDept.Form) => {
  return http.put(ADMIN_MODULE + `/sys-dept`, params);
};

/**
 * 删除
 * @param params
 * @returns {*}
 */
export const removeSysDeptApi = (params: { ids: number[] }) => {
  return http.delete(ADMIN_MODULE + `/sys-dept`, params);
};

/**
 * 获取详情
 * @param params
 * @returns {*}
 */
export const getSysDeptDetailApi = (params: { id: number }) => {
  const { id } = params;
  return http.get<ISysDept.Row>(ADMIN_MODULE + `/sys-dept/${id}`);
};

/**
 * 获取上级菜单树
 * @param params
 * @returns {*}
 */
export const getMenuTree = (params: { excludeNodeId?: number; appendRoot?: boolean }) => {
  return http.get<ISysDept.Tree[]>(ADMIN_MODULE + `/sys-dept/tree`, params);
};

/**
 * 获取部门负责人关系
 * @param params
 * @returns {*}
 */
export const getSysDeptLeaderApi = () => {
  return http.get<ISysDept.LeaderData>(ADMIN_MODULE + `/sys-dept/leader`);
};

/**
 * 获取部门树
 * @param params
 * @returns {*}
 */
export const getDeptTrees = (params: { deptId?: number }) => {
  return http.get<ISysDept.Dept>(ADMIN_MODULE + `/sys-dept/datascope`, params);
};

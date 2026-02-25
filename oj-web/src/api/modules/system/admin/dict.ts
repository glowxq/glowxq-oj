import http from '@/api';
import { ADMIN_MODULE } from '@/api/helper/prefix';
import type { IDict } from '@/api/interface/system/admin/dict';
import type { IPage } from '@/api/interface';

/**
 * 字典类别列表
 * @param params
 * @returns {*}
 */
export const getDictType = (params: IDict.DictTypeQuery) => {
  return http.get<IPage<IDict.DictType>>(ADMIN_MODULE + `/sys-dict-type`, params);
};

/**
 * 添加字典类别
 * @param params
 * @returns {*}
 */
export const addDictType = (params: IDict.DictType) => {
  return http.post(ADMIN_MODULE + `/sys-dict-type`, params);
};

/**
 * 修改字典类型
 * @param params
 * @returns {*}
 */
export const editDictType = (params: IDict.DictType) => {
  return http.put(ADMIN_MODULE + `/sys-dict-type`, params);
};

/**
 * 删除字典类型
 * @param params
 * @returns {*}
 */
export const deleteDictType = (params: { ids: number[] }) => {
  return http.delete(ADMIN_MODULE + `/sys-dict-type`, params);
};

/**
 * 获取字典数据列表
 * @param params
 * @returns {*}
 */
export const getDictData = (params: IDict.DictQuery) => {
  return http.get<IPage<IDict.Dict>>(ADMIN_MODULE + `/sys-dict`, params);
};

/**
 * 添加字典类别
 * @param params
 * @returns {*}
 */
export const addDictData = (params: IDict.Dict) => {
  return http.post(ADMIN_MODULE + `/sys-dict`, params);
};

/**
 * 修改字典类型
 * @param params
 * @returns {*}
 */
export const editDictData = (params: IDict.Dict) => {
  return http.put(ADMIN_MODULE + `/sys-dict`, params);
};

/**
 * 删除字典
 * @param params
 * @returns {*}
 */
export const deleteDictData = (params: { ids: number[] }) => {
  return http.delete(ADMIN_MODULE + `/sys-dict`, params);
};

/**
 * 获取所有字典信息
 * @returns {*}
 */
export const getAllDict = () => {
  return http.get<Record<string, IDict.DictCustom[]>>(ADMIN_MODULE + `/sys-dict/dict`, {}, { loading: false });
};

export const getDictTypeOptions = () => {
  return http.get<IDict.DictType[]>(ADMIN_MODULE + `/sys-dict-type/selectOptionsType`);
};

/**
 * 导出字典sql
 * @param params
 */
export const exportDictSql = (params: { ids: number[] }) => {
  return http.post<string>(ADMIN_MODULE + `/sys-dict/sql/export`, params);
};

/**
 * 获取指定字典
 * @param params
 * @returns {*}
 */
export const getDictByCode = (params: { typeCode: string[] }) => {
  const searchParams = new URLSearchParams();
  params.typeCode.forEach(code => searchParams.append('typeCode', code));
  return http.get<Record<string, IDict.DictCustom[]>>(ADMIN_MODULE + `/sys-dict/code?${searchParams.toString()}`);
};

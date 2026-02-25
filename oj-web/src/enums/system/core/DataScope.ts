

import type { BaseEnum } from '@/enums/base';

// 数据范围枚举接口
interface DataScopeEnum extends BaseEnum {
  fieldName: string;
}

/**
 * 数据范围枚举
 * 用于标识不同的数据访问范围
 */
export const DataScope: { [key: string]: DataScopeEnum } = {
  /**
   * 所有
   */
  ALL: {
    code: 'All',
    name: '所有',
    fieldName: ''
  },

  /**
   * 本部门及下级部门
   */
  DEPT_AND_CHILDREN: {
    code: 'DeptAndChildren',
    name: '本部门及下级部门',
    fieldName: 'dept_id'
  },

  /**
   * 本部门
   */
  DEPT: {
    code: 'Dept',
    name: '本部门',
    fieldName: 'dept_id'
  },

  /**
   * 本人创建
   */
  USER_CREATE: {
    code: 'UserCreate',
    name: '本人创建',
    fieldName: 'create_id'
  },

  /**
   * 加入的班级
   */
  JOIN_GROUP: {
    code: 'JoinGroup',
    name: '加入的班级',
    fieldName: 'group_id'
  }
};

/**
 * 根据code获取枚举
 * @param code
 * @returns 枚举值或null
 */
export function matchCode(code: string): DataScopeEnum | null {
  return Object.values(DataScope).find(item => item.code === code) || null;
}
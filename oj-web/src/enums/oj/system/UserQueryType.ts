// Dept("Dept", "部门"),
// Group("Group", "班级"),
// Tag("Tag", "标签"),
import type { BaseEnum } from '@/enums/base';

// 用户查询类型枚举接口
interface UserQueryTypeEnum extends BaseEnum {
  tooltip?: string;
  text?: string;
}

/**
 * 用户查询类型枚举
 * 用于标识不同的用户查询类型
 */
export const UserQueryType: { [key: string]: UserQueryTypeEnum } = {
  /**
   * 部门查询
   */
  DEPT: {
    code: 'Dept',
    name: '部门',
    tooltip: '按部门查询',
    text: '按部门查询用户'
  },

  /**
   * 班级查询
   */
  GROUP: {
    code: 'Group',
    name: '班级',
    tooltip: '按班级查询',
    text: '按班级查询用户'
  },

  /**
   * 标签查询
   */
  TAG: {
    code: 'Tag',
    name: '标签',
    tooltip: '按标签查询',
    text: '按标签查询用户'
  }
};

/**
 * 根据code获取枚举
 * @param code 用户查询类型代码
 * @returns 枚举值或null
 */
export function matchCode(code: string): UserQueryTypeEnum | null {
  return Object.values(UserQueryType).find(item => item.code === code) || null;
}

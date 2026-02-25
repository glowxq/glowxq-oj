import type { BaseEnum } from '@/enums/base';

// 权限类型枚举接口
interface AuthTypeEnum extends BaseEnum {
  tooltip?: string;
  text?: string;
}

/**
 * 权限类型枚举
 * 用于标识不同的访问权限类型
 */
export const AuthType: { [key: string]: AuthTypeEnum } = {
  /**
   * 公开
   */
  PUBLIC: {
    code: 0,
    name: '公开',
    tooltip: '所有用户可访问',
    text: '任何用户均可访问此资源'
  },

  /**
   * 私有
   */
  PRIVATE: {
    code: 1,
    name: '私有',
    tooltip: '仅创建者可访问',
    text: '仅资源创建者可以访问'
  },

  /**
   * 受保护
   */
  PROTECTED: {
    code: 2,
    name: '受保护',
    tooltip: '需要密码访问',
    text: '需要输入正确的密码才能访问'
  },

  /**
   * 群组
   */
  GROUP: {
    code: 3,
    name: '群组',
    tooltip: '所属群组成员可访问',
    text: '仅所属群组的成员可以访问'
  }
};

/**
 * 根据code获取枚举
 * @param code 
 * @returns 枚举值或null
 */
export function matchCode(code: number): AuthTypeEnum | null {
  return Object.values(AuthType).find(item => item.code === code) || null;
} 
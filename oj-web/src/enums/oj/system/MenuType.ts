import type { BaseEnum } from '@/enums/base';

// 菜单类型枚举接口
interface MenuTypeEnum extends BaseEnum {
  tooltip?: string;
  text?: string;
}

/**
 * 菜单类型枚举
 * 用于标识不同的菜单类型
 */
export const MenuType: { [key: string]: MenuTypeEnum } = {
  /**
   * 客户端菜单
   */
  CLIENT: {
    code: 'Client',
    name: '客户端',
    tooltip: '客户端菜单',
    text: '在客户端前台显示的菜单'
  },

  /**
   * 管理后台菜单
   */
  ADMIN: {
    code: 'Admin',
    name: '管理端',
    tooltip: '管理端菜单',
    text: '在管理后台显示的菜单'
  },

  /**
   * 通用菜单
   */
  COMMON: {
    code: 'Common',
    name: '通用',
    tooltip: '通用菜单',
    text: '在客户端和管理端都显示的菜单'
  }
};

/**
 * 根据code获取枚举
 * @param code 菜单类型代码
 * @returns 枚举值或null
 */
export function matchCode(code: string): MenuTypeEnum | null {
  return Object.values(MenuType).find(item => item.code === code) || null;
}


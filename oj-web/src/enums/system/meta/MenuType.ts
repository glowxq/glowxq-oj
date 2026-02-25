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
   * 底部菜单
   */
  BOTTOM_MENU: {
    code: 'BottomMenu',
    name: '底部菜单',
    tooltip: '页面底部菜单',
    text: '页面底部菜单'
  },

  /**
   * 顶部菜单
   */
  TOP_MENU: {
    code: 'TopMenu',
    name: '顶部菜单',
    tooltip: '页面顶部菜单',
    text: '页面顶部菜单'
  },

  /**
   * 侧边菜单
   */
  SIDE_MENU: {
    code: 'SideMenu',
    name: '侧边菜单',
    tooltip: '页面侧边菜单',
    text: '页面侧边菜单'
  }
};

/**
 * 根据code获取枚举
 * @param code
 * @returns 枚举值或null
 */
export function matchCode(code: string): MenuTypeEnum | null {
  return Object.values(MenuType).find(item => item.code === code) || null;
} 